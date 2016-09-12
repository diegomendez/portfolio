package com.wideo.metrics.model.collector;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class InteractionsSummaryStats extends StatsResults {

    private static final long serialVersionUID = 1L;
    
    private Double total;
    private Double totalClicks;
    private Double ctr;
    private SortedMap<String, Object> stats;
    
    public InteractionsSummaryStats() { 
        stats = new TreeMap<String, Object>();
    }
    
    public InteractionsSummaryStats(SortedMap<String, Object> actionStats) {
        this.stats = actionStats;
    }
    
    public Map<String, Object> getStats() {
        return this.stats;
    }
    
    public void addStats(String key, Object stat) {
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

    public Double getTotalClicks() {
        return totalClicks;
    }

    public void setTotalClicks(Double totalClicks) {
        this.totalClicks = totalClicks;
    }

    public Double getCtr() {
        return ctr;
    }

    public void setCtr(Double ctr) {
        this.ctr = ctr;
    }

    @Override
    public String toString() {
        return "{total:" + total + ", stats:" + stats.toString() + "}";
    }

}
