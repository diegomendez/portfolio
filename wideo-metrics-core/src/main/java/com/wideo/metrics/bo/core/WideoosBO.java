package com.wideo.metrics.bo.core;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.wideo.metrics.dal.mysql.WideoosDal;
import com.wideo.metrics.model.processor.stats.DashboardStats;

public interface WideoosBO {

    public int getWideosCreatedByDate(Date startDate, Date endDate, List<String> userCategories);

    public List<DashboardStats> getBestWideos(Date startDate, Date endDate, int count);
    
}
