package com.webapp.framework.base.beans;

import org.springframework.context.ApplicationContext;

public class BeanFactory
{
  public static <T> T getBean(String beanName)
  {
    return (T) ApplicationContextHolder.getApplicationContext().getBean(beanName);
  }

  public static <T> T getBean(String beanName, Class<T> clazz)
  {
    return (T) ApplicationContextHolder.getApplicationContext().getBean(beanName);
  }
}