package com.wideo.metrics.model.collector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserStatsResults extends StatsResults {

    private static final long serialVersionUID = 1L;

    Long userID;
    Map<String, StatsResults> values;

    public UserStatsResults(Long userID) {
        this.userID = userID;
        this.values = new HashMap<String, StatsResults>();
    }

    public UserStatsResults(Long userID, Map<String, StatsResults> values) {
        this.userID = userID;
        this.values = values;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public Map<String, StatsResults> getActionStats() {
        return this.values;
    }

    public void addStatsResult(String key, StatsResults statResult) {
        this.values.put(key,statResult);
    }
    
    public void addAllStatsResult(Map<String, StatsResults> statResult) {
        this.values.putAll(statResult);
    }

    @Override
    public String toString() {
        return "{ user_id:" + userID + ", stats: "
                + values.toString() + "}";
    }
}
