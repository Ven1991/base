package com.webapp.framework.core.exception;

import com.webapp.framework.core.utils.StringUtil;

public class BaseException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public static void throwException(String msg) {
		throw new BaseException(msg);
	}

	public static void throwException(String msg, Object[] values) {
		if (null == msg)
			throwException(msg);
		throwException(StringUtil.replace(msg, values));
	}

	public BaseException(String mess) {
		super(mess);
	}

	public BaseException(String mess, Exception ex) {
		super(mess, ex);
	}
}