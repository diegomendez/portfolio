package com.wideo.metrics.model.processor.key;

import java.util.Date;

import org.bson.types.ObjectId;

import com.github.jmkgreen.morphia.annotations.Entity;
import com.github.jmkgreen.morphia.annotations.Id;

@Entity(value = "pendingjobs", noClassnameStored  = true)
public class PendingJobKey extends Key {

    @Id
    private ObjectId id;
    private String processingKeyName;
    private Date startDate;
    private Date toDate;
    
    public PendingJobKey(String processingKeyName, Date startDate, Date toDate) {
        super();
        this.id = new ObjectId();
        this.processingKeyName = processingKeyName;
        this.startDate = startDate;
        this.toDate = toDate;
    }
    
    public String getProcessingKeyName() {
        return processingKeyName;
    }
    public void setProcessingKeyName(String processingKeyName) {
        this.processingKeyName = processingKeyName;
    }
    public Date getStartDate() {
        return startDate;
    }
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    public Date getToDate() {
        return toDate;
    }
    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }
    
}
