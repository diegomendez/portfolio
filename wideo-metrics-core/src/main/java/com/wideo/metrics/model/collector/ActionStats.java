package com.wideo.metrics.model.collector;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class ActionStats extends StatsResults {

    private static final long serialVersionUID = 1L;
    
    private String action;
    private Double total;
    private SortedMap<Long, Object> stats;
    
    public ActionStats() {
        this.action = "";
        this.total = 0.0;
        stats = new TreeMap<Long, Object>();
    }
    
    public ActionStats(String action) {
        this.action = action;
        this.total = 0.0;
        stats = new TreeMap<Long, Object>();
    }
    
    public ActionStats(String action, SortedMap<Long, Object> stats) {
        this.action = action;
        this.total = 0.0;
        this.stats = stats;
    }
    
    public String getAction() {
        return action;
    }
    public void setAction(String action) {
        this.action = action;
    }
    
    public SortedMap<Long, Object> getStats() {
        return this.stats;
    }
    
    public void addStats(ValuesStats stat) {
        stats.put(stat.getName(),stat.getValue());
    }
    
    public void addStats(Long key, Object value) {
        stats.put(key, value);
    }
    

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }
    
    public void sumTotal(Double total) {
        if (this.total == null) {
            this.total = new Double(0);
        }
        this.total += total;
    }

    @Override
    public String toString() {
        return "{" + action + ": stats:" + stats.toString() + "}";
    }

}
