package com.wideo.metrics.bo.processor.task.response;

public class ErrorTaskResponse extends TaskResponse {

    Object data;

    public ErrorTaskResponse(Integer code, String msg, Object data) {
        super(code, msg);
        this.data = data;
    }
    
    public ErrorTaskResponse(Integer code, String msg) {
        super(code, msg);
        this.data = "";
    }
    
    public ErrorTaskResponse(String msg) {
        super(8, msg);
        this.data = "";
    }

    public Object getData() {
        return this.data;
    }

    @Override
    public String toString() {
        return "[code: " + getCode() + " ,msg: " + getMsg() + ", data: "
                + data.toString();
    }
}
