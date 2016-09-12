package com.wideo.metrics.model.event;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import com.wideo.metrics.events.EventNamespaceTypesEnum;
import com.wideo.metrics.events.EventPropertyTypesEnum;

public class GenericStatsEvent extends BasicDBObject implements TrackeableEvent {
    
    private static final long serialVersionUID = 1L;
    
    String namespace;
    String wideoID;
    String action;
    Long userID;
    String count;
    Date date;
    Map<String, Object> properties;
    
    public GenericStatsEvent(String eventObject) {
        BasicDBObject dbObject  = (BasicDBObject) JSON.parse(eventObject);
        this.putAll(dbObject.toMap());
    }

    public String getNamespace() {
        return (String) this.get(EventPropertyTypesEnum.namespace.getName());
    }
    
    public void setNamespace(String namespace) {
        this.put(EventPropertyTypesEnum.namespace.getName(), namespace);
    }
    
    public String getWideoID() {
        return (String) this.get(EventPropertyTypesEnum.wideo_id.getName());
    }

    public void setWideoID(String wideoID) {
        this.put(EventPropertyTypesEnum.wideo_id.getName(), wideoID);
    }

    public String getAction() {
        return (String) this.get(EventPropertyTypesEnum.action.getName());
    }

    public void setAction(String action) {
        this.put(EventPropertyTypesEnum.action.getName(), action);
    }

    public Long getUserID() {
        return (Long) this.get(EventPropertyTypesEnum.user_id.getName());
    }

    public void setUserID(Long userID) {
        this.put(EventPropertyTypesEnum.namespace.getName(), userID);
    }

    public String getCount() {
        return (String) this.get(EventPropertyTypesEnum.count.getName());
    }

    public void setCount(String count) {
        this.put(EventPropertyTypesEnum.count.getName(), namespace);
    }

    public Date getDate() {
        return (Date) this.get(EventPropertyTypesEnum.date.getName());
    }

    public void setDate(Date date) {
        this.put(EventPropertyTypesEnum.date.getName(), date);
    }
    
    public Map<String, Object> getProperties() {
        return (Map<String, Object>) this.get(EventPropertyTypesEnum.properties.getName());
    }
    
    public void setProperties(Map<String, Object> properties) {
        this.put(EventPropertyTypesEnum.properties.getName(), properties);
    }
    
    public BasicDBObject getBasicDBObject() {
        BasicDBObject newBasicDBObject = new BasicDBObject();
        newBasicDBObject.put("namespace", getNamespace());
        newBasicDBObject.put("action", getAction());
        newBasicDBObject.put("properties", getProperties());
        newBasicDBObject.put("date", getDate());
        newBasicDBObject.put("wideo_id", getWideoID());
        newBasicDBObject.put("user_id", getUserID());
        return newBasicDBObject;
    } 

}
