package com.wideo.metrics.bo.processor.key;

import java.util.List;

import com.wideo.metrics.model.processor.key.PendingJobKey;
import com.wideo.metrics.model.processor.key.ProcessingKey;

public interface StatsPendingJobsBO {

    public List<PendingJobKey> getPendingJobsForKey(String processingKeyName);

    public List<PendingJobKey> getAllPendingJobs();
    
    public void addPendingJob(PendingJobKey pendingJobKey);
    
}
