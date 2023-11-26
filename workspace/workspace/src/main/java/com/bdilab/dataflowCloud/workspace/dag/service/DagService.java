package com.bdilab.dataflowCloud.workspace.dag.service;


import com.bdilab.dataflowCloud.workspace.dag.pojo.Dag;
import com.bdilab.dataflowCloud.workspace.dag.pojo.DagNode;
import com.bdilab.dataflowCloud.workspace.dag.pojo.DagNodeBuilder;

/**
 * Only operate the dag and partial filter-records operations in Redis.
 *
 * @author wh
 * @date 2021/4/9
 */
public interface DagService {
  DagNode getNode(String workspaceId, String nodeId);

  Dag getDag(String workspaceId);

  boolean addNode(String workspaceId, DagNodeBuilder dagNodeBuilder);

  boolean addEdge(String workspaceId, String preNodeId, String nextNodeId, Integer slotIndex);

  boolean removeNode(String workspaceId, String deletedNodeId);

  boolean removeEdge(String workspaceId, String preNodeId, String nextNodeId, Integer slotIndex);

  boolean updateNode(String workspaceId, String nodeId, Object nodeDescription);

}
