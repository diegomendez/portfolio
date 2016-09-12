package com.wideo.metrics.bo.core;

import java.util.Date;
import java.util.List;

import com.wideo.metrics.model.processor.stats.DashboardStats;

public interface UsersBO {

    public Long getSignupsByDate(Date startDate, Date endDate, List<String> userCategories);
    
    public List<DashboardStats> getSignupsByUserCategoryByDate(Date startDate,
            Date endDate);

    public Integer getUsersSignedUp(Date startDate, Date endDate,
            List<Long> userIDs,List<String> userCategories);
}
