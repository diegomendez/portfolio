package com.wideo.metrics.event;

import java.util.Map;

import com.mongodb.BasicDBObject;
import com.wideo.metrics.events.EventNamespaceTypesEnum;
import com.wideo.metrics.events.EventPropertyTypesEnum;
import com.wideo.metrics.events.WideoEventTypesEnum;

public class WideoInteractionEventDTO extends GenericEventDTO {

    private static final long serialVersionUID = 1L;

    private String wideoID;
    private String interactionID;
    
    public WideoInteractionEventDTO(WideoEventTypesEnum action, String wideoID,
            String interactionID, Map<String, Object> props, Map<String, Object> userAgentProperties) {

        super(EventNamespaceTypesEnum.interactivity.getName(),
                action.getName(), props, userAgentProperties);
        this.wideoID = wideoID;
        this.interactionID = interactionID;
    }
       
    public String getWideoID() {
        return wideoID;
    }

    public String getInteractionID() {
        return interactionID;
    }

    public void setWideoID(String wideoID) {
        this.wideoID = wideoID;
    }

    public void setInteractionID(String interactionID) {
        this.interactionID = interactionID;
    }

    @Override
    public BasicDBObject getBasicDBObject() {
        BasicDBObject newBasicDBObject = new BasicDBObject();
        newBasicDBObject = super.getBasicDBObject();
        newBasicDBObject.append(EventPropertyTypesEnum.wideo_id.getName(), wideoID);
        newBasicDBObject.append(EventPropertyTypesEnum.interaction_id.getName(), interactionID);
        return newBasicDBObject;
    }

}
