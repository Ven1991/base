package com.webapp.framework.utils.tools;

import java.io.StringWriter;
import java.net.URL;
import java.util.Map;
import java.util.Properties;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.log.NullLogChute;

public class HtmlCreate
{
  public static String getHtmlFromTemplate(String templatePage, Map<String, Object> map)
  {
    VelocityContext context = new VelocityContext();

    if (null != map) {
      for (String key : map.keySet()) {
        Object value = map.get(key);
        context.put(key, value);
      }
    }

    StringWriter writer = new StringWriter();
    getTemplate(templatePage).merge(context, writer);

    return writer.toString();
  }

  private static Template getTemplate(String templatePage)
  {
    VelocityEngine ve = new VelocityEngine();
    URL url = HtmlCreate.class.getResource("/config/velocity.properties");
    if (null == url) {
      url = HtmlCreate.class.getResource("/vm/velocity.properties");
    }
    Properties p = new Properties();
    try {
      p.load(url.openStream());
    }
    catch (Exception e) {
      p.setProperty("input.encoding", "UTF-8");
      p.setProperty("output.encoding", "UTF-8");
      p.setProperty("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
    }

    ve.setProperty("runtime.log.logsystem", new NullLogChute());

    ve.init(p);
    return ve.getTemplate(templatePage);
  }
}