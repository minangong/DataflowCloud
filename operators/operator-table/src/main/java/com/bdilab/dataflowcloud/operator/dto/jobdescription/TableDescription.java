package com.bdilab.dataflowcloud.operator.dto.jobdescription;

import com.alibaba.fastjson.JSONObject;
import com.bdilab.dataflowCloud.operator.dto.jobdescription.JobDescription;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TableDescription.
 *
 * @author: Zunjing Chen
 * @create: 2021-09-18
 */
@Data
@NoArgsConstructor
public class TableDescription extends JobDescription {
  private String filter;
  private String[] project;
  private String[] group;
  private int page = 1;

  /**
   * All args constructor.
   *
   */
  public TableDescription(String dataSource,String nodeDataResult,String filter, String[] project, String[] group) {
    super(new String[]{dataSource},nodeDataResult);
    this.filter = filter;
    this.project = project;
    this.group = group;
  }

  /**
   * Generate TableDescription from json object.
   */
  public static TableDescription generateFromJson(JSONObject json) {
    String filter = json.getString("filter");
    String[] project = json.getJSONArray("project").toArray(new String[]{});
    String[] group = json.getJSONArray("group").toArray(new String[]{});
    String dataSource = json.getString("dataSource");
    String nodeDataResult = json.getString("nodeDataResult");
    return new TableDescription(dataSource,nodeDataResult,filter, project, group);
  }
}
