package com.wideo.metrics.model.processor.stats;

import java.io.Serializable;

public class DashboardStats implements Serializable {

    String name;
    Object value;

    public DashboardStats() {

    }

    public DashboardStats(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "DashboardStats [name=" + name + ", value=" + value + "]";
    }

}
