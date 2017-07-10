package com.webapp.framework.base.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webapp.framework.base.dao.BaseDao;
import com.webapp.framework.base.service.BaseService;
import com.webapp.framework.base.service.CommonService;
import com.webapp.framework.core.beans.DBDialect;

@Service("commonService")
public class CommonServiceImpl extends BaseService implements CommonService {

	@Autowired
	private BaseDao baseDao;
	
	
	public DBDialect getDbTYPE() {
		return DBDialect.MYSQL;
	}


	@Override
	public Date getDate() {
		return baseDao.getDate();
	}

}