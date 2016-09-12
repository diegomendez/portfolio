package com.wideo.metrics.bo.collector;

import java.util.List;

import com.google.api.services.analyticsreporting.v4.AnalyticsReporting;
import com.google.api.services.analyticsreporting.v4.model.GetReportsResponse;

public interface AnalyticsCollectorBO {

    public GetReportsResponse queryGoogleAnalyticsData(String startDate,
            String endDate, List<String> metricsToLoad, List<String> dimensionsToLoad,
            List<String> ordersToLoad, String filterExpression, String pageToken);
    
    
    public List<Long> getUsersFromSourceMedium(String startDate, String endDate, String filterExpression);
    
}
