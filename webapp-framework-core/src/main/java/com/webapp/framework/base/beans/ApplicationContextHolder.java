package com.webapp.framework.base.beans;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ApplicationContextHolder
  implements ApplicationContextAware
{
  private static Log log = LogFactory.getLog(ApplicationContextHolder.class);
  private static ApplicationContext applicationContext;

  public void setApplicationContext(ApplicationContext context)
    throws BeansException
  {
    if (applicationContext != null) {
      throw new IllegalStateException("ApplicationContextHolder already holded 'applicationContext'.");
    }

    applicationContext = context;
    log.info("holded applicationContext,displayName:" + applicationContext.getDisplayName());
  }

  public static ApplicationContext getApplicationContext() {
    if (applicationContext == null) {
      throw new IllegalStateException("'applicationContext' property is null,ApplicationContextHolder not yet init.");
    }
    return applicationContext;
  }

  public static <T> T getBean(String beanName)
  {
    return (T) getApplicationContext().getBean(beanName);
  }

  public static <T> T getBean(String beanName, Class<T> clazz)
  {
    return (T) getApplicationContext().getBean(beanName);
  }

  public static void cleanHolder() {
    applicationContext = null;
  }

  public static <T> T getBean(Class<T> requiredType)
  {
    return applicationContext.getBean(requiredType);
  }

  public static void clearHolder()
  {
    log.debug("清除SpringContextHolder中的ApplicationContext:" + applicationContext);
    applicationContext = null;
  }

  public void destroy() throws Exception {
    clearHolder();
  }
}