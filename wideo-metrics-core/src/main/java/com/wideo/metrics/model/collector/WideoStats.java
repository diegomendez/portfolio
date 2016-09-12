package com.wideo.metrics.model.collector;

import java.util.Date;
import java.util.Map;

public class WideoStats {

    private String wideoID;
    private String title;
    private Date createDate;
    private Map<String, Object> stats;
    
    public WideoStats() {
        
    }
    
    public WideoStats(String wideoID, String title, Date createDate) {
        this.wideoID = wideoID;
        this.title = title;
        this.createDate = createDate;
    }
    
    public WideoStats(String wideoID, String title, Date createDate, Map<String, Object> stats) {
        this.wideoID = wideoID;
        this.title = title;
        this.createDate = createDate;
        this.stats = stats;
    }

    public String getWideoID() {
        return wideoID;
    }

    public void setWideoID(String wideoID) {
        this.wideoID = wideoID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Map<String, Object> getStats() {
        return stats;
    }

    public void setStats(Map<String, Object> stats) {
        this.stats = stats;
    }
    
    
}
