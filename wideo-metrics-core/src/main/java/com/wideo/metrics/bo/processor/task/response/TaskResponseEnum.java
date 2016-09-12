package com.wideo.metrics.bo.processor.task.response;

public enum TaskResponseEnum {

    SUCCESS_RESPONSE(0, "success"), SUCCESS_TASK_RESPONSE(1,
            "task execution ran successfully"), WARNING_TASK_RESPONSE(
            2,
            "Petition processed with one or more tasks failed. Please check the pending jobs queue"), UNKNOWN_PROCESSING_KEY_ERROR(
            3, "Unknown Processing Key name"), NOT_LIVED_DATE_ERROR(4,
            "Date selected to process is still future"), MONGO_MAPREDUCE_ERROR(
            5, "Error processing map-reduce"), UNKNOWN_TASK_ERROR(9,
            "Unknown error processing task");

    private int code;
    private String msg;

    private TaskResponseEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }
}
