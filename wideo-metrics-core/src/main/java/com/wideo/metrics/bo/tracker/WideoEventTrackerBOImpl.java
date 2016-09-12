package com.wideo.metrics.bo.tracker;


import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import com.wideo.metrics.bo.collector.StatsCollectorBO;
import com.wideo.metrics.bo.processor.StatsFilterProcessorBO;
import com.wideo.metrics.model.event.GenericEvent;
import com.wideo.metrics.model.event.GenericStatsEvent;
import com.wideo.metrics.persistence.dal.EventTrackDalImpl;

@Service
@ComponentScan("com.wideo.metrics")
public class WideoEventTrackerBOImpl implements EventTrackerBO {

    @Autowired
    EventTrackDalImpl eventTrackDalImpl;

    @Autowired
    StatsFilterProcessorBO statsFilterProcessorBO;
    
    public void trackEvent(GenericEvent te) {
        eventTrackDalImpl.trackEvent(te);
    }
    
    public void trackSocialStatsEvent(GenericStatsEvent event) {
        eventTrackDalImpl.trackSocialStatsEvent(event);
    }

    public void trackNewSocialStatsEvent(GenericStatsEvent event) {
        
        String wideoID = event.getWideoID();
        String action = event.getAction();
        Double value = ((Integer)event.get("views")).doubleValue();
        Date date = event.getDate();

        if (wideoID != null) {
            statsFilterProcessorBO.updateWideoFilteredStats(wideoID, action,
                    value, date, null, null);
        }
    }
    
    public void trackWideoStats(GenericEvent event) {
        eventTrackDalImpl.trackWideoStats(event);        
    }

}
