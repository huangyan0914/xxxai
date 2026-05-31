package com.xxx.common.exception;

import com.xxx.common.resp.Resp;
import com.xxx.common.resp.RespCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

/**
 * 全局异常处理，所有业务模块引入 common 即可生效
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BizException.class)
    public Resp<Void> handleBizException(BizException e) {
        log.warn("业务异常: {}", e.getMessage());
        return Resp.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            BindException.class,
            ConstraintViolationException.class,
            MissingServletRequestParameterException.class,
            HttpMessageNotReadableException.class
    })
    public Resp<Void> handleValidationException(Exception e) {
        log.warn("参数校验异常", e);
        return Resp.fail(RespCode.FAIL.getCode(), "请求参数不合法");
    }

    @ExceptionHandler(Exception.class)
    public Resp<Void> handleOtherException(Exception e) {
        log.error("系统异常", e);
        return Resp.fail(RespCode.ERROR);
    }
}


