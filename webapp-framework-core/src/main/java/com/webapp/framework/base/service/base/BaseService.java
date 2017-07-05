package com.webapp.framework.base.service.base;

import java.util.Date;

import org.springframework.dao.DataAccessException;

import com.webapp.framework.base.dao.EntityDao;
import com.webapp.framework.base.service.AbstractService;

public abstract class BaseService extends AbstractService {
	protected abstract EntityDao getEntityDao();

	protected void saveOrUpdate(Object entity) throws DataAccessException {
		getEntityDao().saveOrUpdate(entity);
	}

	protected void save(Object entity) throws DataAccessException {
		getEntityDao().save(entity);
	}

	protected void delete(Object entity) {
		getEntityDao().delete(entity);
	}

	protected void update(Object entity) throws DataAccessException {
		getEntityDao().update(entity);
	}

	public Date getDate() {
		return getEntityDao().getDate();
	}

}