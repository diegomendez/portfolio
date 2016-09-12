package com.wideo.metrics.bo.processor.task.response;

public class UnknownTaskErrorResponse extends ErrorTaskResponse {

    
    public UnknownTaskErrorResponse(Object data) {
        super(TaskResponseEnum.UNKNOWN_TASK_ERROR.getCode(), TaskResponseEnum.UNKNOWN_TASK_ERROR.getMsg(), data);
    }
    
}
