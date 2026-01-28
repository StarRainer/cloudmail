package com.rainer.cloudmall.search.exception;

import com.rainer.cloudmall.common.exception.BaseException;
import com.rainer.cloudmall.common.exception.code.CommonCode;
import com.rainer.cloudmall.common.exception.code.ProductResponseCode;
import com.rainer.cloudmall.common.utils.FeignResult;
import com.rainer.cloudmall.common.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public FeignResult<ProductResponseCode> baseException(BaseException e) {
        log.error(e.getMessage(), e);
        return FeignResult.failure(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public FeignResult<Void> handleException(Exception e) {
        log.error("系统出现未知异常{}", e.getMessage(), e);
        return FeignResult.failure(CommonCode.UNKNOWN_ERROR);
    }
}

