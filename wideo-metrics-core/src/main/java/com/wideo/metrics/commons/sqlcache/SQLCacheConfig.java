package com.wideo.metrics.commons.sqlcache;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "SQLCacheConfig")
public class SQLCacheConfig {

	@XmlElement(name = "hit_counter")
	public String hit_counter;

	@XmlElement(name = "sql_path")
	public String[] sql_path;

}
