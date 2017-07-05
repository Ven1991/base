package com.webapp.framework.base.common.utils.converter;

import java.lang.reflect.InvocationTargetException;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;

public class DateConvertRegiser extends BeanUtils {
	public static void copyProperties(Object dest, Object orig) {
		try {
			BeanUtils.copyProperties(dest, orig);
		} catch (IllegalAccessException ex) {
			ex.printStackTrace();
		} catch (InvocationTargetException ex) {
			ex.printStackTrace();
		}
	}

	static {
		ConvertUtils.register(new DateConvert(), java.util.Date.class);
		ConvertUtils.register(new DateConvert(), java.sql.Date.class);
	}
}