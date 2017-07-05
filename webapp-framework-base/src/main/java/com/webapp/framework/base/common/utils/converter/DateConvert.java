package com.webapp.framework.base.common.utils.converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

public class DateConvert implements Converter {
	public Object convert(Class arg0, Object arg1) {
		String p = StringUtils.trim(getDate(arg1));
		if (StringUtils.isBlank(p)) {
			return p;
		}
		String[] patterns = { "yyyy-MM-dd HH:mm:ss.SSS", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd", "yyyy.MM.dd HH:mm:ss",
				"yyyy.MM.dd", "yyyyMMdd" };
		Date date = null;
		try {
			date = DateUtils.parseDate(p, patterns);
		} catch (ParseException e) {
			try {
				SimpleDateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);
				return df.parse(p);
			} catch (Exception e2) {
				try {
					return new Date(p);
				} catch (Exception e3) {
					new RuntimeException("not date or not support date ", e3);
				}
			}
		}
		return date;
	}

	private String getDate(Object arg1) {
		if ((null == arg1) || ("".equals(arg1)))
			return null;
		try {
			Date d = (Date) arg1;
			return d.toString();
		} catch (Exception e) {
		}
		return (String) arg1;
	}
}