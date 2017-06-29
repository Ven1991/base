package com.webapp.framework.base.application;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

public abstract class InitSystemBaseListener
  implements ApplicationListener<ApplicationEvent>
{
  protected Log log = LogFactory.getLog(getClass());
  private static boolean isStart = false;

  public void onApplicationEvent(ApplicationEvent event)
  {
    if (isStart) return;
    isStart = true;
    init();
  }

  protected abstract void init();
}