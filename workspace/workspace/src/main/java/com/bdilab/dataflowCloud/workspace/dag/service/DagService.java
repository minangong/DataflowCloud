package com.bdilab.dataflowCloud.workspace.dag.service;


import com.bdilab.dataflowCloud.workspace.dag.dto.DagNodeInputDto;
import com.bdilab.dataflowCloud.workspace.dag.pojo.Dag;
import com.bdilab.dataflowCloud.workspace.dag.pojo.DagNode;

/**
 * Only operate the dag and partial filter-records operations in Redis.
 *
 * @author wh
 * @date 2021/4/9
 */
public interface DagService {
  DagNode getNode(String workspaceId, String nodeId);

  Dag getDag(String workspaceId);

  Dag addNode(String workspaceId, DagNodeInputDto dagNodeInputDto);

  Dag addEdge(String workspaceId, String preNodeId, String nextNodeId, Integer slotIndex);

  Dag removeNode(String workspaceId, String deletedNodeId);

  Dag removeEdge(String workspaceId, String preNodeId, String nextNodeId, Integer slotIndex);

  Dag updateNode(String workspaceId, String nodeId, Object nodeDescription);

  Dag updateEdge(String workspaceId, String preNodeId, String nextNodeId,
                 Integer slotIndex, String edgeType);

}
