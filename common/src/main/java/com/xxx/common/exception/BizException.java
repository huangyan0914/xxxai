package com.xxx.common.exception;

import com.xxx.common.resp.RespCode;

/**
 * 业务异常，配合全局异常处理器统一返回前端
 */
public class BizException extends RuntimeException {

    private final String code;

    public BizException(String message) {
        super(message);
        this.code = RespCode.FAIL.getCode();
    }

    public BizException(RespCode respCode) {
        super(respCode.getMsg());
        this.code = respCode.getCode();
    }

    public BizException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}


