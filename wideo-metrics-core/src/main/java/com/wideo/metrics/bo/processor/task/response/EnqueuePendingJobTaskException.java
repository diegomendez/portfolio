package com.wideo.metrics.bo.processor.task.response;

public class EnqueuePendingJobTaskException extends ErrorTaskResponse {

    
    public EnqueuePendingJobTaskException(Object data) {
        super(TaskResponseEnum.UNKNOWN_TASK_ERROR.getCode(), TaskResponseEnum.UNKNOWN_TASK_ERROR.getMsg(), data);
    }
    
}
