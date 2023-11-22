package com.bdilab.dataflowCloud.operator.dto.jobOutputData;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * description: Results.
 *
 * @author zhb
 */

@Data
public class OutputData {
  private List<Map<String, Object>> data;
  private Map<String, String> metadata;
  private String chartType;
  private Integer count;

  public OutputData() {}

  public OutputData(List<Map<String, Object>> data, Map<String, String> metadata) {
    this.data = data;
    this.metadata = metadata;
  }

  public OutputData(List<Map<String, Object>> data, Map<String, String> metadata, String chartType) {
    this.data = data;
    this.metadata = metadata;
    this.chartType = chartType;
  }

  public OutputData(List<Map<String, Object>> data, Map<String, String> metadata, Integer count) {
    this.data = data;
    this.metadata = metadata;
    this.count = count;
  }
}
