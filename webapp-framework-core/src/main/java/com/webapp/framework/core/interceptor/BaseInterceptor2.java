package com.webapp.framework.core.interceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.ui.ModelMap;

import com.webapp.framework.core.beans.BaseUserForm;
import com.webapp.framework.core.beans.RunTime;
import com.webapp.framework.core.utils.InterceptorUtil;
import com.webapp.framework.core.utils.StringUtil;
import com.webapp.framework.core.utils.UrlFilterUtil;

public abstract class BaseInterceptor2 extends AbstractInterceptor {
	protected static boolean isMobileModel = false;

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		boolean b = super.preHandle(request, response, handler);
		if (!b) {
			return b;
		}

		String url = request.getServletPath();
		BaseUserForm user = getLoginUser(request, response, handler);

		String namespace = url.substring(0, url.lastIndexOf("/"));
		String method = url.substring(url.lastIndexOf("/") + 1);
		method = method.substring(0, method.lastIndexOf("."));
		boolean isNeedNextCheck = UrlFilterUtil.isNeedCheckAuth(namespace, method, null != user);

		if (!isNeedNextCheck) {
			return true;
		}

		boolean isCanAccess = isCanAccess(request, response, namespace, method, user, handler);
		if (!isCanAccess) {
			if (isMobileModel) {
				InterceptorUtil.outNoAuthErrorMsg(request, response, null != user);
				return false;
			}
			return toURL(request, response, null != user);
		}

		return true;
	}

	protected abstract boolean isCanAccess(HttpServletRequest paramHttpServletRequest,
			HttpServletResponse paramHttpServletResponse, String paramString1, String paramString2,
			BaseUserForm paramBaseUserForm, Object paramObject);

	protected void putMapBean(HttpServletRequest request, HttpServletResponse response, Object handler, ModelMap map) {
		BaseUserForm user = (BaseUserForm) request.getSession().getAttribute("LOGIN_USER_IN_SESSION_KEY");

		map.put("USER", user);
	}

	protected void initOnFirstAccess(HttpServletRequest request, HttpServletResponse response) {
		super.initOnFirstAccess(request, response);
		//addOverAllBean("CodesUtil", CodesUtil.class);
		try {
			this.log.info("扫描无需权限控制的白名单...");
			UrlFilterUtil.loadUrlFilter();
		} catch (Exception localException) {
		}
	}

	protected boolean toURL(HttpServletRequest request, HttpServletResponse response, boolean isUserLogin) {
		String surl = null;
		if (isUserLogin) {
			surl = (String) RunTime.get("noAuthPage");
		} else
			surl = (String) RunTime.get("loginPage");

		if (StringUtil.isNull(surl)) {
			return false;
		}
		if (surl.startsWith("/"))
			surl = request.getContextPath() + surl;
		try {
			response.sendRedirect(response.encodeRedirectURL(surl));
		} catch (Exception localException) {
		}
		return false;
	}

	protected String getCookie(HttpServletRequest request, String name) {
		String domain = (String) RunTime.get("hostDomain");
		String cookieName = name;
		if (StringUtil.isNotNull(domain)) {
			if (!domain.startsWith(".")) {
				domain = "." + domain;
			}
			if (cookieName.indexOf(domain) == -1) {
				cookieName = cookieName + domain;
			}
		}
		Cookie[] cookies = request.getCookies();
		if (null == cookies)
			return null;
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals(cookieName)) {
				return cookie.getValue();
			}

		}

		if (StringUtil.isNotNull(domain)) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(name)) {
					return cookie.getValue();
				}
			}
		}

		return null;
	}

	protected abstract BaseUserForm getLoginUser(HttpServletRequest paramHttpServletRequest,
			HttpServletResponse paramHttpServletResponse, Object paramObject);
}