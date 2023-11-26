package com.bdilab.dataflowCloud.workspace.workspace.common.response;

import io.swagger.annotations.ApiModelProperty;

public class GenericHttpResponse<T> {
  @ApiModelProperty(value = "状态码", required = true)
  private int code;
  @ApiModelProperty(value = "状态信息", required = true)
  private String message;
  @ApiModelProperty(value = "请求是否成功", required = true)
  private boolean successful;
  @ApiModelProperty(value = "展示数据", required = true)
  private T data;

  public GenericHttpResponse(){}

  public GenericHttpResponse(HttpCode httpCode, T data) {
    this.code = httpCode.getCode();
    this.message = httpCode.getMessage();
    this.successful = httpCode.isSuccessful();
    this.data = data;
  }

  public GenericHttpResponse(int code,String message,boolean successful,T data){
    this.code = code;
    this.message = message;
    this.successful = successful;
    this.data = data;
  }


  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public boolean isSuccessful() {
    return successful;
  }

  public void setSuccessful(boolean successful) {
    this.successful = successful;
  }

  public T getData() {
    return data;
  }

  public void setData(T data) {
    this.data = data;
  }

}
