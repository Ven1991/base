package com.webapp.framework.utils.converter;

import org.apache.commons.beanutils.Converter;

public final class StringConverter implements Converter {
	public Object convert(Class type, Object value) {
		if ((value == null) || ("".equals(value.toString()))) {
			return (String) null;
		}
		return value.toString();
	}
}