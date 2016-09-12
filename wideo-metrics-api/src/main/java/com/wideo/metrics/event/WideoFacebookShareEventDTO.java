package com.wideo.metrics.event;

import java.util.Map;

import com.mongodb.BasicDBObject;
import com.wideo.metrics.events.EventNamespaceTypesEnum;
import com.wideo.metrics.events.EventPropertyTypesEnum;
import com.wideo.metrics.events.WideoEventTypesEnum;

public class WideoFacebookShareEventDTO extends GenericEventDTO {

    private static final long serialVersionUID = 1L;
    
    String wideoID;
    Long userID;
    
    public WideoFacebookShareEventDTO(String wideoID, Long userID, Map<String, Object> props, Map<String, Object> userAgentProperties) {
        super(EventNamespaceTypesEnum.player.getName(),WideoEventTypesEnum.FACEBOOK_SHARE.getName(),props,userAgentProperties);
        this.wideoID = wideoID;
        this.userID = userID;
    }
    
    public String getWideoID() {
        return wideoID;
    }

    public void setWideoID(String wideoID) {
        this.wideoID = wideoID;
    }
    
    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public BasicDBObject getBasicDBObject() {
        BasicDBObject newBasicDBObject = super.getBasicDBObject();
        newBasicDBObject.put(EventPropertyTypesEnum.wideo_id.getName(), wideoID);
        newBasicDBObject.put(EventPropertyTypesEnum.user_id.getName(), userID);
        return newBasicDBObject;
    }
    
}
