package com.bdilab.dataflowCloud.workspace.execute.controller;


import com.bdilab.dataflowCloud.workspace.dag.consts.WebConstants;
import com.bdilab.dataflowCloud.workspace.dag.pojo.Dag;
import com.bdilab.dataflowCloud.workspace.dag.pojo.DagNode;
import com.bdilab.dataflowCloud.workspace.dag.service.DagService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@CrossOrigin
@RequestMapping(value = WebConstants.BASE_API_PATH + "/excute/")

public class OperatorExecuteController {
    @Autowired
    DagService dagService;

    @Autowired
    RocketMQTemplate rocketMQTemplate;

    @PostMapping("/DagNode")
    public void executeDagNode(@RequestParam String workspaceId,
                              @RequestParam String operatorId) {
        Dag dag = dagService.getDag(workspaceId);
        DagNode dagNode = dag.getDagNode(operatorId);

        String topic = "operatorExecute";
        String tag = dagNode.getNodeType()+"Service";

        String message = workspaceId+"#"+operatorId+"#"+dagNode.getNodeDescription();
        rocketMQTemplate.sendOneWay(topic+":"+tag,message);
    }


}
