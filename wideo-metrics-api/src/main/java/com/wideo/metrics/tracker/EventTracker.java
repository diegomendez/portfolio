package com.wideo.metrics.tracker;

import com.wideo.metrics.event.CustomEventDTO;
import com.wideo.metrics.event.GenericEventDTO;
import com.wideo.metrics.event.GenericStatsEventDTO;
import com.wideo.metrics.event.WideoCloneEventDTO;
import com.wideo.metrics.event.WideoDownloadEventDTO;
import com.wideo.metrics.event.WideoEmbedEventDTO;
import com.wideo.metrics.event.WideoInteractionEventDTO;
import com.wideo.metrics.event.WideoLikeEventDTO;
import com.wideo.metrics.event.WideoPlayEventDTO;
import com.wideo.metrics.event.WideoReuseEventDTO;
import com.wideo.metrics.event.WideoViewEventDTO;
import com.wideo.metrics.event.WideoYoutubeShareEventDTO;

public interface EventTracker {

    public void trackEvent(GenericEventDTO te);
    
    public void trackWideoPlay(WideoPlayEventDTO wideoPlayEventDTO);
    
    public void trackWideoView(WideoViewEventDTO wideoViewEventDTO);
    
    public void trackWideoLike(WideoLikeEventDTO wideoLikeEventDTO);
    
    public void trackWideoReuse(WideoReuseEventDTO wideoReuseEventDTO);
    
    public void trackWideoInteraction(WideoInteractionEventDTO wideoInteractionEventDTO);
    
    public void trackWideoEmbed(WideoEmbedEventDTO wideoEmbedEventDTO);
    
    public void trackWideoClone(WideoCloneEventDTO wideoCloneEventDTO);

    public void trackWideoDownload(WideoDownloadEventDTO wideoDownloadEventDTO);
    
    public void trackCustomEvent(CustomEventDTO customEventDTO);
    
    public void trackSocialStatsEvent(GenericStatsEventDTO socialStatsEventDTO);
    
    public void trackNewSocialStatsEvent(GenericStatsEventDTO socialStatsEventDTO);
    
    public void trackWideoStatsEvent(GenericEventDTO genericEventDTO);

    public void trackWideoYoutubeUpload(
            WideoYoutubeShareEventDTO wideoYoutubeShareEventDTO);

}
