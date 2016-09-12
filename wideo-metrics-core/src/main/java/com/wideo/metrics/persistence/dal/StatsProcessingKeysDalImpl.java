package com.wideo.metrics.persistence.dal;

import java.security.interfaces.DSAKey;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.github.jmkgreen.morphia.Morphia;
import com.github.jmkgreen.morphia.dao.BasicDAO;
import com.github.jmkgreen.morphia.query.Query;
import com.github.jmkgreen.morphia.query.UpdateOperations;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.wideo.metrics.exceptions.InvalidProcessingKeyException;
import com.wideo.metrics.model.processor.key.ProcessingKey;
import com.wideo.metrics.persistence.dal.interfaces.StatsProcessingKeysDalInterface;

@Service
@ComponentScan("com.wideo.metrics")
public class StatsProcessingKeysDalImpl extends BasicDAO<ProcessingKey, String>
        implements StatsProcessingKeysDalInterface {

    public StatsProcessingKeysDalImpl(Mongo mongo, Morphia morphia,
            String dbName) {
        super(mongo, morphia, dbName);
    }

    public List<ProcessingKey> getAllProcessingKeys() {
        Query<ProcessingKey> processingKeyByNameQuery = ds.find(ProcessingKey.class);
    
        List<ProcessingKey> results = processingKeyByNameQuery.asList();

        return results;
    }
    
    public ProcessingKey getProcessingKey(String name) {
        Query<ProcessingKey> processingKeyByNameQuery = ds.find(ProcessingKey.class)
                .field("name").equal(name);
        
        List<ProcessingKey> results = processingKeyByNameQuery.asList();
        
        ProcessingKey processingKeyByNameResult = processingKeyByNameQuery.get();
        if (processingKeyByNameResult != null) {
            return processingKeyByNameResult;
        }
        else {
            throw new InvalidProcessingKeyException("No existe una processingKey con el nombre " + name);
        }
    }

    public void addProcessingKey(ProcessingKey processingKey) {
        ds.save(processingKey);
    }

    public void updateLastProcessedDateForKey(String processingKeyName,
            Date newLastProcessedDate) {
        UpdateOperations<ProcessingKey> updateQuery = ds
                .createUpdateOperations(ProcessingKey.class).set(
                        "lastDateProcessed", newLastProcessedDate);

        
        Query<ProcessingKey> query = ds.find(ProcessingKey.class)
              .field("name").equal(processingKeyName);
        
        ds.update(query, updateQuery, true);
    }
    
}
