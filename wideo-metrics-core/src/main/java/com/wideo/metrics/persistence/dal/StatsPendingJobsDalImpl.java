package com.wideo.metrics.persistence.dal;

import java.util.List;

import com.github.jmkgreen.morphia.Morphia;
import com.github.jmkgreen.morphia.dao.BasicDAO;
import com.github.jmkgreen.morphia.query.Query;
import com.mongodb.Mongo;
import com.wideo.metrics.model.processor.key.PendingJobKey;
import com.wideo.metrics.persistence.dal.interfaces.StatsPendingProcessesDalInterface;

public class StatsPendingJobsDalImpl extends
        BasicDAO<PendingJobKey, String> implements StatsPendingProcessesDalInterface {

    public StatsPendingJobsDalImpl(Mongo mongo, Morphia morphia,
            String dbName) {
        super(mongo, morphia, dbName);
    }
    
    public List<PendingJobKey> getAllPendingJobs() {
        Query<PendingJobKey> q = ds.find(PendingJobKey.class);
        return q.asList();
    }

    public List<PendingJobKey> getPendingJobsForKey(String processingKeyName) {
        Query<PendingJobKey> q = ds.find(PendingJobKey.class).field("processingKeyName").equal(processingKeyName);
        return q.asList();
    }

    public void addPendingJob(PendingJobKey pendingJobKey) {
        ds.save(pendingJobKey);
    }

}
