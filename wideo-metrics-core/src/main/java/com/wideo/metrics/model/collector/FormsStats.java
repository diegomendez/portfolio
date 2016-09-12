package com.wideo.metrics.model.collector;

import java.util.Date;
import java.util.Map;

public class FormsStats extends StatsResults {

    private static final long serialVersionUID = 1L;

//    String wideoID;
//    Long userID;
//    String interactionID;
    Date date;
    Map<String, Object> data;

    public FormsStats() {
    };

    public FormsStats(String interactionID, Date date, Map<String, Object> data) {
//        this.wideoID = wideoID;
//        this.userID = userID;
//        this.interactionID = interactionID;
        this.date = date;
        this.data = data;
    }
//
//    public String getWideoID() {
//        return wideoID;
//    }
//
//    public void setWideoID(String wideoID) {
//        this.wideoID = wideoID;
//    }
    
    public Date getDate() {
        return this.date;
    }
    
    public void setDate(Date date) {
        this.date = date;
    }
    
//    public String getInteractionID() {
//        return interactionID;
//    }
//
//    public void setInteractionID(String interactionID) {
//        this.interactionID = interactionID;
//    }

//    public Long getUserID() {
//        return userID;
//    }
//
//    public void setUserID(Long userID) {
//        this.userID = userID;
//    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

}
