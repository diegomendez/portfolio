package com.wideo.metrics.model.event;

import java.util.Date;
import java.util.Map;













import org.bson.BSON;
import org.bson.BSONObject;
import org.joda.time.LocalDate;
import org.json.JSONObject;

import com.github.jmkgreen.morphia.Datastore;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import com.wideo.metrics.events.EventPropertyTypesEnum;
import com.wideo.metrics.events.WideoEventTypesEnum;

public class GenericEvent extends BasicDBObject implements TrackeableEvent{
    
    private static final long serialVersionUID = 1L;
    
    public GenericEvent(String eventObject) {
        BasicDBObject dbObject  = (BasicDBObject) JSON.parse(eventObject);
        this.putAll(dbObject.toMap());
        this.put(EventPropertyTypesEnum.date.getName(), new Date());
    }
    
    public GenericEvent(Map<String, Object> eventFields) {
        this.putAll(eventFields);
        this.put(EventPropertyTypesEnum.date.getName(), new Date(System.currentTimeMillis()));
    }
 
    public void setNamespace(String namespace) {
        this.put(EventPropertyTypesEnum.namespace.getName(), namespace);
    }
    
    public String getNamespace() {
        return (String) this.get(EventPropertyTypesEnum.namespace.getName());
    }
    
    public void setAction(String action) {
        this.put(EventPropertyTypesEnum.action.getName(), action);
    }
    
    public String getAction() {
        return (String) this.get(EventPropertyTypesEnum.action.getName());
    }
    
    public void setProperties(Map<String, String> properties) {
        this.put(EventPropertyTypesEnum.properties.getName(), properties);
    }
    
    public Map<String, Object> getProperties() {
        return (Map<String, Object>) this.get(EventPropertyTypesEnum.properties.getName());
    }
    
    public void setDate(Date date) {
        this.put(EventPropertyTypesEnum.date.getName(), date);
    }
    
    public Date getDate() {
        return (Date) this.get(EventPropertyTypesEnum.date.getName());
    }
    
    public void setLocation(String location) {
        this.put(EventPropertyTypesEnum.location.getName(), location);
    }
    
    public String getLocation() {
        return (String) this.get(EventPropertyTypesEnum.location.getName());
    }

    public void setUserAgentInfo(Map<String, Object> userAgentInfo) {
        this.putAll(userAgentInfo);
    }
    
}
