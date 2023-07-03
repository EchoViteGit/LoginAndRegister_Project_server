package com.example.separate.server.entity;

import lombok.Data;

/**
 * Created with IntelliJ IDEA
 *
 * @author 郭宏洋
 * @version 1.0.0
 * @DateTime 2023/7/3 21:09
 */
@Data
public class RestBean<T> {
    private Integer status;
    private boolean success;
    private T message;

    public RestBean() {
    }

    public RestBean(Integer status, boolean success, T message) {
        this.status = status;
        this.success = success;
        this.message = message;
    }

    public static<T> RestBean<T> success(){
        return new RestBean<>(200,true,null);
    }

    public static<T> RestBean<T> success(T data){
        return new RestBean<>(200,true,data);
    }

    public static<T> RestBean<T> failure(int status){
        return new RestBean<>(status,false,null);
    }

    public static<T> RestBean<T> failure(int status,T data){
        return new RestBean<>(status,false,data);
    }
}
