package com.bdilab.dataflowCloud.resourceManager.utils;

//import com.baomidou.dynamic.datasource.annotation.DS;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * ClickHouse Jdbc Utils.
 *
 * @author wh
 * @version 1.0
 * @date 2021/09/13
 */
@Slf4j
@Repository
public class ClickHouseJdbcUtils {
  @Resource(name = "clickHouseJdbcTemplate")
  JdbcTemplate ckJdbcTemplate;

  public List<Map<String, Object>> queryForList(String sql) {
    return ckJdbcTemplate.queryForList(sql);
  }


  public void execute(String sql) {
    log.info("Executing clickhouse sql statement: {}", sql);
    ckJdbcTemplate.execute(sql);
  }

  public Integer queryForInteger(String sql) {
    return ckJdbcTemplate.queryForObject(sql,Integer.class);
  }

  public Map<String,String>  getTableMatadata(String tableName){
    String databaseName;
    String tableN;
    if (tableName.contains(".")){
      String[] strs = tableName.split("\\.");
      databaseName = strs[0];
      tableN = strs[1];
    }else{
      databaseName = "dataflow";
      tableN = tableName;
    }
    String sql = "select name,type from system.columns where database=\'"+databaseName+ "\' and table=\'"+tableN+"\' and name not like \'_airbyte%\';";
    List<Map<String,Object>> nameAndType = ckJdbcTemplate.queryForList(sql);

    Map<String,String> result = new HashMap<>();
    for(Map<String,Object> map : nameAndType) {
      result.put(map.get("name").toString(), map.get("type").toString());
    }
    return result;
  }
  public void createView(String viewName, String selectSql) {
    StringBuilder sql = new StringBuilder();
    sql.append("DROP VIEW IF EXISTS ").append(viewName).append(";");
    sql.append("CREATE VIEW ").append(viewName).append(" AS ")
            .append("(").append(selectSql).append(");");
    ckJdbcTemplate.execute(new String(sql));
  }
  /**
   * Query for chart.
   */
  public List<?> query(String sql) {
    String limitSql = sql + " limit 1";
    List<Map<String, Object>> mapList = this.queryForList(limitSql);
    Object o = null;
    for (String key : mapList.get(0).keySet()) {
      o = mapList.get(0).get(key);
    }
    assert o != null;
    return ckJdbcTemplate.queryForList(sql, o.getClass());
  }

  public Long getCount(String sql) {
    String countSql = "select count(*) from (" + sql + ")";
    return ckJdbcTemplate.queryForObject(countSql, Long.class);
  }

}
