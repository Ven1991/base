package com.webapp.framework.utils.file.ipseeker.util;

public class IPEntry
{
  public String beginIp;
  public String endIp;
  public String country;
  public String area;

  public IPEntry()
  {
    this.beginIp = (this.endIp = this.country = this.area = "");
  }
}