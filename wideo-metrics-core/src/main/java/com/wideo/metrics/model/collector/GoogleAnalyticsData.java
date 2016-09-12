package com.wideo.metrics.model.collector;

import java.util.List;

public class GoogleAnalyticsData {

    private String filterName;
    private Double signupsPct;
    private Double revenue;
    
    public String getFilterName() {
        return filterName;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    public Double getSignupsPct() {
        return signupsPct;
    }

    public void setSignupsPct(Double signupsPct) {
        this.signupsPct = signupsPct;
    }

    public void setRevenue(Double revenue) {
        this.revenue = revenue;
    }
    
    public Double getRevenue(){
        return revenue;
    }

}
