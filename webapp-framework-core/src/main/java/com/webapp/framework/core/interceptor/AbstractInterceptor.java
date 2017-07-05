package com.webapp.framework.core.interceptor;

import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.webapp.framework.core.beans.HttpStackManager;
import com.webapp.framework.core.utils.FormatUtil;
import com.webapp.framework.core.utils.StringUtil;

public abstract class AbstractInterceptor extends HandlerInterceptorAdapter {
	protected Logger log = LogManager.getLogger(getClass());

	private Map<String, Object> overallMaps = new HashMap();
	private static Boolean isInit = Boolean.valueOf(false);

	private Map<Long, Long> runTimes = new ConcurrentHashMap();
	private static final String ERROR_MESSAGE_IN_SESSION_KEY = "ERROR_MESSAGE_IN_SESSION_KEY";
	private static final String SUCCESS_MESSAGE_IN_SESSION_KEY = "SUCCESS_MESSAGE_IN_SESSION_KEY";

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		if (!isInit.booleanValue())
			try {
				synchronized (isInit) {
					if (!isInit.booleanValue()) {
						isInit = Boolean.valueOf(true);
						initOnFirstAccess(request, response);
					}
				}
			} catch (Exception localException) {

			}
		this.log.debug("==============执行顺序: 1、preHandle================");

		this.runTimes.put(Long.valueOf(Thread.currentThread().getId()), Long.valueOf(System.currentTimeMillis()));
		return super.preHandle(request, response, handler);
	}

	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView model)
			throws Exception {
		this.log.debug("==============执行顺序: 2、postHandle================");

		String url = "";
		if (null != model) {
			ModelMap map = model.getModelMap();
			String menuPath = request.getServletPath();
			map.addAttribute("MENU_PATH", menuPath);

			putMapBean(request, response, handler, map);

			url = model.getViewName();
			if ((StringUtil.isNotNull(url)) && (url.toLowerCase().startsWith("redirect:"))) {
				String error = (String) map.get("_error");
				String message = (String) map.get("_success");
				if (StringUtil.isNotNull(error)) {
					request.getSession().setAttribute("ERROR_MESSAGE_IN_SESSION_KEY", error);
				}
				if (StringUtil.isNotNull(message)) {
					request.getSession().setAttribute("SUCCESS_MESSAGE_IN_SESSION_KEY", message);
				}
				map.clear();
			} else {
				String error = (String) map.get("_error");
				String message = (String) map.get("_success");
				if ((StringUtil.isNull(message)) && (StringUtil.isNull(error))) {
					error = (String) request.getSession().getAttribute("ERROR_MESSAGE_IN_SESSION_KEY");
					message = (String) request.getSession().getAttribute("SUCCESS_MESSAGE_IN_SESSION_KEY");
					if (StringUtil.isNotNull(error)) {
						map.put("_error", error);
					}
					if (StringUtil.isNotNull(message)) {
						map.put("_success", message);
					}
				}

				request.getSession().removeAttribute("ERROR_MESSAGE_IN_SESSION_KEY");
				request.getSession().removeAttribute("SUCCESS_MESSAGE_IN_SESSION_KEY");

				map.addAttribute("ROOT_PATH", request.getContextPath());
				map.addAttribute("request", request);
				map.addAttribute("response", response);
				map.addAttribute("session", request.getSession());
				map.addAllAttributes(this.overallMaps);

				Enumeration names = request.getAttributeNames();
				while (names.hasMoreElements()) {
					String name = (String) names.nextElement();
					map.addAttribute(name, request.getAttribute(name));
				}
			}
		}

		String action = "";
		if (null != handler) {
			action = handler.getClass().getName();
			action = action.substring(action.lastIndexOf(".") + 1);
		}

		Long runTime = (Long) this.runTimes.get(Long.valueOf(Thread.currentThread().getId()));
		if (runTime != null) {
			runTime = Long.valueOf(System.currentTimeMillis() - runTime.longValue());
			this.runTimes.remove(Long.valueOf(Thread.currentThread().getId()));
		}

		this.log.info(MessageFormat.format("[{0}][{1}][{2}][{3}ms]",
				new Object[] { action, request.getServletPath(), url, runTime }));
	}

	protected abstract void putMapBean(HttpServletRequest paramHttpServletRequest,
			HttpServletResponse paramHttpServletResponse, Object paramObject, ModelMap paramModelMap);

	protected void initOnFirstAccess(HttpServletRequest request, HttpServletResponse response) {
		addOverAllBean("Format", FormatUtil.class);
	}

	protected void addOverAllBean(String key, Object value) {
		this.overallMaps.put(key, value);
	}

	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		this.log.debug("==============执行顺序: 3、afterCompletion================");
		if (null != ex) {
			this.log.error("Action异常：" + ex.getMessage(), ex);
		}

		HttpStackManager.remoteThreadData();
	}
}
