package com.wideo.metrics.bo.processor.task.response;

public class DateNotLivedException extends TaskResponse {

    public DateNotLivedException() {
        super(TaskResponseEnum.NOT_LIVED_DATE_ERROR.getCode(),
                TaskResponseEnum.NOT_LIVED_DATE_ERROR.getMsg());
    }
}
