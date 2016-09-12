package com.wideo.metrics.bo.processor.task.response;

public class ProcessingKeyUnknownException extends TaskResponse {

    public ProcessingKeyUnknownException() {
        super(TaskResponseEnum.UNKNOWN_PROCESSING_KEY_ERROR.getCode(),
                TaskResponseEnum.UNKNOWN_PROCESSING_KEY_ERROR.getMsg());
    }
}
