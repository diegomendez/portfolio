package com.wideo.metrics.bo.processor;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import com.wideo.metrics.persistence.dal.StatsProcessorDalImpl;

@Service
@ComponentScan("com.wideo.metrics")
public class StatsFilterProcessorBOImpl implements StatsFilterProcessorBO {

    @Autowired
    StatsProcessorDalImpl statsProcessorDalImpl;

    @Override
    public boolean updateWideoFilteredStats(String wideoID, String action,
            Double value, Date date, String propertyKey, String propertyValue) {
        return statsProcessorDalImpl.updateWideoFilteredStats(wideoID, date,
                value, action, propertyKey, propertyValue);
    }

    @Override
    public boolean updateWideoInteractionsFilteredStats(String wideoID,
            String action, String interactionID, Double value, Date date) {
        return statsProcessorDalImpl.updateWideoInteractionsFilteredStats(
                wideoID, action, interactionID, value, date);
    }

    @Override
    public boolean updateWideoFilteredStatsPlayingSec(String wideoID, String action,
            Double playingSec, Double value, Date date) {
        return statsProcessorDalImpl.updateWideoFilteredStatsPlayingSec(wideoID, date,
                playingSec, value, action);
    }

    @Override
    public boolean updateUserFilteredStats(Long userID, String action,
            Double value, Date date, String propertyKey, String propertyValue) {
        return statsProcessorDalImpl.updateUserFilteredStats(userID, date,
                value, action, propertyKey, propertyValue);
    }

}
