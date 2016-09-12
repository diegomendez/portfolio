package com.wideo.metrics.model.collector;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InteractionsStats extends StatsResults {

    private static final long serialVersionUID = 1L;
    
    private Double total;
    private Map<String, StatsResults> stats;
    
    public InteractionsStats() { 
        stats = new HashMap<String, StatsResults>();
    }
    
    public InteractionsStats(Map<String, StatsResults> actionStats) {
        this.stats = actionStats;
    }
    
    public Map<String, StatsResults> getStats() {
        return this.stats;
    }
    
    public void addStats(String key, StatsResults stat) {
        stats.put(key,stat);
    }
    
    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }
    
    public void sumTotal(Double total) {
        if (this.total == null) {
            this.total = total;
        } else {
            this.total += total;
        }
    }

    @Override
    public String toString() {
        return "{total:" + total + ", stats:" + stats.toString() + "}";
    }

}
