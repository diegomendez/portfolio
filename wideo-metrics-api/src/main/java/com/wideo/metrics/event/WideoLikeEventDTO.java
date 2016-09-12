package com.wideo.metrics.event;

import java.util.Map;

import com.mongodb.BasicDBObject;
import com.wideo.metrics.events.EventNamespaceTypesEnum;
import com.wideo.metrics.events.EventPropertyTypesEnum;
import com.wideo.metrics.events.WideoEventTypesEnum;

public class WideoLikeEventDTO extends GenericEventDTO {

    private static final long serialVersionUID = 1L;

    String wideoID;
    Long userID;

    public WideoLikeEventDTO(String wideoID, Map<String, Object> props, Map<String, Object> userAgentProperties) {
        super(EventNamespaceTypesEnum.player.getName(),
                WideoEventTypesEnum.WIDEO_LIKE.getName(), props, userAgentProperties);
// addProperty(EventPropertyTypesEnum.wideo_id.getName(), wideoID);
        this.wideoID = wideoID;
    }
    
    public WideoLikeEventDTO(String wideoID, Long userID, Map<String, Object> props, Map<String, Object> userAgentProperties) {
        super(EventNamespaceTypesEnum.player.getName(),
                WideoEventTypesEnum.WIDEO_LIKE.getName(), props, userAgentProperties);
// addProperty(EventPropertyTypesEnum.wideo_id.getName(), wideoID);
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
        return this.userID;
    }
    
    public void setUserID(Long userID) {
        this.userID = userID;
    }
    
    @Override
    public BasicDBObject getBasicDBObject() {
        BasicDBObject newBasicDBObject = new BasicDBObject();
        newBasicDBObject = super.getBasicDBObject();
        newBasicDBObject.append(EventPropertyTypesEnum.wideo_id.getName(), wideoID);
        newBasicDBObject.append(EventPropertyTypesEnum.user_id.getName(), userID);
        return newBasicDBObject;
    } 
}
