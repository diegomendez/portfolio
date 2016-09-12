package com.wideo.metrics.model.processor.key;

import java.io.Serializable;
import java.util.Date;

import org.bson.types.ObjectId;
import org.json.JSONObject;

import com.github.jmkgreen.morphia.annotations.Entity;
import com.github.jmkgreen.morphia.annotations.Id;

@Entity(value = "keys", noClassnameStored = true)
public class ProcessingKey extends Key {

    @Id
    private ObjectId id;
    private String name;
    private String processingClass;
    private String processingCollection;
    private String processingAction;
    private Date lastDateProcessed;
    private int batchInHours;

    public ProcessingKey() {

    }

    public ProcessingKey(String name, String processingAction,
            String processingClass, String processingCollection,
            Date lastDateProcessed, Integer batchInHours) {
        super();
        this.id = new ObjectId();
        this.name = name;
        this.processingAction = processingAction;
        this.processingClass = processingClass;
        this.lastDateProcessed = lastDateProcessed;
        this.batchInHours = batchInHours;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProcessingAction() {
        return processingAction;
    }

    public void setProcessingKey(String processingAction) {
        this.processingAction = processingAction;
    }

    public String getProcessingClass() {
        return processingClass;
    }

    public void setProcessingClass(String processingClass) {
        this.processingClass = processingClass;
    }

    public Date getLastDateProcessed() {
        return lastDateProcessed;
    }

    public void setLastDateProcessed(Date lastDateProcessed) {
        this.lastDateProcessed = lastDateProcessed;
    }

    public int getBatchInHours() {
        return batchInHours;
    }

    public void setBatchInHours(int batchInHours) {
        this.batchInHours = batchInHours;
    }

    public String getProcessingCollection() {
        return processingCollection;
    }

    public void setProcessingCollection(String processingCollection) {
        this.processingCollection = processingCollection;
    }

    @Override
    public String toString() {
        return "ProcessingKey [id=" + id + ", name=" + name
                + ", processingClass=" + processingClass
                + ", processingCollection=" + processingCollection
                + ", processingAction=" + processingAction
                + ", lastDateProcessed=" + lastDateProcessed
                + ", batchInHours=" + batchInHours + "]";
    }
    
}
