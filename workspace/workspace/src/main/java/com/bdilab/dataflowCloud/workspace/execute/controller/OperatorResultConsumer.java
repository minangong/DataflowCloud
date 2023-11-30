package com.bdilab.dataflowCloud.workspace.execute.controller;


import com.alibaba.fastjson.JSONObject;
import com.bdilab.dataflowCloud.workspace.dag.enums.DagNodeState;
import com.bdilab.dataflowCloud.workspace.dag.pojo.Dag;
import com.bdilab.dataflowCloud.workspace.dag.pojo.DagNode;
import com.bdilab.dataflowCloud.workspace.dag.service.DagService;
import com.bdilab.dataflowCloud.workspace.dag.utils.redis.RedisUtils;
import com.bdilab.dataflowCloud.workspace.execute.service.OperatorExecuteService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RocketMQMessageListener(topic = "operatorResult",consumerGroup = "operatorResultGroup",
        messageModel = MessageModel.CLUSTERING)
public class OperatorResultConsumer implements RocketMQListener<String> {

    @Autowired
    DagService dagService;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    OperatorExecuteService operatorExecuteService;

    @Override
    public void onMessage(String s) {
        JSONObject result = JSONObject.parseObject(s);
        String workspaceId = result.getString("workspaceId");
        String operatorId = result.getString("operatorId");
        String exeResult = result.getString("result");
        String nodeDataResult = result.getString("nodeDataResult");

        if(exeResult.equals("success")){
            log.info(workspaceId+":操作符"+operatorId+"执行成功");
            Dag dag = dagService.getDag(workspaceId);
            DagNode node = dag.getDagNode(operatorId);
            node.setNodeState(DagNodeState.SUCCEED);
            redisUtils.hset(workspaceId,operatorId,node);

            WebSocketServer.sendMessage(workspaceId,"操作符"+operatorId+"执行成功");
            for (DagNode nextNode : dag.getNextNodes(operatorId)){
                dag.getDagStateProxy().checkIfReady(nextNode);
                if(nextNode.isReady()){
                    log.info(operatorId+"推荐 nextNode "+ nextNode.getNodeId() +"开始运行");
                    operatorExecuteService.executeDagNode(workspaceId,nextNode);
                }
            }
        }else{
            log.info(workspaceId+":操作符"+operatorId+"执行失败");
            DagNode node = dagService.getNode(workspaceId,operatorId);
            node.setNodeState(DagNodeState.FAILED);
            redisUtils.hset(workspaceId,operatorId,node);
            WebSocketServer.sendMessage(workspaceId,"操作符"+operatorId+"执行失败");
        }
    }
}
