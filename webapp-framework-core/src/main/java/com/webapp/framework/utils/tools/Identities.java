package com.webapp.framework.utils.tools;

import java.security.SecureRandom;
import java.util.UUID;

public class Identities
{
  private static SecureRandom random = new SecureRandom();

  public static String uuid()
  {
    return UUID.randomUUID().toString();
  }

  public static String uuid2()
  {
    return UUID.randomUUID().toString().replaceAll("-", "");
  }

  public static long randomLong()
  {
    return Math.abs(random.nextLong());
  }
}