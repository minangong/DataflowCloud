package com.bdilab.dataflowCloud.workspace.dag.service.impl;



import com.alibaba.fastjson.JSONObject;
import com.bdilab.dataflowCloud.workspace.dag.enums.DagNodeState;
import com.bdilab.dataflowCloud.workspace.dag.pojo.*;
import com.bdilab.dataflowCloud.workspace.dag.service.DagService;
import com.bdilab.dataflowCloud.workspace.dag.utils.redis.RedisUtils;
import com.bdilab.dataflowCloud.workspace.execute.service.DataSetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
 */
@Slf4j
@Service
public class DagServiceImpl implements DagService {
  @Resource
  RedisUtils redisUtils;
  @Autowired
  DataSetService dataSetService;

  @Override
  public boolean addNode(String workspaceId, DagNodeBuilder dagNodeBuilder) {
    DagNode dagNode = dagNodeBuilder.build();
    if(dagNode.getNodeState().equals(DagNodeState.ALWAYS_SUCCEED)){
      dataSetService.createAlwaysSuccessView(dagNode.getInputDataSource(0),dagNode.getNodeDataResult());
    }
    //log.info(dagNode.toString());
    redisUtils.hset(workspaceId,dagNode.getNodeId(),dagNode);
    log.info("Add node [{}] to [{}].", dagNode.getNodeId(), workspaceId);
    return true;
  }

  @Override
  public boolean removeNode(String workspaceId, String deletedNodeId) {
    Dag dag = getDag(workspaceId);
    DagNode deletedNode = dag.getDagNode(deletedNodeId);
    //修改操作符状态 递归修改为Wait态
    dag.getDagStateProxy().changeWaitState(deletedNode);
    //删除前节点的next信息
    for (int i = 0;  i < deletedNode.getInputDataSlots().length; i++) {
      InputDataSlot inputDataSlot = deletedNode.getInputDataSlots()[i];
      String preNodeId = inputDataSlot.getPreNodeId();
      OutputDataSlot deletedSlot = new OutputDataSlot(deletedNodeId, i);
      if (!StringUtils.isEmpty(preNodeId)) {
        dag.getDagMap().get(preNodeId).getOutputDataSlots().remove(deletedSlot);
      }
    }
    //删除后置节点的pre信息
    for (OutputDataSlot outputDataSlot : deletedNode.getOutputDataSlots()) {
      DagNode nextNode = dag.getDagNode(outputDataSlot.getNextNodeId());
      nextNode.setPreNodeId(outputDataSlot.getNextSlotIndex(), null);
      nextNode.setDataSource(outputDataSlot.getNextSlotIndex(), null);
    }
    //删除节点输出表！！！！
    dataSetService.clearDagNodeView(deletedNode.getNodeDataResult());


    //删除节点
    dag.getDagMap().remove(deletedNodeId);


    //更新redis
    redisUtils.hdel(workspaceId, deletedNodeId);
    redisUtils.hmset(workspaceId,dag.getDagMap());
    log.info("Remove node [{}] in [{}].", deletedNodeId, workspaceId);
    return true;
  }

  @Override
  public boolean updateNode(String workspaceId, String nodeId, Object nodeDescription) {
    Dag dag = getDag(workspaceId);
    DagNode dagNode = dag.getDagNode(nodeId);
    dagNode.setNodeDescription(nodeDescription);

    //修改操作符状态 递归修改为Wait态   首节点判断是否Ready
    dag.getDagStateProxy().changeWaitState(dagNode);
    dag.getDagStateProxy().checkIfReady(dagNode);

    //更新redis
    redisUtils.hmset(workspaceId,dag.getDagMap());
    log.info("Update node [{}] in [{}]", nodeId, workspaceId);
    return true;
  }
  @Override
  public boolean addEdge(String workspaceId, String preNodeId, String nextNodeId, Integer slotIndex) {
    Dag dag = getDag(workspaceId);
    DagNode preNode = dag.getDagNode(preNodeId);
    DagNode nextNode = dag.getDagNode(nextNodeId);

    if(preNode.getOutputDataSlots().contains(new OutputDataSlot(nextNodeId,slotIndex))){
      return true;
    }
    //增加边信息
    preNode.getOutputDataSlots().add(new OutputDataSlot(nextNodeId, slotIndex));
    nextNode.setPreNodeId(slotIndex, preNodeId);
    nextNode.setDataSource(slotIndex,preNode.getNodeDataResult());

    //修改操作符状态 递归修改为Wait态   首节点判断是否Ready
    dag.getDagStateProxy().changeWaitState(nextNode);
    dag.getDagStateProxy().checkIfReady(nextNode);

    //更新redis
    redisUtils.hmset(workspaceId,dag.getDagMap());

    log.info("Add edge between [{}] to slot [{}] of [{}] in [{}].",
        preNodeId, slotIndex, nextNodeId, workspaceId);

    return true;
  }


  @Override
  public boolean removeEdge(String workspaceId,
                        String preNodeId,
                        String nextNodeId,
                        Integer slotIndex) {

    Dag dag = getDag(workspaceId);
    DagNode preNode = dag.getDagNode(preNodeId);
    DagNode nextNode = dag.getDagNode(nextNodeId);

    preNode.getOutputDataSlots().remove(new OutputDataSlot(nextNodeId, slotIndex));
    nextNode.setPreNodeId(slotIndex, null);
    nextNode.setDataSource(slotIndex, null);

    //修改操作符状态 递归修改为Wait态   首节点判断是否Ready
    dag.getDagStateProxy().changeWaitState(nextNode);

    //更新redis
    redisUtils.hmset(workspaceId,dag.getDagMap());

    log.info("remove edge between [{}] to slot [{}] of [{}] in [{}].",
        preNodeId, slotIndex, nextNodeId, workspaceId);
    return true;
  }


  @Override
  public DagNode getNode(String workspaceId, String nodeId) {
    return DagNodeBuilder.convertNode(redisUtils.hget(workspaceId, nodeId));
  }
  @Override
  public Dag getDag(String workspaceId) {
		return new Dag((redisUtils.hmget(workspaceId)));
  }

}
