package com.xxx.common.resp;

import java.io.Serializable;

/**
 * 全局统一返回值封装
 *
 * @param <T> 业务数据泛型
 */
public class Resp<T> implements Serializable {

    private static final long serialVersionUID = -1854616725284151074L;

    private String code;
    private String msg;
    private T data;

    public Resp() {
    }

    public Resp(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> Resp<T> ok(T data) {
        return new Resp<>(RespCode.SUCCESS.getCode(), RespCode.SUCCESS.getMsg(), data);
    }

    public static <T> Resp<T> ok() {
        return ok(null);
    }

    public static <T> Resp<T> fail(String code, String msg) {
        return new Resp<>(code, msg, null);
    }

    public static <T> Resp<T> fail(RespCode respCode) {
        return new Resp<>(respCode.getCode(), respCode.getMsg(), null);
    }

    public static <T> Resp<T> fail(String msg) {
        return fail(RespCode.FAIL.getCode(), msg);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}

