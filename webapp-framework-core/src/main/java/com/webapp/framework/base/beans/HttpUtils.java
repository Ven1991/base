package com.webapp.framework.base.beans;


import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang.ArrayUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.web.util.HtmlUtils;

import com.webapp.framework.base.controller.bean.HttpStackManager;
import com.webapp.framework.utils.file.utils.FileUtil;
import com.webapp.framework.utils.json.JsonMapper;
import com.webapp.framework.utils.tools.StringUtil;

public class HttpUtils
{
  public static HttpServletRequest getRequest()
  {
    return HttpStackManager.getRequest();
  }

  public static HttpServletResponse getResponse() {
    return HttpStackManager.getResponse();
  }

  public static HttpSession getSession() {
    HttpServletRequest request = getRequest();
    if (null == request)
      return null;
    return request.getSession();
  }

  public static boolean checkIsAndroid(HttpServletRequest request)
  {
    String head = request.getHeader("user-agent");
    boolean isAndroid = true;

    if (StringUtil.isNotNull(head)) {
      isAndroid = head.toLowerCase().indexOf("android") > 0;
    }

    return isAndroid;
  }

  public static boolean checkIsWeiXin(HttpServletRequest request)
  {
    String head = request.getHeader("user-agent");
    boolean isAndroid = true;

    if (StringUtil.isNotNull(head)) {
      isAndroid = head.toLowerCase().indexOf("micromessenger") > 0;
    }

    return isAndroid;
  }

  public static <T> T getParameter(String name, Object sDefault)
  {
    return getParameter(name, sDefault, Whitelist.basic());
  }

  public static <T> T getParameter(String name, Object sDefault, Whitelist whitelist)
  {
    return getParameter(name, sDefault, whitelist, false);
  }

  public static <T> T getParameter(String name, Object sDefault, Whitelist whitelist, boolean escape)
  {
    String value = getRequest().getParameter(name);
    if (null == value) {
      return (T) sDefault;
    }
    String val = clean(name, value, whitelist, true);
    return (T) convert(val, sDefault.getClass());
  }

  public static <T> T convert(Object o, Class<T> clas) {
    try {
      return (T) ConvertUtils.convert(o.toString(), clas); } catch (Exception e) {
    }
    return (T) o;
  }

  public static String[] getParameterValues(String name, Whitelist whitelist)
  {
    String[] values = getRequest().getParameterValues(name);
    if (ArrayUtils.isEmpty(values)) {
      return values;
    }
    return getParameterValues(name, values, whitelist);
  }

  public static String[] getParameterValues(String name, String[] values, Whitelist whitelist) {
    return getParameterValues(name, values, whitelist, Boolean.valueOf(false));
  }

  public static String[] getParameterValues(String name, String[] values, Whitelist whitelist, Boolean escape)
  {
    if ((null == values) || (values.length == 0)) {
      return values;
    }
    List list = new ArrayList();
    for (String value : values) {
      list.add(clean(name, value, whitelist, escape.booleanValue()));
    }
    return (String[])list.toArray(new String[0]);
  }

  public static String clean(String name, String value, Whitelist whitelist, boolean escapeHTML) {
    if ((StringUtil.isNull(value)) || (null == whitelist) || (JsonMapper.isJson(value))) {
      return value;
    }
    String val = Jsoup.clean(value, whitelist);
    if (escapeHTML) {
      val = HtmlUtils.htmlEscape(val);
    }
    return val;
  }

  public static void sendRedirect(String url) {
    sendRedirect(getResponse(), url);
  }

  public static void sendRedirect(HttpServletResponse response, String url)
  {
    if (StringUtil.isNull(url)) {
      return;
    }
    if (url.startsWith("/")) {
      HttpServletRequest request = getRequest();
      url = request.getContextPath() + url;
    }
    try {
      response.sendRedirect(response.encodeRedirectURL(url));
    }
    catch (Exception localException)
    {
    }
  }

  public static String getHost()
  {
    return getHost(getRequest());
  }

  public static String getHost(HttpServletRequest req) {
    if (null == req) {
      return null;
    }
    String host = req.getHeader("x-forwarded-host");
    if ((null == host) || (host.equals(""))) {
      host = req.getHeader("host");
    }
    if ((null == host) || (host.equals(""))) {
      return host;
    }

    if (host.indexOf(",") > 0) {
      host = host.split(",")[0];
    }

    return req.getScheme() + "://" + host;
  }

  public static String getIpAddr()
  {
    HttpServletRequest req = getRequest();
    if (null == req) {
      return null;
    }
    String ip = req.getHeader("X-Real-IP");
    if ((!StringUtil.isNull(ip)) && (!"unknown".equalsIgnoreCase(ip))) {
      return ip;
    }
    ip = req.getHeader("x-forwarded-for");
    if ((!StringUtil.isNull(ip)) && (!"unknown".equalsIgnoreCase(ip)))
    {
      int index = ip.indexOf(44);
      if (index != -1) {
        return ip.substring(0, index);
      }
      return ip;
    }

    if ((StringUtil.isNull(ip)) || ("unknown".equalsIgnoreCase(ip))) {
      ip = req.getHeader("Proxy-Client-IP");
    }
    if ((StringUtil.isNull(ip)) || ("unknown".equalsIgnoreCase(ip))) {
      ip = req.getHeader("WL-Proxy-Client-IP");
    }
    if ((StringUtil.isNull(ip)) || ("unknown".equalsIgnoreCase(ip))) {
      ip = req.getRemoteAddr();
    }
    return ip;
  }

  public static String getWebAppName()
  {
    String path = getWebAppRealPath();
    return path.substring(path.lastIndexOf(File.separator) + 1);
  }

  public static String getWebAppRealPath()
  {
    String serviceType = getWebServiceType();

    if (serviceType.equalsIgnoreCase("JBOSS")) {
      URL url = FileUtil.class.getResource("/config");
      String path = new File(url.getFile()).getParentFile().getParentFile().getParent();
      if (path.endsWith("lib")) {
        path = new File(path).getParentFile().getParent();
      }
      return path;
    }
    URL url = Thread.currentThread().getContextClassLoader().getResource("");
    String path = new File(url.getFile()).getParentFile().getParent();
    if (path.endsWith("lib")) {
      path = new File(path).getParentFile().getParent();
    }

    return path;
  }

  public static String getWebServiceType()
  {
    String tmp = System.getProperty("jboss.home.dir");
    if (StringUtil.isNotNull(tmp))
      return "JBOSS";
    tmp = System.getProperty("catalina.home");
    if (StringUtil.isNotNull(tmp)) {
      return "TOMCAT";
    }
    return null;
  }
}