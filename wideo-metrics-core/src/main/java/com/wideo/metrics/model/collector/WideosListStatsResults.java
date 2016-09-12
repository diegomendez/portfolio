package com.wideo.metrics.model.collector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WideosListStatsResults extends StatsResults {

    private static final long serialVersionUID = 1L;

    Map<String, StatsResults> values;

    public WideosListStatsResults() {
        super();
        this.values = new HashMap<String, StatsResults>();
    }

    public WideosListStatsResults(Map<String, StatsResults> values) {
        super();
        this.values = values;
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
        return "{wideosList: " + values.toString()
                + "}";
    }
}
