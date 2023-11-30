package com.bdilab.dataflowCloud.workspace.dag.pojo;

import com.bdilab.dataflowCloud.workspace.dag.enums.DagNodeState;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class DagStateProxy {
  private final Dag dag;

  public DagStateProxy (Dag dag) {
    this.dag = dag;
  }


  public Dag checkIfReady(DagNode node){
    if(node.getNodeState().isReady()){
      return dag;
    }
    List<DagNode> preNodes = dag.getPreNodes(node.getNodeId());

    for(DagNode preNode : preNodes){
      if(!preNode.isSuccess()){
        node.setNodeState(DagNodeState.WAIT);
        return dag;
      }
    }
    node.setNodeState(DagNodeState.READY);
    return dag;
  }

  public Dag changeWaitState(DagNode node){
    changeWaitStateRecursively(node);
    return dag;
  }

  public void changeWaitStateRecursively(DagNode node){
    if(node == null)
    {
      return;
    }
    node.setNodeState(DagNodeState.WAIT) ;
    for(DagNode nextnode : dag.getNextNodes(node.nodeId)){
      changeWaitStateRecursively(nextnode);
    }
  }


}