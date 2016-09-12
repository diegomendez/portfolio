package com.wideo.metrics.bo.processor.key;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import com.wideo.metrics.model.processor.key.PendingJobKey;
import com.wideo.metrics.persistence.dal.StatsPendingJobsDalImpl;

@Service
@ComponentScan("com.wideo.metrics")
public class StatsPendingJobsBOImpl implements StatsPendingJobsBO {

    @Autowired
    StatsPendingJobsDalImpl statsPendingJobsDalImpl;

    public List<PendingJobKey> getPendingJobsForKey(String processingKeyName) {
        return statsPendingJobsDalImpl
                .getPendingJobsForKey(processingKeyName);
    }

    public List<PendingJobKey> getAllPendingJobs() {
        return statsPendingJobsDalImpl.getAllPendingJobs();
    }

    public void addPendingJob(PendingJobKey pendingJobKey) {
        statsPendingJobsDalImpl.addPendingJob(pendingJobKey);
    }

}
