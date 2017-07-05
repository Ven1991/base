package com.webapp.framework.core.cache.oscache;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.stereotype.Component;

import com.opensymphony.oscache.base.EntryRefreshPolicy;
import com.opensymphony.oscache.base.NeedsRefreshException;
import com.opensymphony.oscache.general.GeneralCacheAdministrator;
import com.opensymphony.oscache.web.filter.ExpiresRefreshPolicy;
import com.webapp.framework.core.cache.CacheException;

@Component("osCache")
public class OsCache {
	private GeneralCacheAdministrator cacheAdministrator = new GeneralCacheAdministrator();
	private static OsCache instance = new OsCache();

	public OsCache() {
		
	}

	public OsCache(Properties prop) {
		this.cacheAdministrator = new GeneralCacheAdministrator(prop);
	}

	public static synchronized OsCache getInstance() {
		return instance;
	}

	public void put(Object key, Object value) {
		this.cacheAdministrator.putInCache(String.valueOf(key), value);
	}

	public Object get(Object key) {
		try {
			return this.cacheAdministrator.getFromCache(String.valueOf(key));
		} catch (NeedsRefreshException e) {
			this.cacheAdministrator.cancelUpdate(String.valueOf(key));
		}
		return null;
	}

	public void clear() throws CacheException {
		this.cacheAdministrator.flushAll();
	}

	public boolean containsKey(Object key) {
		return get(key) != null;
	}

	public void destroy() throws CacheException {
		this.cacheAdministrator.destroy();
	}

	public boolean flushAll() {
		this.cacheAdministrator.flushAll();
		return true;
	}

	public Map<Object, Object> get(Object[] keys) {
		Map result = new HashMap();
		for (Object key : keys) {
			Object value = get(key);
			result.put(key, value);
		}
		return result;
	}

	public void put(Object key, Object value, int TTL) {
		EntryRefreshPolicy Policy = new ExpiresRefreshPolicy(TTL);
		this.cacheAdministrator.putInCache(String.valueOf(key), value, Policy);
	}

	public boolean remove(Object key) {
		this.cacheAdministrator.flushEntry(String.valueOf(key));
		return true;
	}

	public GeneralCacheAdministrator getCacheAdministrator() {
		return this.cacheAdministrator;
	}

	public void setCacheAdministrator(GeneralCacheAdministrator cacheAdministrator) {
		this.cacheAdministrator = cacheAdministrator;
	}
}