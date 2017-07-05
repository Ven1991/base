package com.webapp.framework.base.common.utils.sql;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.webapp.framework.base.beans.BeanFactory;
import com.webapp.framework.base.service.CommonService;

public class DbDate {
	protected static Log log = LogFactory.getLog(DbDate.class);

	public static Date getDate() {
		try {
			CommonService commonService = (CommonService) BeanFactory.getBean("commonService");
			return commonService.getDate();
		} catch (Exception e) {
			log.error("获取数据库日期失败：" + e.getMessage());
		}
		return new Date();
	}
}