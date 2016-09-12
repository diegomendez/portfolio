package com.wideo.metrics.bo.processor.key;

import java.util.Date;
import java.util.List;

import com.wideo.metrics.model.processor.key.ProcessingKey;

public interface StatsProcessingKeysBO {

    public List<ProcessingKey> getAllProcessingKeys();
    
    public ProcessingKey getProcessingKey(String name);
    
    public void addProcessingKey(ProcessingKey processingKey);
    
    public void updateLastProcessedDateForKey(String processingKeyName, Date newLastProcessedDate);
    
}
