package com.webapp.framework.base.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mybatis.spring.support.SqlSessionDaoSupport;

public abstract class AbstractDao extends SqlSessionDaoSupport{
	protected Logger log = LogManager.getLogger(getClass());
	
	
	
	
}