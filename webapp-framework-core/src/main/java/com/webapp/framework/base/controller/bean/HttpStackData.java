package com.webapp.framework.base.controller.bean;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.ui.ModelMap;

public class HttpStackData
{
  private HttpServletRequest request;
  private HttpServletResponse response;
  private ModelMap modelMap;
  private Map<Object, Object> data;

  public HttpStackData()
  {
    this.data = new HashMap();
  }

  public void putData(Object key, Object value) {
    this.data.put(key, value);
  }

  public Object getData(Object key) {
    return this.data.get(key);
  }

  public HttpStackData(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) {
    this();

    this.request = request;
    this.response = response;
    this.modelMap = modelMap;
  }

  public HttpServletRequest getRequest() {
    return this.request;
  }

  public void setRequest(HttpServletRequest request) {
    this.request = request;
  }

  public HttpServletResponse getResponse() {
    return this.response;
  }

  public void setResponse(HttpServletResponse response) {
    this.response = response;
  }

  public ModelMap getModelMap() {
    return this.modelMap;
  }

  public void setModelMap(ModelMap modelMap) {
    this.modelMap = modelMap;
  }
}