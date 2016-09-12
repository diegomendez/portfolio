package com.wideo.metrics.dal.mysql;

import java.util.Date;
import java.util.List;

import com.wideo.metrics.model.processor.stats.DashboardStats;

public interface WideoosDalMySqlInterface {

    public int getCreatedWideos(Date startDate, Date endDate, List<String> userCategories);
    
    public List<DashboardStats> getBestWideos(Date startDate, Date endDate,
            int count);
}
