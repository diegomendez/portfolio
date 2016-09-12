package com.wideo.metrics.model.processor.stats;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.bson.types.ObjectId;

import com.github.jmkgreen.morphia.annotations.Entity;
import com.github.jmkgreen.morphia.annotations.Id;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

@Entity(value = "wideos", noClassnameStored = true)
public class WideoFilteredStats implements FilteredStats {

    private static final long serialVersionUID = 1L;

    @Id
    private ObjectId id;
    private Date createdAt;
    private String wideo_id;
    private Date startDate;
    private Date endDate;
    private Map<String, Object> stats;
    private String propertyKey;
    private Boolean filteredByProperty;

    public WideoFilteredStats() {
        super();
    }

    public WideoFilteredStats(String wideoID, Date startDate) {
        super();
        this.wideo_id = wideoID;
        this.startDate = startDate;
        this.endDate = null;
        this.stats = new HashMap<String, Object>();
    }

    public WideoFilteredStats(String wideoID, Date startDate, String propertyKey) {
        super();
        this.wideo_id = wideoID;
        this.startDate = startDate;
        this.endDate = null;
        this.stats = new HashMap<String, Object>();
        this.propertyKey = propertyKey;
    }

    public WideoFilteredStats(Date creationDate, String wideoID,
            Date startDate, Date endDate, Boolean filteredByProperty) {
        super();
        this.createdAt = creationDate;
        this.wideo_id = wideoID;
        this.startDate = startDate;
        this.endDate = endDate;
        this.stats = new HashMap<String, Object>();
        this.filteredByProperty = filteredByProperty;
    }

    public WideoFilteredStats(String wideoID, Date startDate, Date endDate) {
        super();
        this.wideo_id = wideoID;
        this.startDate = startDate;
        this.endDate = endDate;
        this.stats = new HashMap<String, Object>();
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getWideoID() {
        return wideo_id;
    }

    public void setWideoID(String wideoID) {
        this.wideo_id = wideoID;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getEndDate() {
        return this.endDate;
    }

    public Map<String, Object> getStats() {
        return this.stats;
    }

    public void setSats(Map<String, Object> stats) {
        this.stats = stats;
    }

    @Override
    public String toString() {
        return "WideoFilteredStats [createdAt=" + createdAt + ", wideoID="
                + wideo_id + ", startDate=" + startDate + ", endDate= "
                + endDate + ", stats=" + stats + "]";
    }

    public void updateStats(String propertyValue, String action, Double value) {
        if (propertyValue != null) {
            Map<String, Object> propertyStats = null;
            if (stats.containsKey(propertyValue)) {
                propertyStats = (Map<String, Object>) stats.get(propertyValue);
            }
            else {
                propertyStats = new HashMap<String, Object>();
            }
            if (propertyStats.containsKey(action)) {
                Double oldValue = (Double) propertyStats.get(action);
                value += oldValue;
            }
            propertyStats.put(action, value);
            stats.put(propertyValue, propertyStats);
        }
        else {
            updateStats(action, value);
        }
    }

    public void updateStats(String action, Double value) {
        if (stats.containsKey(action)) {
            Double oldValue = (Double) stats.get(action);
            value += oldValue;
        }
        stats.put(action, value);
    }

    public void updatePlayingSecStats(String action, Double playingSec,
            Double value) {
        if (stats.containsKey(action)) {
            Map<String, Double> playingSecs = (Map<String, Double>) stats
                    .get(action);
            if (playingSecs.containsKey(String.valueOf(playingSec.longValue()))) {
                Double oldValue = playingSecs.get(String.valueOf(playingSec
                        .longValue()));
                value += oldValue;
            }
            playingSecs.put(String.valueOf(playingSec.longValue()), value);
        }
        else {
            Map<String, Double> playingSecs = new HashMap<String, Double>();
            playingSecs.put(String.valueOf(playingSec.longValue()), value);
            stats.put(action, playingSecs);
        }
    }

    public void updateInteractionsStats(String action, String interactionID,
            Double value) {
        Double oldValue = (Double) stats.get(action);
        Double overallValue = value;
        if (oldValue != null) {
            overallValue += oldValue;
        }
        stats.put(action, overallValue);
        Map<String, Object> interactionsMap = (Map<String, Object>) stats
                .get("interactions");
        Map<String, Double> interactionStats = null;
        if (interactionsMap != null) {
            interactionStats = (Map<String, Double>) interactionsMap
                    .get(interactionID);
            Double interactionActionValue = value;
            if (interactionStats != null) {
                Double oldActionValue = (Double) interactionStats.get(action);
                if (oldActionValue != null) {
                    interactionActionValue += oldActionValue;
                }
            }
            else {
                interactionStats = new HashMap<String, Double>();
                interactionStats.put(action, value);
            }
            interactionStats.put(action, interactionActionValue);
        }
        else {
            interactionsMap = new HashMap<String, Object>();
            interactionStats = new HashMap<String, Double>();
            interactionStats.put(action, value);
        }
        interactionsMap.put(interactionID, interactionStats);
        stats.put("interactions", interactionsMap);
    }

    public void setFilteredByProperty(Boolean filteredByProperty) {
        this.filteredByProperty = filteredByProperty;
    }
    
    public Boolean getFilteredByProperty() {
        return this.filteredByProperty;
    }
    
    public DBObject toDBObject() {
        DBObject newDBObject = new BasicDBObject();
        newDBObject.put("createdAt", this.createdAt);
        newDBObject.put("wideo_id", this.wideo_id);
        newDBObject.put("startDate", this.startDate);
        newDBObject.put("endDate", this.endDate);
        newDBObject.put("stats", this.stats);
        newDBObject.put("propertyKey", this.propertyKey);
        return newDBObject;
    }

}
