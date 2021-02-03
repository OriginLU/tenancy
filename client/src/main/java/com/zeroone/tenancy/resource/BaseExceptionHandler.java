package com.zeroone.tenancy.resource;


import com.google.common.base.Throwables;
import com.zeroone.tenancy.dto.RestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;



public class BaseExceptionHandler {

    protected final Logger log = LoggerFactory.getLogger(BaseExceptionHandler.class);


    /**
     * 业务异常拦截器
     *
     */
    @ExceptionHandler(IllegalStateException.class)
    public RestResult<Void> bizExceptionHandler(IllegalStateException e) {
        log.error(e.getMessage(), e);
        return RestResult.returnFailure(e.getMessage());
    }



    /**
     * 全局异常拦截器
     */
    @ExceptionHandler({BindException.class, MethodArgumentNotValidException.class})
    public RestResult<Void> bindExceptionHandler(Exception e) {
        String errorMessage ="入参错误[" +  validateMessage(getAllErrors(e)) + "]";
        log.error(errorMessage, e);
        return RestResult.returnFailure(errorMessage);
    }




    /**
     * 全局异常拦截器
     */
    @ExceptionHandler(Exception.class)
    public RestResult<Void> exceptionHandler(Exception e) {
        log.error(e.getMessage(), e);
        return RestResult.returnError(getCauseString(e));
    }

    private String getCauseString(Exception e) {
        return Throwables.getRootCause(e).getMessage();
    }


    private String validateMessage(List<ObjectError> errors){

        if (CollectionUtils.isEmpty(errors)){
            return "";
        }
        return errors.stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining(","));
    }

    private List<ObjectError> getAllErrors(Exception e) {

        if (e instanceof BindException) {
            return ((BindException) e).getAllErrors();
        } else {
            return ((MethodArgumentNotValidException) e).getBindingResult().getAllErrors();
        }
    }
}
