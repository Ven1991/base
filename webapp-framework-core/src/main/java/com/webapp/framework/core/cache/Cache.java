package com.webapp.framework.core.cache;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.webapp.framework.core.cache.oscache.OsCache;
import com.webapp.framework.core.cache.redis.RedisCache;
import com.webapp.framework.core.exception.BaseException;

public class Cache {
	private static Logger log = LogManager.getLogger(Cache.class);
	private static Cache instance;
	private OsCache osCache;
	private RedisCache redisCache;

	public static Cache getInstance() {
		if (null != instance)
			return instance;
		try {
			synchronized (log) {
				if (null != instance) {
					return instance;
				}
				instance = new Cache();
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return instance;
	}

	private Cache() {
		this.osCache = OsCache.getInstance();
		try {
			this.redisCache = RedisCache.getInstance();
		} catch (Exception e) {
			log.error("初始化Redis失败：" + e.getMessage());
			this.redisCache = null;
		}
	}

	public boolean put(String key, String field, Object value) {
		if ((null == key) || (null == field))
			BaseException.throwException("键值不允许为空");
		if (null != this.redisCache) {
			return this.redisCache.putMapItem(key, field, value);
		}
		Map map = (Map) this.osCache.get(key);
		if (null == map) {
			try {
				synchronized (instance) {
					map = (Map) this.osCache.get(key);
					if (null == map)
						map = new HashMap();
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
		map.put(field, value);
		this.osCache.put(key, map);
		return true;
	}

	public <T> T get(String key, String field, Class<T> clazz) {
		if ((null == key) || (null == field))
			BaseException.throwException("键值不允许为空");
		if (null != this.redisCache) {
			return this.redisCache.getMapItem(key, field, clazz);
		}
		Map map = (Map) this.osCache.get(key);
		if (null == map) {
			return null;
		}
		return (T) map.get(field);
	}

	public Long remove(String key) {
		if (null == key)
			BaseException.throwException("键值不允许为空");
		if (null != this.redisCache) {
			return this.redisCache.remove(new String[] { key });
		}
		this.osCache.remove(key);
		return Long.valueOf(1L);
	}

	public Long remove(String key, String[] fields) {
		if ((null == key) || (null == fields))
			BaseException.throwException("键值不允许为空");
		if (null != this.redisCache) {
			return this.redisCache.removeFromMap(key, fields);
		}
		if (null == fields) {
			return Long.valueOf(0L);
		}
		Map map = (Map) this.osCache.get(key);
		if (null == map) {
			return Long.valueOf(0L);
		}
		for (String field : fields) {
			map.remove(field);
		}
		this.osCache.put(key, map);
		return Long.valueOf(1L);
	}
}