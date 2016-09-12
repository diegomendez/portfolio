package com.wideo.metrics.event;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.mongodb.BasicDBObject;
import com.wideo.metrics.events.EventNamespaceTypesEnum;

public class GenericStatsEventDTO {
    
    String namespace;
    String wideoID;
    String action;
    Long userID;
    Integer count;
    Date date;
    Map<String, Object> properties;
    
    public GenericStatsEventDTO(String action, String wideoID, Long userID, Integer count, Date date) {
        this.namespace = EventNamespaceTypesEnum.social.getName();
        this.wideoID = wideoID;
        this.action = action;
        this.userID = userID;
        this.count = count;
        this.date = date;
        this.properties = new HashMap<String, Object>();
    }
    
    public String getNamespace() {
        return this.namespace;
    }
    
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
    
    public String getWideoID() {
        return wideoID;
    }

    public void setWideoID(String wideoID) {
        this.wideoID = wideoID;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    
    public Map<String, Object> getProperties() {
        return properties;
    }
    
    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public void addProperty(String key, Object value) {
        this.properties.put(key, value);
    }
    
    public BasicDBObject getBasicDBObject() {
        BasicDBObject newBasicDBObject = new BasicDBObject();
        newBasicDBObject.put("namespace", this.namespace);
        newBasicDBObject.put("action", this.action);
        newBasicDBObject.put("properties", this.properties);
        newBasicDBObject.put("date", this.date);
        newBasicDBObject.put("wideo_id", this.wideoID);
        newBasicDBObject.put("user_id", this.userID);
        newBasicDBObject.put("views", count);
        return newBasicDBObject;
    } 

}
