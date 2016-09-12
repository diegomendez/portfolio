package com.wideo.metrics.dal.mysql;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.wideo.metrics.model.processor.stats.DashboardStats;

public interface UsersDalMySqlInterface {

    public Long getSignupsByDate(Date startDate, Date endDate, List<String> userCategories);
    
    public List<DashboardStats> getSignupsByUserCategoryByDate(Date startDate, Date endDate);

    public List<Long> getUsersSignedUp(Date startDate, Date endDate, List<Long> userIDs,List<String> userCategories);
}
