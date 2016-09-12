package com.wideo.metrics.commons.sqlcache;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "SQLCacheQueryGroup")
public class SQLCacheQueryGroup {

	@XmlElement(name = "queryGroupName")
	public String queryGroupName;

	@XmlElement(name = "query")
	public SQLCacheQuery[] query;

}
