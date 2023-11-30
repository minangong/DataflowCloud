package com.bdilab.dataflowCloud.workspace.execute.controller;


import com.bdilab.dataflowCloud.workspace.dag.consts.WebConstants;
import com.bdilab.dataflowCloud.workspace.dag.pojo.Dag;
import com.bdilab.dataflowCloud.workspace.dag.pojo.DagNode;
import com.bdilab.dataflowCloud.workspace.dag.service.DagService;
import com.bdilab.dataflowCloud.workspace.execute.service.OperatorExecuteService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@CrossOrigin
@RequestMapping(value = WebConstants.BASE_API_PATH + "/workspace")

public class OperatorExecuteController {



    @Autowired
    OperatorExecuteService operatorExecuteService;

    @PostMapping("/executeDag")
    public void executeDag(@RequestParam String workspaceId) {
        log.info("---------------------------------------------");
        log.info("---------------------------------------------");
        operatorExecuteService.executeDag(workspaceId);
    }

}
