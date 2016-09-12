package com.wideo.metrics.services.tracker;

import com.wideo.metrics.model.event.GenericEvent;
import com.wideo.metrics.model.event.GenericStatsEvent;

public interface EventTrackerService {

    void trackEvent(GenericEvent event);
    
    void trackSocialStatEvent(GenericStatsEvent event);

    void trackNewSocialStatEvent(GenericStatsEvent event);
    
    void trackWideoStats(GenericEvent event);

}
