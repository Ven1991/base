package com.webapp.framework.core.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.webapp.framework.core.beans.MonthDate;

public class FormatUtil {
	protected static Logger log = LogManager.getLogger(FormatUtil.class);
	public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

	public static String format(String pattern, Double x) {
		if (null == x) {
			x = Double.valueOf(0.0D);
		}
		if (null != pattern) {
			pattern = pattern.replaceAll("@", "#");
		}
		DecimalFormat df = new DecimalFormat(pattern);

		return df.format(x);
	}

	public static String format(String pattern, Long x) {
		if (null == x) {
			x = Long.valueOf(0L);
		}
		if (null != pattern) {
			pattern = pattern.replaceAll("@", "#");
		}
		DecimalFormat df = new DecimalFormat(pattern);

		return df.format(x);
	}

	public static String format(String pattern, Integer x) {
		if (null == x) {
			x = Integer.valueOf(0);
		}
		if (null != pattern) {
			pattern = pattern.replaceAll("@", "#");
		}
		DecimalFormat df = new DecimalFormat(pattern);

		return df.format(x);
	}

	public static String format(String pattern, BigDecimal x) {
		if (null == x) {
			return "0";
		}
		if (null != pattern) {
			pattern = pattern.replaceAll("@", "#");
		}
		DecimalFormat df = new DecimalFormat(pattern);
		return df.format(x);
	}

	public static String format(String pattern, java.util.Date date) {
		if (null == date)
			return null;
		if (null == pattern)
			pattern = "yyyy-MM-dd";
		SimpleDateFormat df = new SimpleDateFormat(pattern);
		return df.format(date);
	}

	public static String format(java.util.Date date) {
		return format(null, date);
	}

	public static String datetime() {
		java.util.Date date = new java.util.Date();
		return format("yyyy.MM.dd HH:mm:ss", date);
	}

	public static String datetime(String format) {
		java.util.Date date = new java.util.Date();
		return format(format, date);
	}

	public static java.util.Date str2date(String sDate, String format) {
		if (null == sDate)
			return null;
		if (null == format) {
			format = "yyyy-MM-dd";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		try {
			return sdf.parse(sDate);
		} catch (ParseException e) {
			log.error("日期[" + sDate + "][" + format + "]转换失败：" + e.getMessage());
		}
		return null;
	}

	public static java.util.Date str2date(String sDate) {
		return str2date(sDate, null);
	}

	public static java.sql.Date str2sqldate(String sDate, String format) {
		return new java.sql.Date(str2date(sDate, format).getTime());
	}

	public static int double2int(double d) throws Exception {
		String value = Double.toString(d);
		if (value.indexOf(".") > 0) {
			value = value.substring(0, value.indexOf("."));
		}
		return Integer.parseInt(value);
	}

	public static Integer long2int(Long l) {
		if (null == l)
			return null;
		String value = Long.toString(l.longValue());
		return Integer.valueOf(Integer.parseInt(value));
	}

	public static Long int2Long(Integer i) {
		if (null == i) {
			return null;
		}
		String value = i.toString();
		return Long.valueOf(Long.parseLong(value));
	}

	public static String format2Price(double d) {
		return format("##,###,###,##0.00", Double.valueOf(d));
	}

	public static String format2PriceInput(double d) {
		return format("##########0.00", Double.valueOf(d));
	}

	public static Double dateIntervalDAY(java.util.Date date1, java.util.Date date2) {
		if ((null == date1) || (null == date2)) {
			return null;
		}
		Long l = Long.valueOf(date1.getTime() - date2.getTime());
		Double d = new Double(86400000.0D);
		return DoubleUtil.precise(Double.valueOf(l.doubleValue()), d, "/");
	}

	public static Double dateIntervalYEAR(java.util.Date date1, java.util.Date date2) {
		if ((null == date1) || (null == date2)) {
			return null;
		}
		Long l = Long.valueOf(date1.getTime() - date2.getTime());
		Double d = new Double(1471228928.0D);
		return DoubleUtil.precise(Double.valueOf(l.doubleValue()), d, "/");
	}

	public static java.util.Date trimTimeFromDate(java.util.Date d) {
		return str2date(format("yyyy.MM.dd", d), "yyyy.MM.dd");
	}

	public static boolean isNumeric(String str) {
		try {
			Double.parseDouble(str);
			return true;
		} catch (Exception e) {
		}
		return false;
	}

	public static boolean checkNumFraction(String str, int len) {
		if (len < 0) {
			return false;
		}
		String mm = "^[0-9]*[1-9][0-9]*$";
		if (len > 0) {
			mm = "^[0-9]+(.[0-9]{0," + len + "})?$";
		}
		Pattern pattern = Pattern.compile(mm);
		return pattern.matcher(str).matches();
	}

	public static java.util.Date dateYearAdd(java.util.Date oDate, int add) {
		if (null == oDate) {
			return oDate;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(oDate);
		calendar.set(1, calendar.get(1) + add);

		return calendar.getTime();
	}

	public static java.util.Date dateMonthAdd(java.util.Date oDate, int add) {
		if (null == oDate) {
			return oDate;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(oDate);
		calendar.set(2, calendar.get(2) + add);

		return calendar.getTime();
	}

	public static java.util.Date dateAdd(java.util.Date oDate, int add) {
		if (null == oDate) {
			return oDate;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(oDate);
		calendar.set(5, calendar.get(5) + add);

		return calendar.getTime();
	}

	public static int getDaysByYearAndMonth(String dmouth) {
		SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM");
		Calendar rightNow = Calendar.getInstance();
		try {
			rightNow.setTime(simpleDate.parse(dmouth));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return rightNow.getActualMaximum(5);
	}

	public static String getWeekOfDateName(java.util.Date dt) {
		String[] weekDays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
		Calendar cal = Calendar.getInstance();
		cal.setTime(dt);
		int w = cal.get(7) - 1;
		if (w < 0)
			w = 0;
		return weekDays[w];
	}

	public static String getWeekOfDateShortName(java.util.Date dt) {
		String[] weekDays = { "日", "一", "二", "三", "四", "五", "六" };
		Calendar cal = Calendar.getInstance();
		cal.setTime(dt);
		int w = cal.get(7) - 1;
		if (w < 0)
			w = 0;
		return weekDays[w];
	}

	public static int getWeekOfDate(java.util.Date dt) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(dt);
		int w = cal.get(7) - 1;
		if (w < 0)
			w = 0;
		return w;
	}

	public static MonthDate getMonthDate(java.util.Date d) {
		MonthDate md = new MonthDate();
		md.setDate(format("dd", d));
		md.setMonth(format("MM", d));
		md.setYear(format("yyyy", d));

		int week = getWeekOfDate(d);
		md.setIsWorkDay((week == 0) || (week == 6) ? 1 : 0);
		md.setWeek(week);
		md.setWeekName(getWeekOfDateName(d));
		md.setWeekShortName(getWeekOfDateShortName(d));
		return md;
	}
}