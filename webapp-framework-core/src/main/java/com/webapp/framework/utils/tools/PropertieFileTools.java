package com.webapp.framework.utils.tools;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class PropertieFileTools
{
  public static Map<String, String> load(String file)
  {
    Properties prop = new Properties();
    InputStream in = PropertieFileTools.class.getResourceAsStream(file);
    try
    {
      prop.load(in);
    } catch (IOException e) {
      return null;
    }

    Map map = new HashMap();
    Set keyValue = prop.keySet();
    for (Iterator it = keyValue.iterator(); it.hasNext(); ) {
      String key = (String)it.next();
      String value = prop.getProperty(key);
      map.put(key, value);
    }

    return map;
  }
}