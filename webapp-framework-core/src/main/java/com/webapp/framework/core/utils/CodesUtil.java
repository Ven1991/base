package com.webapp.framework.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CodesUtil {
	protected static Logger logger = LoggerFactory.getLogger(CodesUtil.class);

	/*public static Map<String, AbCodes> getCodesMap(String type) {
		BaseCodeService baseCodeService = (BaseCodeService) BeanFactory.getBean("baseCodeService");
		return baseCodeService.getCodesMapFromCache(type);
	}

	public static List<AbCodes> getCodesList(String type) {
		BaseCodeService baseCodeService = (BaseCodeService) BeanFactory.getBean("baseCodeService");
		return baseCodeService.getAllCodesListFromCache(type);
	}

	public static AbCodes getCodesFromCache(String type, String key) {
		BaseCodeService baseCodeService = (BaseCodeService) BeanFactory.getBean("baseCodeService");
		return baseCodeService.getCodesFromCache(type, key);
	}

	public static AbCodes getCodesFromCache(String type, Integer sta) {
		if (null == sta)
			return null;
		String key = sta.toString();
		return getCodesFromCache(type, key);
	}

	public static String getCodesValueFromCache(String type, String key) {
		AbCodes c = getCodesFromCache(type, key);
		if (null == c) {
			return null;
		}
		return c.getValue();
	}

	public static String getValue(String type, String key) {
		return getCodesValueFromCache(type, key);
	}

	public static String getValue(String type, Integer key) {
		return getCodesValueFromCache(type, key);
	}

	public static String getValue(String type, Long key) {
		if (null == key)
			return null;
		return getCodesValueFromCache(type, Integer.valueOf(key.intValue()));
	}

	public static String getCodesValueFromCache(String type, String key, String defaultValue) {
		String value = getCodesValueFromCache(type, key);
		if (null == value) {
			return defaultValue;
		}
		return value;
	}

	public static String getCodesValueFromCache(String type, Integer sta) {
		if (null == sta)
			return null;
		String key = sta.toString();
		return getCodesValueFromCache(type, key);
	}

	public static String getCodesValueFromCache(String type, Integer sta, String defaultValue) {
		String value = getCodesValueFromCache(type, sta);
		if (null == value)
			return defaultValue;
		return value;
	}*/
}