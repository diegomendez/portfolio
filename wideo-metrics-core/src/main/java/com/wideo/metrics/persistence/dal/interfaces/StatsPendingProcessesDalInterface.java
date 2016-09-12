package com.wideo.metrics.persistence.dal.interfaces;

import java.util.List;

import com.wideo.metrics.model.processor.key.PendingJobKey;
import com.wideo.metrics.model.processor.key.ProcessingKey;

public interface StatsPendingProcessesDalInterface {

    public List<PendingJobKey> getAllPendingJobs();

    public List<PendingJobKey> getPendingJobsForKey(String processingKeyName);

    public void addPendingJob(PendingJobKey pendingJobKey);

}
