package com.wideo.metrics.event;

import java.util.Map;

import com.mongodb.BasicDBObject;
import com.wideo.metrics.events.EventNamespaceTypesEnum;
import com.wideo.metrics.events.EventPropertyTypesEnum;
import com.wideo.metrics.events.WideoEventTypesEnum;

public class WideoEmbedEventDTO extends GenericEventDTO {

    private static final long serialVersionUID = 1L;

    private String wideoID;
    private String embedSiteURL;
    
    public WideoEmbedEventDTO(String url, String wideoID,
            Map<String, Object> props, Map<String, Object> userAgentProperties) {
        super(EventNamespaceTypesEnum.player.getName(), WideoEventTypesEnum.EMBED_VIEW.getName(), props, userAgentProperties);
        this.wideoID = wideoID;
        this.embedSiteURL = url;
    }

    public String getWideoID() {
        return wideoID;
    }

    public void setWideoID(String wideoID) {
        this.wideoID = wideoID;
    }

    public String getEmbedSiteURL() {
        return embedSiteURL;
    }

    public void setEmbedSiteURL(String embedSiteURL) {
        this.embedSiteURL = embedSiteURL;
    }
    
    @Override
    public BasicDBObject getBasicDBObject() {
        BasicDBObject newBasicDBObject = new BasicDBObject();
        newBasicDBObject = super.getBasicDBObject();
        newBasicDBObject.append(EventPropertyTypesEnum.wideo_id.getName(), wideoID);
        newBasicDBObject.append(EventPropertyTypesEnum.embed_site_url.getName(), embedSiteURL);
        return newBasicDBObject;
    }

}
