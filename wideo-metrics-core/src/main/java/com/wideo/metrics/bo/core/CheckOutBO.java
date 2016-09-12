package com.wideo.metrics.bo.core;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.wideo.metrics.model.processor.stats.DashboardStats;

public interface CheckOutBO {

    public List<DashboardStats> getSoldPlansByName(Date startDate, Date endDate);
    
    public Map<String, Object> getTodayTotalRevenue(Date today);
    
    public Long getRecurrentPlansSold(Date startDate, Date endDate);

    public Long getTotalPlansSold(List<Integer> chargeTypeIds, Date startDate,
            Date endDate);

    public Double getMonthChurnRate(Integer month, Integer year);

    public Long getDistinctPaidUsers(Date startDate, Date endDate);

    public Double getRevenue(Date startDate, Date endDate);

    public Double getRevenueForUsers(List<Long> userIDs, Date startDate,
            Date endDate);
    
    
}
