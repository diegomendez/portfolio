package com.wideo.metrics.persistence.dal.interfaces;

import com.wideo.metrics.model.event.GenericEvent;
import com.wideo.metrics.model.event.GenericStatsEvent;

public interface EventTrackDalInterface {
    
    public void trackEvent(GenericEvent event);
    
    public void trackSocialStatsEvent(GenericStatsEvent event);
    
    public void trackWideoStats(GenericEvent event);
    
}
