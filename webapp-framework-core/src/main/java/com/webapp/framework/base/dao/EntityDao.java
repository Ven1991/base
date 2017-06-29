package com.webapp.framework.base.dao;

import java.util.Date;

public abstract interface EntityDao
{
  public abstract void saveOrUpdate(Object paramObject);

  public abstract void save(Object paramObject);

  public abstract void delete(Object paramObject);

  public abstract void update(Object paramObject);

  public abstract Date getDate();
}