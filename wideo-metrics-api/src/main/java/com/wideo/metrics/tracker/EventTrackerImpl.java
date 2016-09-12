package com.wideo.metrics.tracker;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.wideo.metrics.event.CustomEventDTO;
import com.wideo.metrics.event.GenericEventDTO;
import com.wideo.metrics.event.GenericStatsEventDTO;
import com.wideo.metrics.event.WideoCloneEventDTO;
import com.wideo.metrics.event.WideoDownloadEventDTO;
import com.wideo.metrics.event.WideoEmbedEventDTO;
import com.wideo.metrics.event.WideoFacebookShareEventDTO;
import com.wideo.metrics.event.WideoInteractionEventDTO;
import com.wideo.metrics.event.WideoLikeEventDTO;
import com.wideo.metrics.event.WideoPlayEventDTO;
import com.wideo.metrics.event.WideoReuseEventDTO;
import com.wideo.metrics.event.WideoViewEventDTO;
import com.wideo.metrics.event.WideoYoutubeShareEventDTO;
import com.wideo.metrics.utils.HttpUtils;

public class EventTrackerImpl implements EventTracker {

    public Logger LOGGER = Logger.getLogger(EventTrackerImpl.class);

    private String wideoMetricsTrackingEnabled;
    private String wideoMetricsTrackingUrl;

    public void trackEvent(GenericEventDTO event) {
        if ("true".equals(wideoMetricsTrackingEnabled)) {
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("event", event.getBasicDBObject().toString());
            String response = HttpUtils.urlPost(
                    wideoMetricsTrackingUrl + "/trackAPI", headers, "",
                    "application/text");
        }
    }

    public void trackWideoView(WideoViewEventDTO wideoViewEventDTO) {
        trackEvent(wideoViewEventDTO);
    }

    public void trackWideoPlay(WideoPlayEventDTO wideoPlayEventDTO) {
        trackEvent(wideoPlayEventDTO);
    }

    public void trackWideoLike(WideoLikeEventDTO wideoLikeEventDTO) {
        trackEvent(wideoLikeEventDTO);
    }

    public void trackWideoEmbed(WideoEmbedEventDTO wideoEmbedEventDTO) {
        trackEvent(wideoEmbedEventDTO);
    }

    public void trackWideoInteraction(
            WideoInteractionEventDTO wideoInteractionEventDTO) {
        trackEvent(wideoInteractionEventDTO);
    }

    public void trackWideoReuse(WideoReuseEventDTO wideoReuseEventDTO) {
        trackEvent(wideoReuseEventDTO);
    }

    public void trackWideoClone(WideoCloneEventDTO wideoCloneEventDTO) {
        trackEvent(wideoCloneEventDTO);
    }

    public void trackWideoDownload(WideoDownloadEventDTO wideoDownloadEventDTO) {
        trackEvent(wideoDownloadEventDTO);
    }

    public void trackWideoYoutubeShare(
            WideoYoutubeShareEventDTO wideoYoutubeShareEventDTO) {
        trackEvent(wideoYoutubeShareEventDTO);
    }

    public void trackWideoFacebookShare(
            WideoFacebookShareEventDTO wideoFacebookShareEventDTO) {
        trackEvent(wideoFacebookShareEventDTO);
    }
    
    public void trackWideoYoutubeUpload(
            WideoYoutubeShareEventDTO wideoYoutubeShareEventDTO) {
        trackEvent(wideoYoutubeShareEventDTO);
    }

    public void trackCustomEvent(CustomEventDTO customEventDTO) {
        trackEvent(customEventDTO);
    }
    
    public void trackSocialStatsEvent(GenericStatsEventDTO event) {
        if ("true".equals(wideoMetricsTrackingEnabled)) {
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("event", event.getBasicDBObject().toString());
            String response = HttpUtils.urlPost(
                    wideoMetricsTrackingUrl + "/trackSocialStats", headers, "",
                    "application/text");
        }
    }

    public void trackNewSocialStatsEvent(GenericStatsEventDTO event) {
        if ("true".equals(wideoMetricsTrackingEnabled)) {
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("event", event.getBasicDBObject().toString());
            String response = HttpUtils.urlPost(
                    wideoMetricsTrackingUrl + "/trackNewSocialStats", headers, "",
                    "application/text");
        }
    }
    
    public void trackWideoStatsEvent(GenericEventDTO event) {
        if ("true".equals(wideoMetricsTrackingEnabled)) {
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("event", event.getBasicDBObject().toString());
            String response = HttpUtils.urlPost(
                    wideoMetricsTrackingUrl + "/trackWideoStats", headers, "",
                    "application/text");
        }
    }
    
    @Required
    public void setWideoMetricsTrackingUrl(String wideoMetricsTrackingUrl) {
        this.wideoMetricsTrackingUrl = wideoMetricsTrackingUrl;
    }

    @Required
    public void setWideoMetricsTrackingEnabled(
            String wideoMetricsTrackingEnabled) {
        this.wideoMetricsTrackingEnabled = wideoMetricsTrackingEnabled;
    }
}
