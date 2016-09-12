package com.wideo.metrics.event;

import java.util.HashMap;
import java.util.Map;

import com.mongodb.BasicDBObject;
import com.wideo.metrics.events.EventNamespaceTypesEnum;
import com.wideo.metrics.events.EventPropertyTypesEnum;
import com.wideo.metrics.events.WideoEventTypesEnum;

public class WideoReuseEventDTO extends GenericEventDTO {

    private static final long serialVersionUID = 1L;

    private String reusedWideoID;
    private String newWideoID;
    private Long userID;
    
    public WideoReuseEventDTO(String reusedWideoID, String newWideoID, Long userID,
            Map<String, Object> props, Map<String, Object> userAgentProperties) {
        super(EventNamespaceTypesEnum.user.getName(),
                WideoEventTypesEnum.WIDEO_REUSE.getName(), props, userAgentProperties);
        this.reusedWideoID = reusedWideoID;
        this.newWideoID = newWideoID;
        this.userID = userID;
    }

    public String getReusedWideoID() {
        return reusedWideoID;
    }

    public void setReusedWideoID(String reusedWideoID) {
        this.reusedWideoID = reusedWideoID;
    }
    
    public String getNewWideoID() {
        return newWideoID;
    }

    public void setNewWideoID(String newWideoID) {
        this.newWideoID = newWideoID;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public BasicDBObject getBasicDBObject() {
        BasicDBObject newBasicDBObject = super.getBasicDBObject();
        newBasicDBObject.put(EventPropertyTypesEnum.reused_wideo_id.getName(), reusedWideoID);
        newBasicDBObject.put(EventPropertyTypesEnum.wideo_id.getName(), newWideoID);
        newBasicDBObject.put(EventPropertyTypesEnum.user_id.getName(), userID);
        return newBasicDBObject;
    }

}
