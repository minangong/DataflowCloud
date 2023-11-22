package com.bdilab.dataflowCloud.operator.dto.jobOutputData;

/**
 * 操作符运行结果状态，自行添加
 *
 * @author wh
 * @date 2021/05/05
 */
public enum OutputStateEnum {
  SUCCESS("success", "success"),

  /**
   * 内部错误，无法运行，一般用于不返回前端结果。
   */
  RUN_FAILED("fail_not_return", "操作符内部错误"),

  /**
   * 操作符参数不够，无法运行，一般用于不返回前端结果。
   */
  NOT_READY("fail_not_return", "操作符参数不足"),

  /**
   * 操作符参数错误，无法运行，一般用于返回前端提示信息。
   */
  PARAMETER_ERROR("fail_return", "操作符参数错误"),

  CANCELLED_ERROR("job_cancelled", "操作符运行被取消");


  private final String type;
  private final String info;

  OutputStateEnum(String type, String info) {
    this.type = type;
    this.info = info;
  }

  public String getType() {
    return type;
  }

  public String getInfo() {
    return info;
  }
}
