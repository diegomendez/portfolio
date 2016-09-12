package com.wideo.metrics.bo.processor;

import java.util.Date;

public interface StatsFilterProcessorBO {

    public boolean updateWideoFilteredStats(String wideoID, String action,
            Double value, Date date, String propertyKey, String propertyValue);
    
    public boolean updateWideoFilteredStatsPlayingSec(String wideoID, String action,
            Double playingSec, Double value, Date date);
    
    public boolean updateUserFilteredStats(Long userID, String action,
            Double value, Date date, String propertyKey, String propertyValue);

    public boolean updateWideoInteractionsFilteredStats(String wideoID,
            String action, String interactionID, Double value, Date date);
    
}
