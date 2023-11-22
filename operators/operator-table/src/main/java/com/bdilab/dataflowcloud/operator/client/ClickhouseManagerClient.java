package com.bdilab.dataflowcloud.operator.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient("clickhouseManagerService")
@RequestMapping("/dataflowCloud/resourceManager/clickhouse")
public interface ClickhouseManagerClient {
    @PostMapping("/queryForList")
    public List<Map<String, Object>> queryForList(@RequestParam String sql);

    @PostMapping("/createView")
    public void createView(@RequestParam String viewName, @RequestParam String selectSql);

    @PostMapping("/queryForInteger")
    public Integer queryForInteger(@RequestParam String sql);

    @PostMapping("/getTableMatadata")
    public Map<String,String>  getTableMatadata(@RequestParam String tableName);
}
