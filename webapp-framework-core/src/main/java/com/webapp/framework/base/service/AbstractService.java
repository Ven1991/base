package com.webapp.framework.base.service;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.webapp.framework.base.beans.BeanUtil;

@Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
public abstract class AbstractService {
	protected Logger log = LogManager.getLogger(getClass());

	public abstract Date getDate();

	protected void copyProperties(Object target, Object source) {
		BeanUtil.copyProperties(target, source);
	}

	protected <T> T copyProperties(Class<T> destClass, Object orig) {
		return BeanUtil.copyProperties(destClass, orig);
	}
}