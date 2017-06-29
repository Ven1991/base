package com.webapp.framework.utils.tools;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class NetWorkTools
{
  public static String[] getAllLocalHostIP()
  {
    String[] ret = null;
    try {
      String hostName = getLocalHostName();
      if (hostName.length() > 0) {
        InetAddress[] addrs = InetAddress.getAllByName(hostName);
        if (addrs.length > 0) {
          ret = new String[addrs.length];
          for (int i = 0; i < addrs.length; i++)
            ret[i] = addrs[i].getHostAddress();
        }
      }
    }
    catch (Exception ex) {
      ret = null;
    }
    return ret;
  }

  public static String getLocalHostName()
  {
    String hostName;
    try
    {
      InetAddress addr = InetAddress.getLocalHost();
      hostName = addr.getHostName();
    }
    catch (Exception ex)
    {
//      String hostName;
      hostName = "";
    }
    return hostName;
  }

  private static String hexByte(byte b)
  {
    String s = Integer.toHexString(b);
    int len = s.length();
    for (int i = len; i < 8; i++) {
      s = "0" + s;
    }
    return s.substring(6).toUpperCase();
  }

  public static List<String> getMacs()
  {
    List list = new ArrayList();
    try {
      Enumeration el = NetworkInterface.getNetworkInterfaces();
      while (el.hasMoreElements()) {
        byte[] mac = ((NetworkInterface)el.nextElement()).getHardwareAddress();

        if ((mac != null) && (mac.length != 0))
        {
          StringBuffer sb = new StringBuffer();
          for (byte b : mac) {
            sb.append(hexByte(b));
            sb.append("-");
          }
          sb.deleteCharAt(sb.length() - 1);
          list.add(sb.toString());
        }
      }
    } catch (Exception e) { e.printStackTrace(); }

    return list;
  }

  public static String getMac()
  {
    List list = getMacs();
    if ((null == list) || (list.isEmpty()))
      return null;
    return (String)list.get(0);
  }
}