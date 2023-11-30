package com.bdilab.dataflowCloud.workspace.execute.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.bdilab.dataflowCloud.workspace.dag.service.WebSocketResolveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * WebSocketServer.
 *
 * @author wjh
 */
@Slf4j
@Service
@ServerEndpoint("/webSocket/{workspaceId}")
// 设置为原型作用域也不能解决依赖无法注入的问题，可能 Tomcat 在 Spring 容器之外手动创建了实例。。
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class WebSocketServer {
  private final static Integer lock = 0;
  private static WebSocketResolveService socketResolveService;
  private static final AtomicInteger onlineCount = new AtomicInteger(0);
  private static final ConcurrentHashMap<String, HashSet<Session>> sessionSet = new ConcurrentHashMap<>(128);

  public WebSocketServer() {

  }

  /**
   * Send to all sessions.
   *
   * @param message The message to send.
   */
  public static void sendMessage(String workspaceId, String message) {
    if (!sessionSet.containsKey(workspaceId)) {
      return;
    }
    for (Session session : sessionSet.get(workspaceId)) {
      synchronized (session) {
        try {
          session.getBasicRemote().sendText(String.format("%s", message));
        }
        catch (Exception e) {
          log.error(
              "{} send message to session of workspace: {}, session: {}", e.getClass().getSimpleName(), workspaceId,
              session.getId()
          );
          synchronized (sessionSet.get(workspaceId)) {
            sessionSet.get(workspaceId).remove(session);
          }
        }
      }
    }
    if (message.length() > 300) {
      message = message.substring(0, 300);
    }
    //log.info("Send message to session of workspace: {}. The message: {}", workspaceId, message);
  }

  /**
   * Relay messages to all sessions except sendSession under workspaceId.
   *
   * @param workspaceId workspaceId.
   * @param message     The message to send.
   * @param sessionIds  The sendSession id list.
   */
  public static void relayMessage(String workspaceId, String message, List<String> sessionIds) {
    if (sessionIds.size() != 2) {
      return;
    }
    Session session1 = null;
    try {
      for (Session session : sessionSet.get(workspaceId)) {
        session1 = session;
        if (sessionIds.contains(session.getId())) {
          continue;
        }
        synchronized (session) {
          session.getBasicRemote().sendText(String.format("%s", message));
        }
      }
      if (message.length() > 300) {
        message = message.substring(0, 300);
      }
      log.info(
          "Relay message to session of workspace: {} . Except sessions {}. The message: {}", workspaceId, sessionIds,
          message
      );
    }
    catch (Exception e) {
      log.error("Exception when relay message to session-{}:\n{}", session1 == null ? null : session1.getId(), message);
      e.printStackTrace();
    }
  }

  /**
   * Send to one session.
   *
   * @param message The message to send.
   * @param session The session to send.
   */
  public static void sendMessageP2P(String message, Session session) {
    try {
      synchronized (session) {
        session.getBasicRemote().sendText(String.format("%s", message));
      }
    }
    catch (IOException e) {
      log.error("IOException when sending message to session{}:", session.getId());
      e.printStackTrace();
    }
  }

  public static void setSocketResolveService(ApplicationContext context) {
    socketResolveService = context.getBean(WebSocketResolveService.class);
  }

  public static List<String> getNowWorkSpace() {
    List<String> res = new ArrayList<>();
//    sessionSet.forEach((k, v) -> res.add(k));
    sessionSet.forEachKey(1, res::add);
    return res;
  }

  /**
   * Rabbitmq consumer
   *
   *
   */
//  @RabbitListener(bindings = @QueueBinding(
//          value = @Queue(value = "userTest-queue",durable = "true"),
//          exchange = @Exchange(name = "userTest-exchange",durable = "true",type = "direct"),
//          key = "userTest-key"
//  ))
//  @RabbitHandler
//  public void receiveMessage(@Payload String outputJson, @Headers Map<String,Object> headers, Channel channel) throws IOException {
//    BaseOutputJson baseOutputJson = JSON.parseObject(outputJson, BaseOutputJson.class);
//    String workspaceId = baseOutputJson.getWorkspaceId();
//    log.info("Rabbitmq: "+outputJson);
//    sendMessage(workspaceId,outputJson);
//    //sendMessage(workspaceId, jobOutputJson.toString());
//    Long deliveryTag = (Long)headers.get(AmqpHeaders.DELIVERY_TAG);
//    channel.basicAck(deliveryTag,false);
//  }

  /**
   * Connection establishment.
   *
   * @param session The current session.
   */
  @OnOpen
  public void onOpen(@PathParam("workspaceId") String workspaceId, Session session) {
    // 重新加个mysql中表加载到redis。注意clickhouse中节点输出临时表这时候是没的，应该等前端渲染完再发执行所有头结点任务.
    // 如果想仅在后端测试，可以打开openWorkspace中的“后端执行所有头结点任务功能”.
//    workspaceService.openWorkspace(workspaceId);
    session.setMaxIdleTimeout(3600000);

    synchronized (lock) {// 这里是为了和onClose删除时的读写安全,可以讨论一下。
      sessionSet.putIfAbsent(workspaceId, new HashSet<>());
      sessionSet.get(workspaceId).add(session);
    }

    log.info("工作区id列表：" + sessionSet.get(workspaceId).toString());

    // 建立连接时给前端推送当前连接sessionId
    try {
      Map<String, Object> map = new HashMap<>();
      map.put("sessionId", session.getId());
      String msg = JSON.toJSONString(map);
      session.getBasicRemote().sendText(String.format("%s", msg));
    }
    catch (IOException e) {
      throw new RuntimeException(e.getMessage());
    }
    onlineCount.incrementAndGet();

    log.info("Session [{}] has connected to workspace [{}]. " +
             "The size of this workspace is [{}]. " +
             "The total size of session is [{}].",
        session.getId(), workspaceId, sessionSet.get(workspaceId).size(), onlineCount
    );
  }

  /**
   * Close connection.
   *
   * @param session The current session.
   */
  @OnClose
  public void onClose(Session session) {
    Map<String, String> pathParameters = session.getPathParameters();
    String workspaceId = pathParameters.get("workspaceId");
    HashSet<Session> sessions = sessionSet.get(workspaceId);
    if (sessions == null) {
      return;
    }
    sessions.remove(session);
    if (sessionSet.get(workspaceId).isEmpty()) {
      synchronized (lock) {
        if (sessionSet.get(workspaceId).isEmpty()) {
          sessionSet.remove(workspaceId);
//          workspaceService.closeWorkspace(workspaceId);
        }
      }
    }

    onlineCount.decrementAndGet();
    log.info("Session [{}] has closed, which workspace is [{}]. The total size of session is [{}].",
        session.getId(), workspaceId, onlineCount
    );
  }

  /**
   * Receive messages.
   *
   * @param message The message to receive.
   * @param session The current session.
   */
  @OnMessage
  public void onMessage(String message, Session session) {
//    try {
//      // System.out.println("onMessage" + message);
//      Map<String, String> pathParameters = session.getPathParameters();
//      session.getRequestParameterMap();
//      socketResolveService.resolve(message, session);
//    }
//    catch (JSONException e) {
//      log.info("WebSocket test : " + message);
//    }
  }

  /**
   * Error occurred.
   *
   * @param session The current session.
   * @param error   The error.
   */
  @OnError
  public void onError(Session session, Throwable error) {
    log.error("Session [{}] ERROR: {}", session, error.getMessage());
    error.printStackTrace();
  }
}