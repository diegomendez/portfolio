package com.wideo.metrics.event;

import java.util.HashMap;
import java.util.Map;

import com.mongodb.BasicDBObject;

public class CustomEventDTO extends GenericEventDTO {

    private static final long serialVersionUID = 1L;

    private Map<String, Object> eventCustomKeys;
    
    public CustomEventDTO(String namespace, String action, Map<String, Object> eventCustomKeys,
            Map<String, Object> props, Map<String, Object> userAgentProperties) {
        super(namespace, action, props, userAgentProperties);
        this.eventCustomKeys = new HashMap<String, Object>();
        if (eventCustomKeys != null) {
            this.eventCustomKeys.putAll(eventCustomKeys);
        }
    }
    
    public void setNewCustomKey(String key, String value) {
        this.eventCustomKeys.put(key, value);
    }
    
    @Override
    public BasicDBObject getBasicDBObject() {
        BasicDBObject newBasicDBObject = new BasicDBObject();
        newBasicDBObject = super.getBasicDBObject();
        newBasicDBObject.putAll(eventCustomKeys);
        return newBasicDBObject;
    }

}
