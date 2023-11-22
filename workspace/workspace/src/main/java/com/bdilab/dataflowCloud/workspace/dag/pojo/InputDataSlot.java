package com.bdilab.dataflowCloud.workspace.dag.pojo;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The input data-slot of node.
 *
 * @author wh
 * @date 2021/11/16
 */
@Data
public class InputDataSlot {
  private String dataSource;
  private String preNodeId;


  /**
   * For fastjson serialize.
   */
  public InputDataSlot() {
  }

  public InputDataSlot(String dataSource) {
    this.dataSource = dataSource;
  }

  public InputDataSlot(String dataSource, String preNodeId) {
    this.dataSource = dataSource;
    this.preNodeId = preNodeId;
  }
}
