package com.webapp.framework.core.utils;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class InterceptorUtil {
	protected static Logger log = LogManager.getLogger(InterceptorUtil.class);

	public static void outMobileErrorMsg(HttpServletRequest request, HttpServletResponse response, String str) {
		Map map = new HashMap();
		map.put("code", Integer.valueOf(1));
		map.put("message", str);

		writeJSON(request, response, map);
	}

	private static void writeJSON(HttpServletRequest request, HttpServletResponse response, Object o) {
		Gson gson = new Gson();
		String json = null == o ? "" : gson.toJson(o, o.getClass());
		try {
			response.setContentType("text/javascript;charset=UTF-8");
			PrintWriter out = response.getWriter();

			String jsoncallback = request.getParameter("jsoncallback");
			if ((jsoncallback != null) && (!jsoncallback.equals("")))
				out.write(jsoncallback + "(" + json + ")");
			else
				out.write(json);
		} catch (IOException e) {
			log.error("printWriter write exception:", e);
		}
	}

	public static void outNoAuthErrorMsg(HttpServletRequest request, HttpServletResponse response,
			boolean isUserLogin) {
		Map map = new HashMap();
		if (isUserLogin) {
			map.put("code", Integer.valueOf(3));
			map.put("message", "您无权限访问当前资源。");
		} else {
			map.put("code", Integer.valueOf(2));
			map.put("message", "请登录后再操作");
		}

		writeJSON(request, response, map);
	}
}