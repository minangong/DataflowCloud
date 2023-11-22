package com.bdilab.dataflowCloud.workspace.service.impl;



import com.bdilab.dataflowCloud.workspace.dag.pojo.Dag;
import com.bdilab.dataflowCloud.workspace.dag.service.DagService;
import com.bdilab.dataflowCloud.workspace.dag.utils.redis.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Operate dag services in Redis.
 * When you add code for dag reading or writing in Redis, note the following :
 * 1. Only reading redis once does not require a lock.
 * 2. Only writing redis once does not require a lock.
 * However,
 * 3. When you need to read a DAG from Redis, modify it, and finally write back to redis,
 *    remember to add @ApplyDagLock above the method. Also, minimize lock granularity to
 *    optimize performance.
 * 4. If there are two writes back to redis in your method, be sure to keep the two writes
 *    back atomic, The solution is to put two operations in Redis Transaction.
 *    Eg : DagServiceImpl.removeNode(). Otherwise, reading dag from Redis will result in a
 *    dirty read, which means the read will also be locked !!
 *
 * @author wh
 * @date 2021/4/9
 */
@Slf4j
@Service
public class DagServiceImpl implements DagService {
  @Resource
  RedisUtils redisUtils;


  @Override
  public Dag addNode(String workspaceId, DagNodeInputDto dagNodeInputDto) {
    Dag dag = getDag(workspaceId);
    dag.getDagManipulateProxy().addNode(new DagNode(dagNodeInputDto));
    redisUtils.hmset(workspaceId, dag.getDagMap());
    log.info("Add node [{}] to [{}].", dagNodeInputDto.getNodeId(), workspaceId);
    return dag;
  }

  @Override
  public Dag addEdge(String workspaceId, String preNodeId, String nextNodeId, Integer slotIndex) {
    Dag dag = getDag(workspaceId);
    dag.getDagManipulateProxy().addEdge(preNodeId, nextNodeId, slotIndex, dag);
    redisUtils.hmset(workspaceId, dag.getDagMap());
    log.info("Add edge between [{}] to slot [{}] of [{}] in [{}].",
        preNodeId, slotIndex, nextNodeId, workspaceId);
    return dag;
  }

  @Override
  public Dag removeNode(String workspaceId, String deletedNodeId) {
    Dag dag = getDag(workspaceId);
    dag.getDagManipulateProxy().removeNode(deletedNodeId, dag);
    redisUtils.hdel(workspaceId, deletedNodeId);
    redisUtils.hmset(workspaceId, dag.getDagMap());
    log.info("Remove node [{}] in [{}].", deletedNodeId, workspaceId);
    return dag;
  }

  @Override
  public Dag removeEdge(String workspaceId,
                        String preNodeId,
                        String nextNodeId,
                        Integer slotIndex) {
    Dag dag = getDag(workspaceId);
    dag.getDagManipulateProxy().removeEdge(preNodeId, nextNodeId, slotIndex, dag);
    redisUtils.hmset(workspaceId, dag.getDagMap());
    log.info("Add edge between [{}] to slot [{}] of [{}] in [{}].",
        preNodeId, slotIndex, nextNodeId, workspaceId);
    return dag;
  }

  @Override
  public Dag updateNode(String workspaceId, String nodeId, Object nodeDescription) {
    Dag dag = getDag(workspaceId);
    dag.getDagManipulateProxy().updateNode(nodeId, nodeDescription, dag);
    redisUtils.hmset(workspaceId, dag.getDagMap());
    log.info("Update node [{}] in [{}]", nodeId, workspaceId);
    return dag;
  }

  @Override
  public Dag updateEdge(String workspaceId,
                        String preNodeId,
                        String nextNodeId,
                        Integer slotIndex,
                        String edgeType) {
    Dag dag = getDag(workspaceId);
    dag.getDagManipulateProxy().updateEdge(preNodeId, nextNodeId, slotIndex, edgeType, dag);
    redisUtils.hmset(workspaceId, dag.getDagMap());
    log.info("Update the output edge of [{}] to [{}] in [{}]", preNodeId, edgeType, workspaceId);
    return dag;
  }

  @Override
  public DagNode getNode(String workspaceId, String nodeId) {
    return (DagNode) redisUtils.hget(workspaceId, nodeId);
  }
  @Override
  public Dag getDag(String workspaceId) {
		return new Dag((redisUtils.hmget(workspaceId)));
  }



}
