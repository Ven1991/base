package com.webapp.framework.core.utils;

import java.math.BigDecimal;

public class DoubleUtil {
	public static double preciseAdd(Double a, Double b) {
		if (null == a) {
			a = Double.valueOf(0.0D);
		}
		if (null == b) {
			b = Double.valueOf(0.0D);
		}
		BigDecimal r = new BigDecimal(Double.toString(a.doubleValue()));
		r = r.add(new BigDecimal(Double.toString(b.doubleValue())));
		return r.doubleValue();
	}

	public static double preciseSub(Double a, Double b) {
		if (null == a) {
			a = Double.valueOf(0.0D);
		}
		if (null == b) {
			b = Double.valueOf(0.0D);
		}
		BigDecimal r = new BigDecimal(Double.toString(a.doubleValue()));
		r = r.subtract(new BigDecimal(Double.toString(b.doubleValue())));
		return r.doubleValue();
	}

	public static double preciseMul(Double a, Double b) {
		if ((null == b) || (null == a)) {
			return 0.0D;
		}
		BigDecimal r = new BigDecimal(Double.toString(a.doubleValue()));
		r = r.multiply(new BigDecimal(Double.toString(b.doubleValue())));

		return r.doubleValue();
	}

	public static double preciseDev(Double a, Double b) {
		if ((null == b) || (b.doubleValue() == 0.0D) || (null == a)) {
			return 0.0D;
		}
		BigDecimal r = new BigDecimal(Double.toString(a.doubleValue()));
		r = r.divide(new BigDecimal(Double.toString(b.doubleValue())), 6, 4);
		return r.doubleValue();
	}

	public static double preciseDev(Double a, Double b, int scale) {
		if ((null == b) || (b.doubleValue() == 0.0D) || (null == a)) {
			return 0.0D;
		}
		BigDecimal r = new BigDecimal(Double.toString(a.doubleValue()));
		r = r.divide(new BigDecimal(Double.toString(b.doubleValue())), scale, 4);
		return r.doubleValue();
	}

	public static Double precise(Double a, Double b, String t) throws IllegalArgumentException {
		if (null == a)
			a = Double.valueOf(0.0D);
		if (null == b)
			b = Double.valueOf(0.0D);
		switch (t.charAt(0)) {
		case '+':
			return Double.valueOf(preciseAdd(a, b));
		case '-':
			return Double.valueOf(preciseSub(a, b));
		case '*':
			return Double.valueOf(preciseMul(a, b));
		case '/':
			return Double.valueOf(preciseDev(a, b));
		case ',':
		case '.':
		}
		return null;
	}

	public static Double precise(Double a, Double b, String t, int scale) throws IllegalArgumentException {
		switch (t.charAt(0)) {
		case '+':
			return Double.valueOf(preciseAdd(a, b));
		case '-':
			return Double.valueOf(preciseSub(a, b));
		case '*':
			Double d = Double.valueOf(preciseMul(a, b));
			return Double.valueOf(round(d.doubleValue(), scale));
		case '/':
			return Double.valueOf(preciseDev(a, b, scale));
		case ',':
		case '.':
		}
		return null;
	}

	public static double round(double v, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		BigDecimal b = new BigDecimal(Double.toString(v));
		BigDecimal one = new BigDecimal("1");
		return b.divide(one, scale, 4).doubleValue();
	}
}