package com.wideo.metrics.commons.sqlcache;

import javax.xml.bind.annotation.XmlRegistry;

@XmlRegistry
public class ObjectFactory {

	public ObjectFactory() {
	}

	public SQLCacheQueryGroup createSQLCacheQueryGroup() {
		return new SQLCacheQueryGroup();
	}

	public SQLCacheConfig createSQLCacheConfig() {
		return new SQLCacheConfig();
	}

}
