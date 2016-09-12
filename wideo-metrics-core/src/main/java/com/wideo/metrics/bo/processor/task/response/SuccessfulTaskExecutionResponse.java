package com.wideo.metrics.bo.processor.task.response;

public class SuccessfulTaskExecutionResponse extends TaskResponse {

    public SuccessfulTaskExecutionResponse() {
        super(TaskResponseEnum.SUCCESS_TASK_RESPONSE.getCode(), TaskResponseEnum.SUCCESS_TASK_RESPONSE.getMsg());
    }

}
