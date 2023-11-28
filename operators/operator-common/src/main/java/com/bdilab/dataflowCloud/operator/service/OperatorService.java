package com.bdilab.dataflowCloud.operator.service;


import com.alibaba.fastjson.JSONObject;
import com.bdilab.dataflowCloud.operator.dto.jobOutputData.OperatorOutputData;
import com.bdilab.dataflowCloud.operator.dto.jobdescription.JobDescription;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface OperatorService<T extends JobDescription> {

    /**
     * linkage: 1. save the result to ClickHouse.
     *          2. return the result (if...exist).
     *
     * @param jobDescription job Description
     * @param extendMessage extend message from dag
     * @author wh
     * @date 2022-04-30
     */
    OperatorOutputData executeOperator(JSONObject jobDescription, List<Object> extendMessage) throws Exception;

}
