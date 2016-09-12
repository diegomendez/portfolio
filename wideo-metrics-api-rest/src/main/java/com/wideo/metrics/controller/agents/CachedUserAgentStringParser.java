package com.wideo.metrics.controller.agents;

import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import net.sf.uadetector.ReadableUserAgent;
import net.sf.uadetector.UserAgentStringParser;
import net.sf.uadetector.service.UADetectorServiceFactory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

@Component
public class CachedUserAgentStringParser implements UserAgentStringParser {

    private final UserAgentStringParser parser = UADetectorServiceFactory
            .getCachingAndUpdatingParser();
    private final Cache<String, ReadableUserAgent> cache = CacheBuilder
            .newBuilder().maximumSize(100).expireAfterWrite(2, TimeUnit.HOURS)
            .build();

    @Override
    public String getDataVersion() {
        return parser.getDataVersion();
    }

    @Override
    public ReadableUserAgent parse(final String userAgentString) {
        ReadableUserAgent result = cache.getIfPresent(userAgentString);
        if (result == null) {
            result = parser.parse(userAgentString);
            cache.put(userAgentString, result);
        }
        return result;
    }

    @Override
    public void shutdown() {
        parser.shutdown();
    }

}
