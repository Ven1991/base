package com.webapp.framework.base.service;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.webapp.framework.base.beans.BeanUtil;

@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
public abstract class AbstractService{
  protected Log log = LogFactory.getLog(getClass());

  public abstract Date getDate();

  protected void copyProperties(Object target, Object source)
  {
    BeanUtil.copyProperties(target, source);
  }

  protected <T> T copyProperties(Class<T> destClass, Object orig)
  {
    return BeanUtil.copyProperties(destClass, orig);
  }
}