package com.sh.fbs.basic.commom.filters;

import com.sh.fbs.commom.result.BizBaseErrorCode;
import com.sh.fbs.commom.result.BizException;
import com.sh.fbs.commom.result.Result;
import com.sh.fbs.commom.result.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalServletExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result> handleException(Exception e) {
        Result result = ResultUtils.buildResult(BizBaseErrorCode.FAILED, null);
        if (e instanceof BizException) {
            result = ResultUtils.buildResult(((BizException) e).getECode(), e.getMessage(), null);
        }
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}