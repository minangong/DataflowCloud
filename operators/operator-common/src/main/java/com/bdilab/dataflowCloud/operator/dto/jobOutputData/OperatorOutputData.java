package com.bdilab.dataflowCloud.operator.dto.jobOutputData;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 操作符输出结果
 *
 * @author wh
 * @date 2021/05/05
 */
@AllArgsConstructor
@Getter
public class OperatorOutputData {
  /**
   * 状态
   */
  private OutputStateEnum state;

  /**
   * 状态详细信息
   */
  private String stateInfo;

  /**
   * 输出结果，用于发往前端
   */
  private OutputData outputData;

  /**
   * 传递给下一操作符的中间信息
   */
  private Object intermediateResult;

  public OperatorOutputData(OutputStateEnum state) {
    this.state = state;
  }

  public OperatorOutputData(OutputStateEnum state, String stateInfo) {
    this.state = state;
    this.stateInfo = stateInfo;
  }

  public OperatorOutputData(OutputStateEnum state, String stateInfo, OutputData outputData) {
    this.state = state;
    this.stateInfo = stateInfo;
    this.outputData = outputData;
  }

  public OperatorOutputData setState(OutputStateEnum state) {
    this.state = state;
    return this;
  }

  public OperatorOutputData setStateInfo(String stateInfo) {
    this.stateInfo = stateInfo;
    return this;
  }

  public OperatorOutputData setOutputData(OutputData outputData) {
    this.outputData = outputData;
    return this;
  }

  public OperatorOutputData setIntermediateResult(Object intermediateResult) {
    this.intermediateResult = intermediateResult;
    return this;
  }
}
