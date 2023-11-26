package com.bdilab.dataflowcloud.operator.controller;


import com.alibaba.fastjson.JSONObject;
import com.bdilab.dataflowCloud.operator.dto.jobOutputData.OperatorOutputData;
import com.bdilab.dataflowcloud.operator.service.OperatorTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/dataflowCloud/operators/table")
public class TableController {
    @Autowired
    OperatorTableService operatorTableService;


    @PostMapping("/execute")
    public OperatorOutputData executeOperator(@RequestParam("jobDescription") String jobDescription,
                                              @RequestParam("saveTableName") String saveTableName
                                              ) throws Exception{

        return operatorTableService.executeOperator(JSONObject.parseObject(jobDescription), saveTableName, new ArrayList<Object>());
    }
}
