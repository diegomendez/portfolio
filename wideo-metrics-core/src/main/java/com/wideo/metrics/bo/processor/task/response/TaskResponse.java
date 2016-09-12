package com.wideo.metrics.bo.processor.task.response;

import java.io.Serializable;

public class TaskResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private int code;
    private String msg;

    public TaskResponse(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "TaskResponse [Code: " + this.code + ", Msg: " + this.msg + "]";
    }
}
