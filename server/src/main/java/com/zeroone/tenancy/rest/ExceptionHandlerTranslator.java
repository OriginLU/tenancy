package com.zeroone.tenancy.rest;

import com.zeroone.tenancy.dto.RestResult;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;


@RestControllerAdvice
public class ExceptionHandlerTranslator {

    private static final Logger log = LoggerFactory.getLogger(ExceptionHandlerTranslator.class);


    /**
     * 全局异常拦截器
     */
    @ExceptionHandler({BindException.class, MethodArgumentNotValidException.class})
    public RestResult<Void> bindExceptionHandler(Exception e) {
        String errorMessage ="入参错误[" +  validateMessage(getAllErrors(e)) + "]";
        log.error(errorMessage, e);
        return RestResult.returnFailure(errorMessage);
    }


    private String validateMessage(List<ObjectError> errors){

        if (CollectionUtils.isEmpty(errors)){
            return "";
        }
        return errors.stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining(","));
    }

    /**
     * 全局异常拦截器
     */
    @ExceptionHandler(Exception.class)
    public RestResult<Void> exceptionHandler(Exception e) {
        log.error(e.getMessage(), e);
        return RestResult.returnFailure(ExceptionUtils.getMessage(e));
    }


    private List<ObjectError> getAllErrors(Exception e) {

        if (e instanceof BindException) {
            return ((BindException) e).getAllErrors();
        } else {
            return ((MethodArgumentNotValidException) e).getBindingResult().getAllErrors();
        }
    }
}
