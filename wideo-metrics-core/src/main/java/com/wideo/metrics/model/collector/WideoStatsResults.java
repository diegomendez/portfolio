package com.wideo.metrics.model.collector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WideoStatsResults extends StatsResults {

    private static final long serialVersionUID = 1L;

    String wideoID;
    Map<String, StatsResults> values;

    public WideoStatsResults() {
        super();
        this.values = new HashMap<String, StatsResults>();
    }

    public WideoStatsResults(String wideoID) {
        super();
        this.wideoID = wideoID;
        this.values = new HashMap<String, StatsResults>();
    }

    public WideoStatsResults(String wideoID, Map<String, StatsResults> values) {
        super();
        this.wideoID = wideoID;
        this.values = values;
    }

    public WideoStatsResults(String wideoID, String actionKey,
            StatsResults value) {
        super();
        this.wideoID = wideoID;
        this.values = new HashMap<String, StatsResults>();
        values.put(actionKey, value);
    }

    public String getWideoID() {
        return wideoID;
    }

    public void setWideoID(String wideoID) {
        this.wideoID = wideoID;
    }

    public Map<String, StatsResults> getActionStats() {
        return this.values;
    }

    public void addStatsResult(String key, StatsResults statResult) {
        this.values.put(key, statResult);
    }

    public void addAllStatsResult(Map<String, StatsResults> statsResults) {
        this.values.putAll(statsResults);
    }

    @Override
    public String toString() {
        return "{ wideo_id:" + wideoID + ", actionStats: " + values.toString()
                + "}";
    }
}
