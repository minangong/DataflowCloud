package com.bdilab.dataflowcloud.operator.controller;


import com.alibaba.fastjson.JSONObject;
import com.bdilab.dataflowCloud.operator.dto.jobOutputData.OperatorOutputData;
import com.bdilab.dataflowcloud.operator.service.OperatorTableService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;



@Slf4j
@Component
@RocketMQMessageListener(topic = "operatorExecute",consumerGroup = "operatorExecuteGroup",
        selectorExpression = "tableService",
        messageModel = MessageModel.CLUSTERING)
public class TableConsumer implements RocketMQListener<String> {

    @Autowired
    OperatorTableService operatorTableService;

    @Autowired
    RocketMQTemplate rocketMQTemplate;

    @Override
    public void onMessage(String s) {
        JSONObject jsonObject = JSONObject.parseObject(s);

        String workspaceId = jsonObject.getString("workspaceId");
        String operatorId = jsonObject.getString("operatorId");
        String desc = jsonObject.getString("NodeDescription");

        log.info("get desc:  " + desc);
        JSONObject result = new JSONObject();
        result.put("workspaceId",workspaceId);
        result.put("operatorId",operatorId);

        try {
            operatorTableService.executeOperatorNoResponse(JSONObject.parseObject(desc), new ArrayList<Object>());

            result.put("result","success");
            result.put("nodeDataResult",(JSONObject.parseObject(desc)).get("nodeDataResult"));
            rocketMQTemplate.sendOneWay("operatorResult",result.toJSONString());
        }
        catch (Exception e) {
            log.info(e.getMessage());
            result.put("result","failed");
            result.put("nodeDataResult",null);
            rocketMQTemplate.sendOneWay("operatorResult",result.toJSONString());
        }
    }
}
