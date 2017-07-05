package com.webapp.framework.base.listener;

import java.io.File;
import java.text.MessageFormat;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.springframework.web.context.ContextLoaderListener;

import com.webapp.framework.core.utils.StringUtil;


public class LoggerConfigListener extends ContextLoaderListener
{
  protected static final String DEFAULT_WEB_APP_NAME = "webapp.name";

  public void contextInitialized(ServletContextEvent event)
  {
    ServletContext context = event.getServletContext();

    String logDir = System.getProperty("jboss.server.log.dir");
    if (StringUtil.isNull(logDir)) {
      String serverHome = System.getProperty("catalina.home");
      if (StringUtil.isNull(serverHome)) {
        serverHome = System.getProperty("user.home");
      }
      logDir = serverHome + File.separator + "logs";
      System.setProperty("jboss.server.log.dir", logDir);
    }

    if (StringUtil.isNull(logDir)) {
      String appPath = context.getRealPath("/");
      logDir = MessageFormat.format(appPath + "{0}..{0}..{0}log", new Object[] { File.separator });
    }

    super.contextInitialized(event);
  }

  public void contextDestroyed(ServletContextEvent event)
  {
    super.contextDestroyed(event);
  }
}