package com.wideo.metrics.event;

import java.util.HashMap;
import java.util.Map;

import com.mongodb.BasicDBObject;
import com.wideo.metrics.events.EventNamespaceTypesEnum;
import com.wideo.metrics.events.EventPropertyTypesEnum;
import com.wideo.metrics.events.WideoEventTypesEnum;

public class WideoCloneEventDTO extends GenericEventDTO {

    private static final long serialVersionUID = 1L;

    private String clonedWideoID;
    private String newWideoID;
    private Long userID;
    
    public WideoCloneEventDTO(String newWideoID, String clonedWideoID, Long userID,
            Map<String, Object> props, Map<String, Object> userAgentProperties) {

        super(EventNamespaceTypesEnum.user.getName(),
                WideoEventTypesEnum.WIDEO_CLONE.getName(), props, userAgentProperties);
        this.clonedWideoID = clonedWideoID;
        this.newWideoID = newWideoID;
        this.userID = userID;
    }

    public String getClonedWideoID() {
        return clonedWideoID;
    }
    
    public void setClonedWideoID(String clonedWideoID) {
        this.clonedWideoID = clonedWideoID;
    }
    
    public String getNewWideoID() {
        return newWideoID;
    }

    public void setWideoID(String newWideoID) {
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
        newBasicDBObject.put(EventPropertyTypesEnum.cloned_wideo_id.getName(), clonedWideoID);

        newBasicDBObject.put(EventPropertyTypesEnum.wideo_id.getName(), newWideoID);
        newBasicDBObject.put(EventPropertyTypesEnum.user_id.getName(), userID);
        return newBasicDBObject;
    }

}
