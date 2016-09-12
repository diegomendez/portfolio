package com.wideo.metrics.event;

import java.util.Map;

import com.mongodb.BasicDBObject;
import com.wideo.metrics.events.EventNamespaceTypesEnum;
import com.wideo.metrics.events.EventPropertyTypesEnum;
import com.wideo.metrics.events.WideoEventTypesEnum;

public class WideoPlayEventDTO extends GenericEventDTO {

    private static final long serialVersionUID = 1L;

    String wideoID;
    
    public WideoPlayEventDTO(String wideoID, Map<String, Object> props, Map<String, Object> userAgentProperties) {
        super(EventNamespaceTypesEnum.player.getName(),
                WideoEventTypesEnum.WIDEO_PLAY.getName(), props, userAgentProperties);
//        addProperty(EventPropertyTypesEnum.wideo_id.getName(), wideoID);
        this.wideoID = wideoID;
    }
    
    public String getWideoID() {
        return wideoID;
    }

    public void setWideoID(String wideoID) {
        this.wideoID = wideoID;
    }
    
    @Override
    public BasicDBObject getBasicDBObject() {
        BasicDBObject newBasicDBObject = new BasicDBObject();
        newBasicDBObject = super.getBasicDBObject();
        newBasicDBObject.append(EventPropertyTypesEnum.wideo_id.getName(), wideoID);
        return newBasicDBObject;
    } 
}
