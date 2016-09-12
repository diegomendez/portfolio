package com.wideo.metrics.services.tracker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wideo.metrics.bo.tracker.EventTrackerBO;
import com.wideo.metrics.model.event.GenericEvent;
import com.wideo.metrics.model.event.GenericStatsEvent;


@Service
public class EventTrackerServiceImpl implements EventTrackerService {

    @Autowired
    EventTrackerBO wideoEventTrackerBO;
    
    public void trackEvent(GenericEvent event) {
        wideoEventTrackerBO.trackEvent(event);
    }

    public void trackSocialStatEvent(GenericStatsEvent event) {
        wideoEventTrackerBO.trackSocialStatsEvent(event);
    }

    public void trackNewSocialStatEvent(GenericStatsEvent event) {
        wideoEventTrackerBO.trackNewSocialStatsEvent(event);
    }
    
    public void trackWideoStats(GenericEvent event) {
        wideoEventTrackerBO.trackWideoStats(event);
    }


}
