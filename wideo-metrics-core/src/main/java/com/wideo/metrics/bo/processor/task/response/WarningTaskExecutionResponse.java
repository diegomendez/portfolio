package com.wideo.metrics.bo.processor.task.response;

public class WarningTaskExecutionResponse extends TaskResponse {

    public WarningTaskExecutionResponse() {
        super(TaskResponseEnum.WARNING_TASK_RESPONSE.getCode(),
                TaskResponseEnum.WARNING_TASK_RESPONSE.getMsg());
    }
}
