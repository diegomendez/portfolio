package com.wideo.metrics.persistence.dal.interfaces;

import java.util.Date;
import java.util.List;

import com.wideo.metrics.model.processor.key.ProcessingKey;

public interface StatsProcessingKeysDalInterface {

    public List<ProcessingKey> getAllProcessingKeys();
    
    public ProcessingKey getProcessingKey(String name);

    public void addProcessingKey(ProcessingKey processingKey);

    public void updateLastProcessedDateForKey(String processingKeyName,
            Date newLastProcessedDate);
        
}
