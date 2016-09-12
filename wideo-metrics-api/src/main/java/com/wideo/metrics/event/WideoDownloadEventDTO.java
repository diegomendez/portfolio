package com.wideo.metrics.event;

import java.util.Map;

import com.mongodb.BasicDBObject;
import com.wideo.metrics.events.EventNamespaceTypesEnum;
import com.wideo.metrics.events.EventPropertyTypesEnum;
import com.wideo.metrics.events.WideoEventTypesEnum;

public class WideoDownloadEventDTO extends GenericEventDTO {

    private static final long serialVersionUID = 1L;

    Long userID;
    String wideoID;

    public WideoDownloadEventDTO(String wideoID, Long userID, Map<String, Object> props) {
        super(EventNamespaceTypesEnum.user.getName(),
                WideoEventTypesEnum.WIDEO_DOWNLOAD.getName(), props,
                null);
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

    @Override
    public BasicDBObject getBasicDBObject() {
        BasicDBObject newBasicDBObject = new BasicDBObject();
        newBasicDBObject = super.getBasicDBObject();
        newBasicDBObject.append(EventPropertyTypesEnum.wideo_id.getName(),
                wideoID);
        newBasicDBObject.append(EventPropertyTypesEnum.user_id.getName(),
                userID);
        return newBasicDBObject;
    }

}
