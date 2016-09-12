package com.wideo.metrics.controllers;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.uadetector.ReadableUserAgent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.wideo.metrics.controller.agents.CachedUserAgentStringParser;
import com.wideo.metrics.events.EventPropertyTypesEnum;

@Component
public class AbstractController {

    @Autowired
    CachedUserAgentStringParser cachedUserAgentStringParser;
    
    protected Map<String, Object> getUserAgentInfo(HttpServletRequest request) {
        Map<String, Object> userAgentProperties = new HashMap<String, Object>();
        String browser = null;
        String os = null;
        
        if (request.getAttribute("countryID") != null) {
            userAgentProperties.put(EventPropertyTypesEnum.location.getName(), request.getAttribute("countryID"));
        }
        
        if (request.getAttribute("referer") != null) {
            String referer = (String) request.getAttribute("referer");
            if (!referer.contains("wideo.co")) {
                userAgentProperties.put(EventPropertyTypesEnum.embed_site_url.getName(), referer);
            }
        }
        
        try {
            // Get an UserAgentStringParser and analyze the requesting client
            ReadableUserAgent agent = cachedUserAgentStringParser.parse(request
                    .getHeader("User-Agent"));
            browser = agent.getName();
            os = agent.getOperatingSystem().getName();
            userAgentProperties.put(EventPropertyTypesEnum.browser.getName(), browser);
            userAgentProperties.put(EventPropertyTypesEnum.os.getName(), os);
            String deviceCategory = agent.getDeviceCategory().getCategory().getName();
            userAgentProperties.put(EventPropertyTypesEnum.device_category.getName(), deviceCategory);
        }
        catch (Exception e) {
            //handle exception
        }
        return userAgentProperties;
    }

}
