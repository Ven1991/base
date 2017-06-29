package com.webapp.framework.core.mvc.controller;


import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.util.HtmlUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.webapp.framework.base.beans.BeanUtil;
import com.webapp.framework.base.beans.HttpUtils;
import com.webapp.framework.base.exception.BaseException;
import com.webapp.framework.core.mvc.controller.bean.HttpStackManager;
import com.webapp.framework.utils.json.JsonMapper;
import com.webapp.framework.utils.tools.StringUtil;

public class BaseController
{
  public static final String ERROR_MESSAGE_IN_MAP_KEY = "_error";
  public static final String SUCCESS_MESSAGE_IN_MAP_KEY = "_success";
  protected Log log = LogFactory.getLog(getClass());

  protected String redirect(String url)
  {
    return String.format("redirect:%s", new Object[] { url });
  }

  @ModelAttribute
  protected void setModelMap(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap)
  {
    HttpStackManager.initNewData(request, response, modelMap);
  }

  public void setBean(String name, Object value) {
    ModelMap modelMap = HttpStackManager.getModelMap();
    if (null == modelMap)
      BaseException.throwException("ModelMap is null.");
    modelMap.addAttribute(name, value);
  }

  protected void success(String msg, Object[] args)
  {
    if (null == msg) {
      return;
    }
    if ((null != args) && (args.length > 0)) {
      msg = StringUtil.replace(msg, args);
    }

    setBean("_success", msg.replaceAll("\"", "\\\\\""));
  }

  protected void success(String msg) {
    success(msg, new Object[0]);
  }

  protected void error(String msg, Object[] args)
  {
    if (null == msg) {
      return;
    }
    if ((null != args) && (args.length > 0)) {
      msg = StringUtil.replace(msg, args);
    }

    setBean("_error", msg.replaceAll("\"", "\\\\\""));
  }

  protected void error(String msg) {
    error(msg, new Object[0]);
  }

  protected String okAjaxResult(Object value)
  {
    Map map = new HashMap();
    map.put("code", Integer.valueOf(0));
    map.put("data", value);
    outJsonObject(map);

    return null;
  }

  protected String errAjaxResult(String errMsg)
  {
    Map map = new HashMap();
    map.put("code", Integer.valueOf(1));
    map.put("message", errMsg);
    outJsonObject(map);
    return null;
  }

  protected <T> T getForm(Class<T> clas)
  {
    try
    {
      return copyProperties(clas, getParameterMap(true));
    } catch (Exception e) {
      this.log.error(e.getLocalizedMessage(), e);
    }
    return null;
  }

  protected <T> T getForm(Class<T> clas, boolean filter) {
    try {
      return copyProperties(clas, getParameterMap(filter));
    } catch (Exception e) {
      this.log.error(e.getLocalizedMessage(), e);
    }
    return null;
  }

  protected void copyProperties(Object target, Object source)
  {
    BeanUtil.copyProperties(target, source);
  }

  protected <T> T copyProperties(Class<T> destClass, Object orig)
  {
    return BeanUtil.copyProperties(destClass, orig);
  }

  protected Map<String, Object> getParameterMap(boolean escape)
  {
    Map map = new LinkedHashMap();
    HttpServletRequest req = getRequest();
    Enumeration names = req.getParameterNames();
    while (names.hasMoreElements()) {
      String name = (String)names.nextElement();
      String[] values = escape ? getParameterValues(name) : req.getParameterValues(name);

      if (name.endsWith("[]"))
      {
        name = name.substring(0, name.getBytes().length - 2);
      }

      if ((null == values) || (values.length == 0)) {
        map.put(name, values);
      }
      else if (values.length == 1) {
        map.put(name, values[0]);
      }
      else {
        StringBuilder builder = new StringBuilder();
        for (String value : values) {
          builder.append(new StringBuilder().append("'").append(value).append("',").toString());
        }
        builder = builder.deleteCharAt(builder.length() - 1);
        map.put(name, builder.toString());
      }
    }
    return map;
  }

  protected String[] getParameterValues(String name) {
    return getParameterValues(name, Whitelist.basic());
  }

  protected String[] getParameterValues(String name, Whitelist whitelist) {
    return HttpUtils.getParameterValues(name, whitelist);
  }

  protected String getParameter(String name)
  {
    return (String)HttpUtils.getParameter(name, "");
  }

  protected <T> T getParameter(String name, Object sDefault)
  {
    return getParameter(name, sDefault, Whitelist.basic());
  }

  protected <T> T getParameter(String name, Object sDefault, Whitelist whitelist)
  {
    return getParameter(name, sDefault, whitelist, false);
  }

  protected <T> T getParameter(String name, Object sDefault, Whitelist whitelist, boolean escape)
  {
    String value = getRequest().getParameter(name);
    if (null == value) {
      return (T) sDefault;
    }
    String val = clean(name, value, whitelist);
    return (T) convert(val, sDefault.getClass());
  }

  protected String clean(String name, String value, Whitelist whitelist) {
    return clean(name, value, whitelist, false);
  }

  protected String clean(String name, String value, Whitelist whitelist, boolean escapeHTML) {
    if ((value.isEmpty()) || (null == whitelist) || (JsonMapper.isJson(value))) {
      return value;
    }
    String val = Jsoup.clean(value, whitelist);
    if (escapeHTML) {
      val = HtmlUtils.htmlEscape(val);
    }
    if (!val.equals(value)) {
      this.log.info(new StringBuilder().append("parameter(").append(name).append(") value is unsafe:").append(value).append(" ,be cleaned : ").append(val).toString());
    }
    return val;
  }

  protected <T> T convert(Object o, Class<T> clas)
  {
    try {
      return (T) ConvertUtils.convert(o.toString(), clas);
    } catch (Exception e) {
      this.log.error(e.getLocalizedMessage(), e);
    }
    return (T) o;
  }

  public HttpServletRequest getRequest()
  {
    return HttpStackManager.getRequest();
  }

  public HttpServletResponse getResponse() {
    return HttpStackManager.getResponse();
  }

  protected HttpSession getSession() {
    HttpServletRequest request = getRequest();
    if (null == request)
      return null;
    return request.getSession();
  }

  protected void outJsonObject(Object o)
  {
    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss.SSS").create();
    String msg = gson.toJson(o);
    outString(msg);
  }

  protected void outString(String msg) {
    HttpServletResponse response = getResponse();
    if (null == response) {
      BaseException.throwException("response is null");
    }
    response.setContentType("text/html;charset=utf-8");
    try {
      String callback = getParameter("jsoncallback");
      if (StringUtil.isNotNull(callback))
        response.getWriter().write(new StringBuilder().append(callback).append("(").append(msg).append(")").toString());
      else
        response.getWriter().write(msg);
    }
    catch (Exception localException)
    {
    }
  }

  protected void outJsonArray(Object o)
  {
    outJsonObject(o);
  }
}