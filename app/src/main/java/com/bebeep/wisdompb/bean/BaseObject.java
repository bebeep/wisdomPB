package com.bebeep.wisdompb.bean;

import com.bebeep.wisdompb.MyApplication;

public class BaseObject<T> {
    private boolean success;
    private int errorCode;
    private String msg;
    private T data;


    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
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


    @Override
    public String toString() {
        return MyApplication.gson.toJson(this);
    }
}
