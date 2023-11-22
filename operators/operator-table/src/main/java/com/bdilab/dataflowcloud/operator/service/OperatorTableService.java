package com.bdilab.dataflowcloud.operator.service;


import com.alibaba.fastjson.JSONObject;
import com.bdilab.dataflowCloud.operator.dto.jobOutputData.OperatorOutputData;
import com.bdilab.dataflowCloud.operator.dto.jobOutputData.OutputData;
import com.bdilab.dataflowCloud.operator.dto.jobOutputData.OutputStateEnum;
import com.bdilab.dataflowCloud.operator.service.OperatorService;
import com.bdilab.dataflowcloud.operator.client.ClickhouseManagerClient;
import com.bdilab.dataflowcloud.operator.dto.jobdescription.TableDescription;
import com.bdilab.dataflowcloud.operator.generator.TableSqlGenerator;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;


@Service
@Slf4j
public class OperatorTableService implements OperatorService<TableDescription> {
    @Autowired
    ClickhouseManagerClient clickhouseManagerClient;

    @Override
    public OperatorOutputData executeOperator(JSONObject jobDescription, String saveTableName, List<Object> extendMessage) throws Exception {

        TableDescription tableDescription = jobDescription.toJavaObject(TableDescription.class);
        // 将计算结果保存到ClickHouse
        TableSqlGenerator tableSqlGenerator = new TableSqlGenerator(tableDescription);

        String sql = tableSqlGenerator.generateDataSourceSql();

        clickhouseManagerClient.createView(saveTableName,sql);
        String tableSelectSql = tableSqlGenerator.generateLimit(saveTableName);
        List<Map<String, Object>> data = clickhouseManagerClient.queryForList(tableSelectSql);
        Integer count = clickhouseManagerClient.queryForInteger(tableSqlGenerator.selectCount(saveTableName));

        OutputData outputData = new OutputData(data, clickhouseManagerClient.getTableMatadata(saveTableName), count);
        return new OperatorOutputData(OutputStateEnum.SUCCESS).setOutputData(outputData);
    }
}
