package com.webapp.framework.core.beans;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.ui.ModelMap;

public class HttpStackManager {
	private static final ThreadLocal<HttpStackData> actionContext = new ThreadLocal();

	protected static Log log = LogFactory.getLog(HttpStackManager.class);

	public static void initNewData(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) {
		HttpStackData hsd = new HttpStackData(request, response, modelMap);
		actionContext.set(hsd);
	}

	public static void putData(Object key, Object value) {
		HttpStackData hsd = getCurrentThreadStack();
		if (null == hsd)
			return;
		hsd.putData(key, value);
	}

	public static Object getData(Object key) {
		HttpStackData hsd = getCurrentThreadStack();
		if (null == hsd)
			return null;
		return hsd.getData(key);
	}

	public static HttpServletRequest getRequest() {
		HttpStackData hsd = getCurrentThreadStack();
		if (null == hsd)
			return null;
		return hsd.getRequest();
	}

	public static HttpServletResponse getResponse() {
		HttpStackData hsd = getCurrentThreadStack();
		if (null == hsd)
			return null;
		return hsd.getResponse();
	}

	public static ModelMap getModelMap() {
		HttpStackData hsd = getCurrentThreadStack();
		if (null == hsd)
			return null;
		return hsd.getModelMap();
	}

	private static HttpStackData getCurrentThreadStack() {
		return (HttpStackData) actionContext.get();
	}

	public static void remoteThreadData() {
		actionContext.remove();
	}
}