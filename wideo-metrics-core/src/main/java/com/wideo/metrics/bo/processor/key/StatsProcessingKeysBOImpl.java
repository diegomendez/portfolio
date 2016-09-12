package com.wideo.metrics.bo.processor.key;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.wideo.metrics.model.processor.key.ProcessingKey;
import com.wideo.metrics.persistence.dal.StatsProcessingKeysDalImpl;
import com.wideo.metrics.persistence.dal.interfaces.StatsProcessingKeysDalInterface;

@Service
@ComponentScan("com.wideo.metrics")
public class StatsProcessingKeysBOImpl implements StatsProcessingKeysBO {

    @Autowired
    StatsProcessingKeysDalImpl statsProcessingKeysDalImpl;
    
    public List<ProcessingKey> getAllProcessingKeys() {
        return statsProcessingKeysDalImpl.getAllProcessingKeys();
    }
    
    public ProcessingKey getProcessingKey(String name) {
        return statsProcessingKeysDalImpl.getProcessingKey(name);
    }

    public void addProcessingKey(ProcessingKey processingKey) {
        statsProcessingKeysDalImpl.addProcessingKey(processingKey);
    }
    
    public void updateLastProcessedDateForKey(String processingKeyName, Date newLastProcessedDate) {
        statsProcessingKeysDalImpl.updateLastProcessedDateForKey(processingKeyName, newLastProcessedDate);
    }

}
