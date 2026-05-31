package com.xxx.common.resp;

/**
 * 全局响应码枚举，可按需扩展
 */
public enum RespCode {

    SUCCESS("0", "成功"),
    FAIL("1", "失败"),
    UNAUTHORIZED("401", "未授权"),
    FORBIDDEN("403", "无权限"),
    NOT_FOUND("404", "资源不存在"),
    ERROR("500", "服务器异常");

    private final String code;
    private final String msg;

    RespCode(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}

