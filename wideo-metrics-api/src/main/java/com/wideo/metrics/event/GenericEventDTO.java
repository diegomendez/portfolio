package com.wideo.metrics.event;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.joda.time.LocalDate;

import com.mongodb.BasicDBObject;
import com.wideo.metrics.events.EventPropertyTypesEnum;

public class GenericEventDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private String namespace;
    private String action;
    private Map<String, Object> properties;
    private Date date;
    private Map<String, Object> userAgentProperties;
    
    public GenericEventDTO(String namespace, String action, Map<String, Object> props, Map<String, Object> userAgentProperties) {
        this.namespace = namespace;
        this.action = action;
        this.date = new Date(System.currentTimeMillis());
        this.properties = new HashMap<String, Object>();
        if (props != null) {
            properties.putAll(props);
        }
        this.userAgentProperties = new HashMap<String, Object>();
        if (userAgentProperties != null) {
            this.userAgentProperties = userAgentProperties;
        }
    }
    
    public String getNamespace() {
        return this.namespace;
    }
    
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
    
    public String getAction() {
        return action;
    }
    
    public void setAction(String action) {
        this.action = action;
    }
    
    public Map<String, Object> getProperties() {
        return properties;
    }
    
    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
    
    public Date getDate() {
        return this.date;
    }
    
    public void addProperty(String key, Object value) {
        this.properties.put(key, value);
    }
    
    public BasicDBObject getBasicDBObject() {
        BasicDBObject newBasicDBObject = new BasicDBObject();
        newBasicDBObject.put("namespace", getNamespace());
        newBasicDBObject.put("action", getAction());
        newBasicDBObject.put("properties", getProperties());
        newBasicDBObject.put("date", getDate());
        newBasicDBObject.putAll(userAgentProperties);
        
        return newBasicDBObject;
    }
}
