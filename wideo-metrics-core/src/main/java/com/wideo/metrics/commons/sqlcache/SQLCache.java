package com.wideo.metrics.commons.sqlcache;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

public class SQLCache {

	private static HashMap<String, SQLCacheQuery> cache;
	private static SQLCacheConfig cfg;
	private final static String XML_CONFIG = "sql/SQLCacheConfig.xml";
	private static HashMap<String, Integer> hits;

	private final static String JAB_CONTEXT = "com.wideo.metrics.commons.sqlcache";

	private static final Logger LOGGER = Logger.getLogger(SQLCache.class);

	static {
		cache = new HashMap<String, SQLCacheQuery>();
		try {
			ClassLoader classLoader = Thread.currentThread()
					.getContextClassLoader();
			InputStream resourceAsStream = classLoader
					.getResourceAsStream(XML_CONFIG);

			JAXBContext jc = JAXBContext.newInstance(JAB_CONTEXT);
			Unmarshaller u;
			u = jc.createUnmarshaller();
			cfg = (SQLCacheConfig) u.unmarshal(resourceAsStream);

			// Si esta prendido el log de hits
			if (cfg.hit_counter.equals("Y"))
				hits = new HashMap<String, Integer>();
			initCache();

		} catch (JAXBException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	/**
	 * Imprime las estadisticas de hits a los queries
	 * 
	 */
	public static String getHitStats() {
		String txt = "";
		if (hits == null)
			txt = "Hit status apagado";
		else {
			Iterator<String> it = hits.keySet().iterator();
			String key;
			while (it.hasNext()) {
				key = (String) it.next();
				txt += cache.get(key).queryID + " -> HITS: " + hits.get(key)
						+ "\n";
			}
		}

		return txt;

	}

	private static void putSql(SQLCacheQuery sql)
			throws SQLCacheQueryDuplicateException {
		if (cache.containsKey(sql.queryID.toUpperCase())) {
			System.out.println("SQL Duplicado: " + sql.queryID.toUpperCase());
			throw new SQLCacheQueryDuplicateException();
		}
		hits.put(sql.queryID.toUpperCase(), 0);
		cache.put(sql.queryID.toUpperCase(), sql);
	}

	/**
	 * Trae un sql del cache
	 * 
	 * @param id
	 * @return
	 */
	private static SQLCacheQuery getSqlElement(String key) {
		key = key.toUpperCase();
		if (cfg.hit_counter.equals("Y")) {
			if (hits.containsKey(key)) {
				hits.put(key, hits.get(key) + 1);
			} else {
				hits.put(key, 1);
			}
		}

		if (!cache.containsKey(key)) {
			return null;
		} else {
			return (SQLCacheQuery) cache.get(key);
		}
	}

	/**
	 * Trae un sql del cache, solo el texto a ejecutar
	 * 
	 * @param id
	 * @return
	 */
	public static String getSql(String key) {
		return getSqlElement(key).queryText;
	}

	private static void initCache() throws JAXBException {
		LOGGER.info("Init SQL cache");
		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();

		JAXBContext jc = JAXBContext.newInstance(JAB_CONTEXT);
		Unmarshaller u;
		u = jc.createUnmarshaller();
		InputStream iStr;
		try {
			SQLCacheQueryGroup grp;
			try {
				for (int i = 0; i < cfg.sql_path.length; i++) {
					iStr = classLoader.getResourceAsStream(cfg.sql_path[i]);
					if (iStr != null) {
						grp = (SQLCacheQueryGroup) u.unmarshal(iStr);
						// Agrego los queries al cache
						for (SQLCacheQuery qry : grp.query)
							SQLCache.putSql(qry);
					}
				}

				LOGGER.info("Init SQL cache finished");
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			LOGGER.info("Init SQL cache FAIL");

			e.printStackTrace();
		}

	}
}
