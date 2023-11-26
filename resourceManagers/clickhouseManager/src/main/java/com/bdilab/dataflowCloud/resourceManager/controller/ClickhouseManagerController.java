package com.bdilab.dataflowCloud.resourceManager.controller;


import com.bdilab.dataflowCloud.resourceManager.utils.ClickHouseJdbcUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/dataflowCloud/resourceManager/clickhouse")
public class ClickhouseManagerController {

    @Autowired
    private ClickHouseJdbcUtils clickHouseJdbcUtils;


    @PostMapping("/queryForList")
    public List<Map<String, Object>> queryForList(String sql) {
        return clickHouseJdbcUtils.queryForList(sql);
    }

    @PostMapping("/createView")
    public void createView(String viewName, String selectSql){
        clickHouseJdbcUtils.createView(viewName,selectSql);
    }


    @PostMapping("/queryForInteger")
    public Integer queryForInteger(String sql,Class c) {
        return clickHouseJdbcUtils.queryForInteger(sql);
    }

    @PostMapping("/getTableMatadata")
    public Map<String,String>  getTableMatadata(String tableName){
        return clickHouseJdbcUtils.getTableMatadata(tableName);
    }

    @PostMapping("/batchDeleteViews")
    public boolean batchDeleteViews(List<String> viewNames){
        return clickHouseJdbcUtils.batchDeleteViews(viewNames);

    }
}
