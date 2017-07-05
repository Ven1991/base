package com.webapp.framework.core.cache.redis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.formula.functions.T;

import com.google.gson.Gson;
import com.webapp.framework.core.exception.BaseException;
import com.webapp.framework.core.utils.PropertiesLoader;
import com.webapp.framework.core.utils.StringUtil;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisCache {
	private static Logger log = LogManager.getLogger(RedisCache.class);
	private static RedisCache instance;
	private JedisPool jedisPool;

	public static RedisCache getInstance() {
		if (null != instance)
			return instance;
		try {
			synchronized (log) {
				if (null != instance) {
					return instance;
				}
				instance = new RedisCache();
			}
		} catch (Exception e) {
			new BaseException("初始化Redis Cache异常", e);
		}

		return instance;
	}

	private RedisCache() {
		PropertiesLoader pl = new PropertiesLoader(new String[] { "/config/redis-config.properties" });

		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxIdle(getPropertiesValue(pl, "redis.pool.maxIdle", "5").intValue());
		config.setTestOnBorrow(true);
		config.setTestOnReturn(true);
		config.setMaxTotal(getPropertiesValue(pl, "redis.pool.maxIdle", "100").intValue());
		config.setMaxWaitMillis(Long.parseLong(pl.getProperty("redis.pool.maxWait", "3000")));
		String host = pl.getProperty("redis.host");
		if (StringUtil.isNull(host)) {
			BaseException.throwException("Redis配置错误，未设置Redis主机IP");
		}
		Integer port = getPropertiesValue(pl, "redis.port", "6379");
		this.jedisPool = new JedisPool(config, host, port.intValue());
	}

	private Integer getPropertiesValue(PropertiesLoader prop, String key, String def) {
		String value = prop.getProperty(key, def);
		return Integer.valueOf(Integer.parseInt(value));
	}

	@Deprecated
	public Jedis getRedisService() {
		return this.jedisPool.getResource();
	}

	@Deprecated
	public boolean clear() {
		Jedis jedis = null;
		try {
			jedis = this.jedisPool.getResource();
			jedis.flushDB();
			return true;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return false;
		} finally {
			jedis.close();
		}
	}

	@Deprecated
	public boolean put(String key, String value) {
		if ((null == value) || (null == key)) {
			return false;
		}
		Jedis jedis = null;
		try {
			jedis = this.jedisPool.getResource();
			jedis.set(key, value);
			return true;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return false;
		} finally {
			jedis.close();
		}
	}

	@Deprecated
	public boolean put(String key, Object value) {
		return put(key, getJsonString(value));
	}

	@Deprecated
	public boolean put(Map<String, Object> map) {
		if ((null == map) || (map.isEmpty())) {
			return false;
		}
		String[] values = new String[map.size() * 2];
		int i = 0;
		String key;
		for (Iterator localIterator = map.keySet().iterator(); localIterator.hasNext();) {
			key = (String) localIterator.next();
			values[i] = key;
			i++;
			values[i] = getObjectToString(map.get(key));
			i++;
		}
		
		Jedis jedis = null;
		try {
			jedis = this.jedisPool.getResource();
			jedis.mset(values);
			return true;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return false;
		} finally {
			jedis.close();
		}
	}

	@Deprecated
	public String get(String key) {
		if (null == key) {
			return null;
		}
		Jedis jedis = null;
		try {
			jedis = this.jedisPool.getResource();
			return jedis.get(key);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return null;
		} finally {
			jedis.close();
		}
	}

	@Deprecated
	public T get(String key, Class<T> clazz) {
		return (T) fromJson(get(key), clazz);
	}

	@Deprecated
	public List<String> list(String[] keys) {
		Jedis jedis = null;
		try {
			jedis = this.jedisPool.getResource();
			return jedis.mget(keys);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return null;
		} finally {
			jedis.close();
		}
	}

	@Deprecated
	public List<T> list(Class<T> clazz, String[] keys) {
		List<String> list = list(keys);
		List result = new ArrayList();
		if ((null == list) || (list.isEmpty())) {
			return result;
		}
		for (String json : list) {
			T bean = (T) fromJson(json, clazz);
			result.add(bean);
		}

		return result;
	}

	public Long exists(String[] keys) {
		Jedis jedis = null;
		try {
			jedis = this.jedisPool.getResource();
			return jedis.exists(keys);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return null;
		} finally {
			jedis.close();
		}
	}

	public boolean exists(String key) {
		Jedis jedis = null;
		try {
			jedis = this.jedisPool.getResource();
			return jedis.exists(key).booleanValue();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return false;
		} finally {
			jedis.close();
		}
	}

	public Long remove(String[] keys) {
		if (null == keys) {
			return null;
		}
		Jedis jedis = null;
		try {
			jedis = this.jedisPool.getResource();
			return jedis.del(keys);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return null;
		} finally {
			jedis.close();
		}
	}

	public String type(String key) {
		Jedis jedis = null;
		try {
			jedis = this.jedisPool.getResource();
			return jedis.type(key);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return null;
		} finally {
			jedis.close();
		}
	}

	public boolean putMapItem(String key, String field, String value) {
		Jedis jedis = null;
		try {
			jedis = this.jedisPool.getResource();
			jedis.hset(key, field, value);
			return true;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return false;
		} finally {
			jedis.close();
		}
	}

	public boolean putMapItem(String key, String field, Object value) {
		return putMapItem(key, field, getJsonString(value));
	}

	public boolean putMapAll(String key, Map<String, Object> map) {
		if ((null == map) || (map.isEmpty())) {
			return false;
		}
		Map values = new HashMap();
		String field;
		Jedis jedis = null;
		for (Iterator localIterator = map.keySet().iterator(); localIterator.hasNext();) {
			field = (String) localIterator.next();
			values.put(field, getObjectToString(map.get(field)));
		}
		try {
			jedis = this.jedisPool.getResource();
			jedis.hmset(key, values);
			return true;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return false;
		} finally {
			jedis.close();
		}
	}

	public boolean existsInMap(String key, String field) {
		Jedis jedis = null;
		try {
			jedis = this.jedisPool.getResource();
			return jedis.hexists(key, field).booleanValue();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return false;
		} finally {
			jedis.close();
		}
	}

	public Long removeFromMap(String key, String[] fields) {
		Jedis jedis = null;
		try {
			jedis = this.jedisPool.getResource();
			return jedis.hdel(key, fields);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return null;
		} finally {
			jedis.close();
		}
	}

	public Long sizeInMap(String key) {
		Jedis jedis = null;
		try {
			jedis = this.jedisPool.getResource();
			return jedis.hlen(key);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return null;
		} finally {
			jedis.close();
		}
	}

	public Set<String> keySetInMap(String key) {
		Jedis jedis = null;
		try {
			jedis = this.jedisPool.getResource();
			return jedis.hkeys(key);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return null;
		} finally {
			jedis.close();
		}
	}

	public String getMapItem(String key, String field) {
		Jedis jedis = null;
		try {
			jedis = this.jedisPool.getResource();
			return jedis.hget(key, field);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return null;
		} finally {
			jedis.close();
		}
	}

	public <T> T getMapItem(String key, String field, Class<T> clazz) {
		String json = getMapItem(key, field);
		if (StringUtil.isNull(json)) {
			return null;
		}
		return fromJson(json, clazz);
	}

	public List<String> getMapList(String key, String[] fields) {
		Jedis jedis = null;
		try {
			jedis = this.jedisPool.getResource();
			return jedis.hmget(key, fields);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return null;
		} finally {
			jedis.close();
		}
	}

	public List<T> getMapList(Class<T> clazz, String key, String[] fields) {
		List<String> list = getMapList(key, fields);
		List result = new ArrayList();
		if ((null == list) || (list.isEmpty())) {
			return result;
		}
		for (String json : list) {
			T bean = (T) fromJson(json, clazz);
			result.add(bean);
		}

		return result;
	}

	private <T> T fromJson(String value, Class<T> clazz) {
		if (StringUtil.isNull(value)) {
			return null;
		}
		Gson gson = new Gson();
		return gson.fromJson(value, clazz);
	}

	private String getJsonString(Object value) {
		if (null == value)
			return null;
		Gson gson = new Gson();
		return gson.toJson(value, value.getClass());
	}

	private String getObjectToString(Object value) {
		if (null == value) {
			return null;
		}
		if (((value instanceof String)) || ((value instanceof Double)) || ((value instanceof Float))
				|| ((value instanceof Integer)) || ((value instanceof Short))) {
			return value.toString();
		}
		String className = value.getClass().getSimpleName();

		if (className.equals("int"))
			return Integer.toString(((Integer) value).intValue());
		if (className.equals("double")) {
			return Double.toString(((Double) value).doubleValue());
		}
		return getJsonString(value);
	}
}