package com.wideo.metrics.event;

import java.io.ObjectInputStream.GetField;
import java.util.Map;

import org.bson.BSONObject;
import org.json.JSONObject;

import com.mongodb.BasicDBObject;
import com.wideo.metrics.events.EventNamespaceTypesEnum;
import com.wideo.metrics.events.EventPropertyTypesEnum;
import com.wideo.metrics.events.WideoEventTypesEnum;

public class WideoViewEventDTO extends GenericEventDTO {

    private static final long serialVersionUID = 1L;

    String wideoID;
    
    public WideoViewEventDTO(String wideoID, Map<String, Object> props, Map<String, Object> userAgentProperties) {
        super(EventNamespaceTypesEnum.player.getName(),
                WideoEventTypesEnum.WIDEO_VIEW.getName(), props, userAgentProperties);
        this.wideoID = wideoID;
    }
    
    public String getWideoID() {
        return wideoID;
    }

    public void setWideoID(String wideoID) {
        this.wideoID = wideoID;
    }
    
    public BasicDBObject getBasicDBObject() {
        BasicDBObject newBasicDBObject = super.getBasicDBObject();
        newBasicDBObject.put(EventPropertyTypesEnum.wideo_id.getName(), wideoID);
        return newBasicDBObject;
    }
    

}
