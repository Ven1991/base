package com.webapp.framework.base.service;

import java.util.Date;

import com.webapp.framework.base.dao.BaseDao;

public abstract class BaseService extends AbstractService {
	protected abstract BaseDao getEntityDao();

	public Date getDate() {
		return getEntityDao().getDate();
	}
}
