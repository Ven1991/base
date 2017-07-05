package com.webapp.framework.core.beans;

import java.util.HashMap;
import java.util.Map;

import com.webapp.framework.core.utils.FileUtil;
import com.webapp.framework.core.utils.NetWorkUtil;
import com.webapp.framework.core.utils.StringUtil;

public class RunTime {
	
	private static Map<String, Object> values = new HashMap();
	private static String thisServiceId;

	public RunTime(String appTabShortName, Integer stp) {
		values.put("APP_SHORT_TABLE_NAME", appTabShortName);
		values.put("SYSTEM_RUN_TYPE", stp);

		thisServiceId = NetWorkUtil.getMac();
		String webAppRootKey = FileUtil.getWebAppName();
		thisServiceId = thisServiceId + "-" + appTabShortName + "-" + stp + "-" + webAppRootKey;
		thisServiceId = StringUtil.getMd5(thisServiceId);
	}

	public static void put(String key, Object value) {
		values.put(key, value);
	}

	public static <T> T get(String key) {
		return (T) values.get(key);
	}

	public static Integer getSystemRunType() {
		Object o = values.get("SYSTEM_RUN_TYPE");
		if (null == o)
			return null;
		return (Integer) o;
	}

	public static String getTableShortName() {
		return (String) values.get("APP_SHORT_TABLE_NAME");
	}

	public static String getThisServiceId() {
		return thisServiceId;
	}

	public static class RunTimeKEY {
		public static final String APP_SHORT_TABLE_NAME = "APP_SHORT_TABLE_NAME";
		public static final String SYSTEM_RUN_TYPE = "SYSTEM_RUN_TYPE";
		public static final String noAuthPage = "noAuthPage";
		public static final String loginPage = "loginPage";
		public static final String loginOutPage = "loginOutPage";
		public static final String hostDomain = "hostDomain";
	}
	
	
	
}