package com.bdilab.dataflowCloud.workspace.workspace.common.response;

import io.swagger.annotations.ApiModelProperty;

/**
 * http状态码
 **/
public enum HttpCode {
    /**
     * 请求成功，返回状态码200
     */
    OK(200, "请求成功", true),
    /**
     * 参数错误，请求失败，返回状态码1001
     */
    PARAMETER_ERROR(1001, "参数错误", false),
    /**
     * 请求未授权，请求失败，返回状态码1002
     */
    UNAUTHORIZED(1002, "请求未授权", false),
    /**
     * 请求被禁止，请求失败，返回状态码1003
     */
    FORBIDDEN(1003, "请求被禁止", false),
    /**
     * 内部参数错误，请求失败，返回状态码1004
     */
    INTERNAL_PARAMETER_ERROR(1004, "内部参数错误", false),
    /**
     * 资源不存在，请求失败，返回状态码404
     */
    NOT_FOUND(404, "资源不存在", false),
    /**
     * 资源不存在，请求失败，返回状态码404
     */
    PERMISSION_DENIED(403, "权限不足", false),
    /**
     * 请求失败，返回状态码300
     */
    FAIL_REQUEST(300, "请求失败", false),
    /**
     * 请求中部分执行失败
     */
    NOT_SUCCESS_COMPLETELY(1005, "部分请求执行失败",false),

    NOT_SUCCESS_MUTISQL(1005, "检测到多行SQL语句!: 一次只能输入一行以';'结尾的SQL语句",false),


    TOKEN_ERROR(4010001,"用户未登录，请重新登录",false),
    TOKEN_NOT_NULL(4010001,"token不能为空",false),
    ACCOUNT_HAS_DELETED_ERROR(4010001,"该账号已被删除，请联系系统管理员",false),
    ACCOUNT_LOCK(4010001,"该账号被锁定,请联系系统管理员",false),
    TOKEN_PAST_DUE(4010002,"token失效,请刷新token",false);

    @ApiModelProperty(value = "状态码", required = true)
    private int code;
    @ApiModelProperty(value = "状态信息", required = true)
    private String message;
    @ApiModelProperty(value = "请求是否成功", required = true)
    private boolean successful;

    HttpCode(int code, String message, boolean successful) {
        this.code = code;
        this.message = message;
        this.successful = successful;
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
}
