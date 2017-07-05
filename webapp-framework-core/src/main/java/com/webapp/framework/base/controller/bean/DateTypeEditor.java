package com.webapp.framework.base.controller.bean;

import java.beans.PropertyEditorSupport;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.webapp.framework.utils.tools.StringUtil;

public class DateTypeEditor extends PropertyEditorSupport {
	public static final DateFormat DF_LONG = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static final DateFormat DF_SHORT = new SimpleDateFormat("yyyy-MM-dd");
	public static final DateFormat DF_YEAR = new SimpleDateFormat("yyyy");
	public static final DateFormat DF_MONTH = new SimpleDateFormat("yyyy-MM");
	public static final int SHORT_DATE = 10;
	public static final int YEAR_DATE = 4;
	public static final int MONTH_DATE = 7;

	public void setAsText(String text) throws IllegalArgumentException {
		if (StringUtil.isNull(text)) {
			setValue(null);
			return;
		}
		try {
			text = text.trim();
			if (text.length() <= 4)
				setValue(new java.sql.Date(DF_YEAR.parse(text).getTime()));
			else if (text.length() <= 7)
				setValue(new java.sql.Date(DF_MONTH.parse(text).getTime()));
			else if (text.length() <= 10)
				setValue(new java.sql.Date(DF_SHORT.parse(text).getTime()));
			else
				setValue(new Timestamp(DF_LONG.parse(text).getTime()));
		} catch (ParseException ex) {
			IllegalArgumentException iae = new IllegalArgumentException("Could not parse date: " + ex.getMessage());
			iae.initCause(ex);
			throw iae;
		}
	}

	public String getAsText() {
		java.util.Date value = (java.util.Date) getValue();
		return value != null ? DF_LONG.format(value) : "";
	}
}