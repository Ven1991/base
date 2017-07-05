package com.webapp.framework.base.service;

import java.util.Date;

import com.webapp.framework.core.beans.DBDialect;

public abstract interface CommonService
{
  public abstract DBDialect getDbTYPE();

  public abstract Date getDate();
}