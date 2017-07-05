package com.webapp.framework.base.common.utils.converter;

import java.lang.reflect.Constructor;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.lang.StringUtils;

public class DateConvertUtils {
	public static Date parse(String dateString, String dateFormat) {
		return parse(dateString, dateFormat, Date.class);
	}

	public static <T extends Date> T parse(String dateString, String dateFormat, Class<T> targetResultType) {
		if (StringUtils.isEmpty(dateString))
			return null;
		DateFormat df = new SimpleDateFormat(dateFormat);
		try {
			long time = df.parse(dateString).getTime();
			// return (Date)targetResultType.getConstructor(new Class[] {
			// Long.TYPE }).newInstance(new Object[] { Long.valueOf(time) });
			return (T) targetResultType.getConstructor(new Class[] { Long.TYPE })
					.newInstance(new Object[] { Long.valueOf(time) });

		} catch (ParseException e) {
			String errorInfo = "cannot use dateformat:" + dateFormat + " parse datestring:" + dateString;
			throw new IllegalArgumentException(errorInfo, e);
		} catch (Exception e) {
			throw new IllegalArgumentException("error targetResultType:" + targetResultType.getName(), e);
		}
	}

	public static String format(Date date, String dateFormat) {
		if (date == null)
			return null;
		return new SimpleDateFormat(dateFormat).format(date);
	}
}