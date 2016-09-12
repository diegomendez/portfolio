package com.wideo.metrics.bo.tracker;

import com.wideo.metrics.model.event.GenericEvent;
import com.wideo.metrics.model.event.GenericStatsEvent;

public interface EventTrackerBO {
    
    public void trackEvent(GenericEvent te);
    
    public void trackSocialStatsEvent(GenericStatsEvent event);

    public void trackNewSocialStatsEvent(GenericStatsEvent event);
    
    public void trackWideoStats(GenericEvent event);

}
