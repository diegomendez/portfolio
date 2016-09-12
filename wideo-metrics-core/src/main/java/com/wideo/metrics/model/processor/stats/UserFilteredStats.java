package com.wideo.metrics.model.processor.stats;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.bson.types.ObjectId;

import com.github.jmkgreen.morphia.annotations.Entity;
import com.github.jmkgreen.morphia.annotations.Id;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

@Entity(value = "users", noClassnameStored = true)
public class UserFilteredStats implements FilteredStats {

    private static final long serialVersionUID = 1L;

    @Id
    private ObjectId id;
    private Date createdAt;
    private Long user_id;
    private Date startDate;
    private Date endDate;
    private Map<String, Object> stats;
    private String propertyKey;
    private Boolean filterByProperties;

    public UserFilteredStats() {
        this.stats = new HashMap<String, Object>();
    }

    public UserFilteredStats(Long userID, Date date, String propertyKey) {
        this.user_id = userID;
        this.startDate = date;
        this.stats = new HashMap<String, Object>();
        if (propertyKey != null) {
            this.propertyKey = propertyKey;
        }
    }

    public UserFilteredStats(Date createdAt, Long userID, Date startDate,
            Date endDate, Boolean filteredByProperties) {
        this.createdAt = createdAt;
        this.user_id = userID;
        this.startDate = startDate;
        this.endDate = endDate;
        this.stats = new HashMap<String, Object>();
        this.filterByProperties = filteredByProperties;
    }

    public UserFilteredStats(Long userID, Date startDate, Date endDate) {
        this.user_id = userID;
        this.startDate = startDate;
        this.endDate = endDate;
        this.stats = new HashMap<String, Object>();
    }

    public Long getUserID() {
        return user_id;
    }

    public void setUserID(Long userID) {
        this.user_id = userID;
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
        return endDate;
    }

    public Map<String, Object> getStats() {
        return this.stats;
    }

    public void setStats(Map<String, Object> stats) {
        this.stats = stats;
    }

    @Override
    public String toString() {
        return "WideoFilteredStats [createdAt= " + createdAt + ", userID="
                + String.valueOf(user_id) + ", date=" + startDate + ", stats="
                + stats + "]";
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

    public void setFilteredByProperty(Boolean filtered) {
        this.filterByProperties = filtered;
    }

    public Boolean getFilteredByProperty() {
        return this.filterByProperties;
    }

    public DBObject toDBObject() {
        DBObject newDBObject = new BasicDBObject();
        newDBObject.put("createdAt", this.createdAt);
        newDBObject.put("user_id", this.user_id);
        newDBObject.put("startDate", this.startDate);
        newDBObject.put("endDate", this.endDate);
        newDBObject.put("stats", this.stats);
        newDBObject.put("propertyKey", this.propertyKey);
        newDBObject.put("filterByProperties", this.filterByProperties);
        return newDBObject;
    }

}
