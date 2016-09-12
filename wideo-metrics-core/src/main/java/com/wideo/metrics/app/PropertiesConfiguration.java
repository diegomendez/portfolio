package com.wideo.metrics.app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@PropertySources({

        @PropertySource(value = "classpath:/com/wideo/metrics/wideo-metrics.properties", ignoreResourceNotFound = false),
        @PropertySource(value = "file:/data/wideo.co/config/wideo-metrics.properties", ignoreResourceNotFound = true) })
public class PropertiesConfiguration {

    @Bean
    public PropertySourcesPlaceholderConfigurer getProperties() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
