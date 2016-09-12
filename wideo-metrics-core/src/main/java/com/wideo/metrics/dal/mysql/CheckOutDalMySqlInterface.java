package com.wideo.metrics.dal.mysql;

import java.util.Date;
import java.util.List;

import com.wideo.metrics.model.processor.stats.DashboardStats;

public interface CheckOutDalMySqlInterface {

    public List<DashboardStats> getSoldPlansByName(Date startDate, Date endDate);

    public Double getTodayTotalRevenue(Date today);

    public Long getRecurrentPlansSold(Date startDate, Date endDate);

    public Long getTotalPlansSold(List<Integer> chargeTypeIds, Date startDate,
            Date endDate);

    public Long getRecurrentPlansSold(List<Integer> plansIDs, Date startDate,
            Date endDate);

    public Double getRevenue(Date startDate, Date endDate);
    
    public Double getRevenueForUsers(List<Long> userIDs, Date startDate, Date endDate);

}
