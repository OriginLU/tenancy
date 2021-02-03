package com.zeroone.tenancy.dto;

import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;


@Data
@ToString
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Generated
public class RestResult<T> implements Serializable {


    private static final long serialVersionUID = 896100912835032882L;

    public static final int ERROR = -1;


    public static final int SUCCESS = 0;


    public static final int FAILURE = 9999;

    /**
     * 响应码
     */
    private int code;
    /**
     * 错误信息
     */
    private String message;

    /**w
     * 返回体
     */
    private T data;


    public boolean isSuccess(){
        return code == SUCCESS;
    }


    public static RestResult<Void> returnError(String message){
        return new RestResult<>(ERROR,message,null);
    }

    public static RestResult<Void> returnFailure(String message){
        return new RestResult<>(FAILURE,message,null);
    }

    public static RestResult<Void> returnFailure(int code,String message){
        return new RestResult<>(code,message,null);
    }

    public static <T> RestResult<T> returnSuccess(T data){
        return new RestResult<>(SUCCESS,"success",data);
    }

    public static <T> RestResult<T> returnSuccess(){
        return new RestResult<>(SUCCESS,"success",null);
    }
}
