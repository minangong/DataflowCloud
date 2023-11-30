package com.bdilab.dataflowCloud.workspace.execute.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.bdilab.dataflowCloud.workspace.dag.pojo.Dag;
import com.bdilab.dataflowCloud.workspace.dag.pojo.DagNode;
import com.bdilab.dataflowCloud.workspace.dag.service.DagService;
import com.bdilab.dataflowCloud.workspace.execute.service.OperatorExecuteService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
public class OperatorExecuteServiceImpl implements OperatorExecuteService {


    @Autowired
    DagService dagService;

    @Autowired
    RocketMQTemplate rocketMQTemplate;


    @Override
    public void executeDag(String workspaceId) {
        Dag dag = dagService.getDag(workspaceId);
        List<DagNode> startNodes = dag.getAlwaysSuccessNodes();
        for(DagNode node : startNodes){
            executeDagNode(workspaceId,node);
        }
    }


    @Override
    public void executeDagNode(String workspaceId, DagNode dagNode) {
        log.info("执行工区为"+workspaceId+"的操作符 " + dagNode.getNodeId());
        if(dagNode.isFailed() || dagNode.isWait()){
            return;
        }
        if(dagNode.isAlwaysSuccess()){
            Dag dag = dagService.getDag(workspaceId);
            List<DagNode> nextNodes = dag.getNextNodes(dagNode.getNodeId());
            for(DagNode nextNode:nextNodes){
                executeDagNode(workspaceId,nextNode);
            }
        }
        if(dagNode.isReady()){
            String topic = "operatorExecute";
            String tag = dagNode.getNodeType()+"Service";
            String message = getNodeExecuteMessage(workspaceId,dagNode);
            rocketMQTemplate.sendOneWay(topic+":"+tag,message);
        }
    }

    public String getNodeExecuteMessage(String workspaceId,DagNode dagNode){

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("workspaceId",workspaceId);
        jsonObject.put("operatorId",dagNode.getNodeId());
        jsonObject.put("NodeDescription",dagNode.getNodeDescription());
        return jsonObject.toJSONString();
    }
}
