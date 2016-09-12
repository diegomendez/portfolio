package com.wideo.metrics.scheduler;

import java.io.File;

import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.support.GenericApplicationContext;

public class SchedulerStarter {
    public static void main(String[] args) {
	GenericApplicationContext newCtx = new GenericApplicationContext();
	
	String configLocation = "file:/data/wideo.co/config/youtube-scheduler-config.xml";
    newCtx.setId(configLocation);
	new XmlBeanDefinitionReader(newCtx).loadBeanDefinitions(configLocation);
	AnnotationConfigUtils.registerAnnotationConfigProcessors(newCtx);
	newCtx.refresh();

	newCtx.registerShutdownHook();
    }
}
