package com.wideo.metrics.persistence.dal.interfaces;

import java.util.Date;

public interface StatsProcessorDalInterface {

    public boolean updateWideoFilteredStats(String wideoID, Date date,
            Double value, String action, String propertyKey,
            String propertyValue);

    public boolean updateWideoFilteredStatsPlayingSec(String wideoID, Date date,
            Double playingSec, Double value, String action);

    public boolean updateWideoInteractionsFilteredStats(String wideoID,
            String action, String interactionID, Double value, Date date);

    public boolean updateUserFilteredStats(Long userID, Date date,
            Double value, String action, String propertyKey, String propertyValue);
}
