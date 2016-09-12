package com.wideo.metrics.bo.processor;

import com.mongodb.CommandResult;
import com.mongodb.DBObject;
import com.wideo.metrics.bo.processor.task.response.TaskResponse;

public interface StatsProcessorCallable {

    public TaskResponse collectAndProcess();
    
}
