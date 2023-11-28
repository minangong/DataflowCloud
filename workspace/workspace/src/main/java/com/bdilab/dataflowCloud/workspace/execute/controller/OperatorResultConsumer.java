package com.bdilab.dataflowCloud.workspace.execute.controller;


import com.alibaba.fastjson.JSONObject;
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

    @Override
    public void onMessage(String s) {
        JSONObject result = JSONObject.parseObject(s);
        String workspaceId = result.getString("workspaceId");
        String operatorId = result.getString("operatorId");
        String exeResult = result.getString("result");
        String nodeDataResult = result.getString("nodeDataResult");

        if(exeResult.equals("success")){
            WebSocketServer.sendMessage(workspaceId,"操作符"+operatorId+"执行成功");
        }else{
            WebSocketServer.sendMessage(workspaceId,"操作符"+operatorId+"执行失败");
        }
    }
}
