package com.wideo.metrics.bo.core;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.wideo.metrics.dal.mysql.WideoosDal;
import com.wideo.metrics.model.processor.stats.DashboardStats;

@Service
@ComponentScan("com.wideo.metrics")
public class WideoosBOImpl implements WideoosBO {

    @Autowired
    WideoosDal wideoosDal;

    @Override
    public int getWideosCreatedByDate(Date startDate, Date endDate, List<String> userCategories) {
        return wideoosDal.getCreatedWideos(startDate, endDate, userCategories);
    }
    
    @Override
    public List<DashboardStats> getBestWideos(Date startDate, Date endDate, int count) {
        return wideoosDal.getBestWideos(startDate,endDate,count);
    }

    
}
