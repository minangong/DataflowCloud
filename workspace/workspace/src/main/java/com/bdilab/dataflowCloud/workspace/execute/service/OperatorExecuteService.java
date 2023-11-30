package com.bdilab.dataflowCloud.workspace.execute.service;


import com.bdilab.dataflowCloud.workspace.dag.pojo.DagNode;
import org.springframework.web.bind.annotation.RequestParam;

public interface OperatorExecuteService {

    void executeDag(String workspaceId);

    void executeDagNode(String workspaceId, DagNode dagNode);

}
