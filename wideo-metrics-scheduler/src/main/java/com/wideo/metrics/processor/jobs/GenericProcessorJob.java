package com.wideo.metrics.processor.jobs;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.wideo.metrics.bo.processor.StatsThreadJobsManager;
import com.wideo.metrics.bo.processor.task.response.TaskResponse;

public class GenericProcessorJob extends QuartzJobBean {

 private static final Logger LOGGER = Logger.getLogger(GenericProcessorJob.class);
    
    StatsThreadJobsManager statsThreadJobsManager;
    String processingKeyName;
    
    @Override
    protected void executeInternal(JobExecutionContext arg0)
            throws JobExecutionException {
        LOGGER.info("Starting job for key "+ processingKeyName);
        TaskResponse petitionResponse = statsThreadJobsManager.performPetition(processingKeyName);        
        LOGGER.info(petitionResponse.toString());
    }
    
    @Required
    public void setStatsThreadJobsManager(StatsThreadJobsManager statsThreadJobsManager) {
        this.statsThreadJobsManager = statsThreadJobsManager;
    }

    @Required
    public void setProcessingKeyName(String processingKeyName) {
        this.processingKeyName = processingKeyName;
    }
}
