package com.wideo.metrics.commons.sqlcache;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "SQLCacheQuery")
public class SQLCacheQuery {

	@XmlElement(name = "queryID")
	public String queryID;

	@XmlElement(name = "queryText")
	public String queryText;

}
