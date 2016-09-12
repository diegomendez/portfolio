package com.wideo.metrics.bo.processor.task.response;

public class MapReduceMongoErrorResponse extends ErrorTaskResponse {
    
    public MapReduceMongoErrorResponse(Object data) {
        super(TaskResponseEnum.MONGO_MAPREDUCE_ERROR.getCode(), TaskResponseEnum.MONGO_MAPREDUCE_ERROR.getMsg(), data);
    }
    
}
