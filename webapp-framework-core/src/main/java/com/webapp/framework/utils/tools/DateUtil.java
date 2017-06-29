package com.webapp.framework.utils.tools;

import java.io.PrintStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DateUtil
{
  public static final String DEF_DATE_FORMAT = "yyyy/MM/dd";
  public static final String DEF_TIME_FORMAT = "kk:mm";
  public static final String DEF_DATE_TIME = "yyyy/MM/dd HH:mm";
  public static final String ORA_DATE_FORMAT = "yyyy-MM-dd";
  public static final String ORA_TIME_FORMAT = "HH24:MI";
  public static final String DATE_TIME = "yyyy-MM-dd HH:mm:ss";
  public static final String ORA_DATE_TIME = "yyyy-MM-DD HH24:MI";
  public static final String ORA_DATE_TIME_FORMAT = "yyyyMMddHHMM";
  public static final String G_DATE_FORMAT = "yyyy-MM-dd";
  public static final String[] dayNames = { "日", "一", "二", "三", "四", "五", "六" };

  public static final String[] dateFormats = { "yyyy-MM-dd", "yyyyMMdd", "yyyy.MM.dd", "yyyy/MM/dd", "yyyy年MM月dd日" };

  private static Map<String, DateFormat> map = new HashMap();
  private static String lock = "lock";

  public static DateFormat getDateFormat(String pattern, Locale[] arg)
  {
    DateFormat instance = (DateFormat)map.get(pattern);
    if (instance == null) {
      synchronized (lock) {
        instance = (DateFormat)map.get(pattern);
        if (instance == null) {
          if ((null != arg) && (arg.length > 0))
            instance = new SimpleDateFormat(pattern, arg[0]);
          else
            instance = new SimpleDateFormat(pattern);
          map.put(pattern, instance);
        }
      }
    }
    return instance;
  }

  public static Long strToLong(String dateStr)
  {
    try
    {
      ParsePosition pos = new ParsePosition(0);
      java.util.Date tempDate = getDateFormat("yyyy/MM/dd", new Locale[0]).parse(dateStr, pos);
      return new Long(tempDate.getTime()); } catch (Exception e) {
    }
    return null;
  }

  public static long getBetweenDays(String endDate, String startDate)
  {
    if ((endDate == null) || (startDate == null))
      return -1L;
    if ((endDate.length() < 10) || (startDate.length() < 10))
      return -1L;
    int startYear = 2001; int startMonth = 12; int startDay = 31;
    int endYear = 2001; int endMonth = 12; int endDay = 31;
    try
    {
      startYear = Integer.parseInt(endDate.substring(0, 4));
      startMonth = Integer.parseInt(endDate.substring(5, 7));
      startDay = Integer.parseInt(endDate.substring(8, 10));
      endYear = Integer.parseInt(startDate.substring(0, 4));
      endMonth = Integer.parseInt(startDate.substring(5, 7));
      endDay = Integer.parseInt(startDate.substring(8, 10));
    } catch (NumberFormatException localNumberFormatException) {
    }
    catch (Exception localException) {
    }
    Calendar startCalendar = Calendar.getInstance();
    Calendar endCalendar = Calendar.getInstance();
    startCalendar.set(startYear, startMonth - 1, startDay, 1, 0, 0);
    endCalendar.set(endYear, endMonth - 1, endDay, 0, 0, 0);
    java.util.Date endDateTemp = startCalendar.getTime();
    java.util.Date startDateTemp = endCalendar.getTime();
    long startTime = endDateTemp.getTime();
    long endTime = startDateTemp.getTime();
    long times = startTime - endTime;

    long days = times / 86400000L;
    return days;
  }

  public static long getBetweenDays(java.util.Date endDate, java.util.Date startDate)
  {
    if ((endDate == null) || (startDate == null))
      return -1L;
    long startTime = startDate.getTime();
    long endTime = endDate.getTime();
    long times = endTime - startTime;
    long days = times / 86400000L;
    return days;
  }

  public static String getAfterDate(String baseDate, int dayCount)
  {
    int year = Integer.parseInt(baseDate.substring(0, 4));
    int month = Integer.parseInt(baseDate.substring(5, 7));
    int date = Integer.parseInt(baseDate.substring(8, 10));

    Calendar calendar = Calendar.getInstance();
    calendar.set(year, month - 1, date);
    calendar.add(5, dayCount);
    java.util.Date _date = calendar.getTime();
    String dateString = getDateFormat("yyyy-MM-dd", new Locale[0]).format(_date);

    return dateString;
  }

  public static java.util.Date getAfterDate(java.util.Date baseDate, int dayCount)
  {
    if (null == baseDate) {
      return null;
    }
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(baseDate);
    calendar.add(5, dayCount);
    return calendar.getTime();
  }

  public static String getAfterDate(String baseDate, int dayCount, String patternString) {
    int year = Integer.parseInt(baseDate.substring(0, 4));
    int month = Integer.parseInt(baseDate.substring(5, 7));
    int date = Integer.parseInt(baseDate.substring(8, 10));

    Calendar calendar = Calendar.getInstance();
    calendar.set(year, month - 1, date);
    calendar.add(5, dayCount);
    java.util.Date _date = calendar.getTime();
    String dateString = getDateFormat(patternString, new Locale[0]).format(_date);

    return dateString;
  }

  public static String parse(String baseDate)
  {
    String dateString = baseDate;
    baseDate = baseDate.trim().toLowerCase();
    try {
      if (baseDate.length() == 11) {
        dateString = baseDate.substring(7);
        if (baseDate.substring(3, 6).equals("jan"))
          dateString = dateString + "-01-" + baseDate.substring(0, 2);
        else if (baseDate.substring(3, 6).equals("feb"))
          dateString = dateString + "-02-" + baseDate.substring(0, 2);
        else if (baseDate.substring(3, 6).equals("mar"))
          dateString = dateString + "-03-" + baseDate.substring(0, 2);
        else if (baseDate.substring(3, 6).equals("apr"))
          dateString = dateString + "-04-" + baseDate.substring(0, 2);
        else if (baseDate.substring(3, 6).equals("may"))
          dateString = dateString + "-05-" + baseDate.substring(0, 2);
        else if (baseDate.substring(3, 6).equals("jun"))
          dateString = dateString + "-06-" + baseDate.substring(0, 2);
        else if (baseDate.substring(3, 6).equals("jul"))
          dateString = dateString + "-07-" + baseDate.substring(0, 2);
        else if (baseDate.substring(3, 6).equals("aug"))
          dateString = dateString + "-08-" + baseDate.substring(0, 2);
        else if (baseDate.substring(3, 6).equals("sep"))
          dateString = dateString + "-09-" + baseDate.substring(0, 2);
        else if (baseDate.substring(3, 6).equals("oct"))
          dateString = dateString + "-10-" + baseDate.substring(0, 2);
        else if (baseDate.substring(3, 6).equals("nov"))
          dateString = dateString + "-11-" + baseDate.substring(0, 2);
        else if (baseDate.substring(3, 6).equals("dec"))
          dateString = dateString + "-12-" + baseDate.substring(0, 2);
        else
          dateString = baseDate;
      } else if (baseDate.length() > 11) {
        dateString = baseDate.substring(7, 11);
        if (baseDate.substring(3, 6).equals("jan"))
          dateString = dateString + "-01-" + baseDate.substring(0, 2);
        else if (baseDate.substring(3, 6).equals("feb"))
          dateString = dateString + "-02-" + baseDate.substring(0, 2);
        else if (baseDate.substring(3, 6).equals("mar"))
          dateString = dateString + "-03-" + baseDate.substring(0, 2);
        else if (baseDate.substring(3, 6).equals("apr"))
          dateString = dateString + "-04-" + baseDate.substring(0, 2);
        else if (baseDate.substring(3, 6).equals("may"))
          dateString = dateString + "-05-" + baseDate.substring(0, 2);
        else if (baseDate.substring(3, 6).equals("jun"))
          dateString = dateString + "-06-" + baseDate.substring(0, 2);
        else if (baseDate.substring(3, 6).equals("jul"))
          dateString = dateString + "-07-" + baseDate.substring(0, 2);
        else if (baseDate.substring(3, 6).equals("aug"))
          dateString = dateString + "-08-" + baseDate.substring(0, 2);
        else if (baseDate.substring(3, 6).equals("sep"))
          dateString = dateString + "-09-" + baseDate.substring(0, 2);
        else if (baseDate.substring(3, 6).equals("oct"))
          dateString = dateString + "-10-" + baseDate.substring(0, 2);
        else if (baseDate.substring(3, 6).equals("nov"))
          dateString = dateString + "-11-" + baseDate.substring(0, 2);
        else if (baseDate.substring(3, 6).equals("dec"))
          dateString = dateString + "-12-" + baseDate.substring(0, 2);
        else
          dateString = baseDate;
        if (!dateString.equals(baseDate))
          dateString = dateString + baseDate.substring(11);
      }
    }
    catch (Exception ex) {
      dateString = baseDate;
    }

    return dateString;
  }

  public static String getBeforeDate(String baseDate, int dayCount)
  {
    return getAfterDate(baseDate, -dayCount);
  }

  public static String getAfterMonth(String baseDate, int monthCount)
  {
    int year = Integer.parseInt(baseDate.substring(0, 4));
    int month = Integer.parseInt(baseDate.substring(5, 7));
    int date = Integer.parseInt(baseDate.substring(8, 10));

    Calendar calendar = Calendar.getInstance();
    calendar.set(year, month - 1, date);
    calendar.add(2, monthCount);
    java.util.Date _date = calendar.getTime();
    String dateString = getDateFormat("yyyy-MM-dd", new Locale[0]).format(_date);

    return dateString;
  }

  public static java.util.Date getAfterMonth(java.util.Date date, int monthCount)
  {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.add(2, monthCount);
    return calendar.getTime();
  }

  public static String getBeforeMonth(String baseDate, int monthCount)
  {
    return getAfterMonth(baseDate, -monthCount);
  }

  public static String getEndDate(String baseDate, int monthCount)
  {
    int day = Integer.parseInt(baseDate.substring(8, 10));

    String endDate = getAfterMonth(baseDate, monthCount);

    int endDay = Integer.parseInt(endDate.substring(8, 10));

    if (endDay == day)
    {
      if (monthCount > 0)
        endDate = getAfterDate(endDate, -1);
      else {
        endDate = getAfterDate(endDate, 1);
      }
    }
    else if (monthCount < 0) {
      endDate = getAfterDate(endDate, 1);
    }

    return endDate;
  }

  public static java.util.Date getDate(String baseDate)
  {
    if ((baseDate == null) || (baseDate.length() == 0))
      return null;
    int year = Integer.parseInt(baseDate.substring(0, 4));
    int month = Integer.parseInt(baseDate.substring(5, 7));
    int date = Integer.parseInt(baseDate.substring(8, 10));
    int hour = 0;
    int minute = 0;
    int second = 0;
    if (baseDate.length() >= 13)
      hour = Integer.parseInt(baseDate.substring(11, 13));
    if (baseDate.length() >= 16)
      minute = Integer.parseInt(baseDate.substring(14, 16));
    if (baseDate.length() >= 19) {
      second = Integer.parseInt(baseDate.substring(17, 19));
    }
    Calendar calendar = Calendar.getInstance();
    calendar.set(year, month - 1, date, hour, minute, second);
    java.util.Date _date = calendar.getTime();

    return _date;
  }

  public static java.util.Date getDateSmall(String baseDate)
  {
    if ((baseDate == null) || (baseDate.length() == 0))
      return null;
    int year = Integer.parseInt(baseDate.substring(0, 4));
    int month = Integer.parseInt(baseDate.substring(4, 6));
    int date = Integer.parseInt(baseDate.substring(6));
    int hour = 0;
    int minute = 0;
    int second = 0;
    Calendar calendar = Calendar.getInstance();
    calendar.set(year, month - 1, date, hour, minute, second);
    java.util.Date _date = calendar.getTime();

    return _date;
  }

  public static java.sql.Date getSqlDate(String baseDate) {
    if ((baseDate == null) || (baseDate.length() == 0))
      return null;
    java.util.Date date = getDate(baseDate);
    return new java.sql.Date(date.getTime());
  }

  public static String getDateString(int year, int month, int date, String patternString)
  {
    String dateString = "";
    Calendar calendar = Calendar.getInstance();
    calendar.set(year, month - 1, date);
    java.util.Date showDate = calendar.getTime();
    dateString = getDateFormat(patternString, new Locale[0]).format(showDate);
    return dateString;
  }

  public static String getDateStringNotDefault(java.util.Date _date, String patternString)
  {
    if ((null == _date) || (StringUtil.isNull(patternString))) {
      return "";
    }
    return getDateFormat(patternString, new Locale[0]).format(_date);
  }

  public static String getDateString(String year, String month, String date, String patternString)
  {
    return getDateString(year, month, date, patternString, true);
  }

  public static String getDateString(String year, String month, String date, String patternString, boolean hasDefault) {
    String dateString = "";
    if (hasDefault)
      dateString = getDateFormat(patternString, new Locale[0]).format(new java.util.Date());
    try {
      int y = Integer.parseInt(year);
      int m = Integer.parseInt(month);
      int d = Integer.parseInt(date);
      dateString = getDateString(y, m, d, patternString);
    }
    catch (Exception localException) {
    }
    return dateString;
  }

  public static String getDateString(String date, String patternString) {
    if (date == null)
      return "";
    if (date.length() < 10)
      return "";
    return getDateFormat(patternString, new Locale[] { Locale.ENGLISH }).format(getDate(date));
  }

  public static String getDateString(java.util.Date _date, String patternString)
  {
    if (_date == null) {
      _date = new java.util.Date();
    }
    if (StringUtil.isNull(patternString));
    return getDateFormat(patternString, new Locale[0]).format(_date);
  }

  public static String getBeforeMonth(int monthCount)
  {
    Calendar calendar = Calendar.getInstance();
    calendar.add(2, monthCount);
    java.util.Date _date = calendar.getTime();
    return getDateFormat("yyyy-MM-dd", new Locale[0]).format(_date);
  }

  public static String getHoursOfDay(String day) {
    String st = "";
    try {
      st = Float.parseFloat(day) * 24.0F + "";
    } catch (NumberFormatException localNumberFormatException) {
    }
    return st;
  }

  public static String getDayOfHours(String hours) {
    String st = "";
    try {
      st = Float.parseFloat(hours) / 24.0D + "";
    } catch (NumberFormatException localNumberFormatException) {
    }
    return st;
  }

  public static String getAfterToday(int dayCount, String patternString)
  {
    SimpleDateFormat formatter = new SimpleDateFormat(patternString);
    Calendar calendar = Calendar.getInstance();

    calendar.add(5, dayCount);
    java.util.Date _date = calendar.getTime();
    String dateString = formatter.format(_date);

    return dateString;
  }

  public static java.util.Date getDateByString(String dateStr)
  {
    try
    {
      return getDate(dateStr, "yyyy-MM-dd"); } catch (Exception e) {
    }
    return null;
  }

  public static String getCurrentDate(String patten) throws Exception
  {
    String date = getDateFormat(patten, new Locale[0]).format(new java.util.Date(System.currentTimeMillis()));
    return date;
  }

  public static String getDateStr(String patten) {
    String date = "";
    date = getDateFormat(patten, new Locale[0]).format(new java.util.Date(System.currentTimeMillis()));
    return date;
  }

  public static java.util.Date getDate(String dateStr, String patten)
  {
    try
    {
      return getDateFormat(patten, new Locale[0]).parse(dateStr);
    } catch (ParseException localParseException) {
    }
    return null;
  }

  public static long getDateOfLong(String dateStr, String patten)
    throws Exception
  {
    return getDate(dateStr, patten).getTime();
  }

  public static String getDateOfString(Long dateLong, String patten)
    throws Exception
  {
    if (dateLong != null) {
      return getDateFormat(patten, new Locale[0]).format(new java.util.Date(dateLong.longValue())).toString();
    }
    return "";
  }

  public static long getDateOfLong(String dateStr)
    throws Exception
  {
    java.util.Date dt = getDateByString(dateStr);
    return dt.getTime();
  }

  public static long getCurrenTimeOfLong()
  {
    Calendar calendar = Calendar.getInstance();
    return calendar.getTime().getTime();
  }

  public static java.sql.Date UtilDateToSQLDate(java.util.Date date)
  {
    return new java.sql.Date(date.getTime());
  }

  public static java.util.Date isDate(String dateStr)
  {
    String date_format_1 = "yyyy/MM/dd";
    String date_format_2 = "yyyy-MM-dd";
    String date_format_3 = "yyyyMMdd";
    String date_format_4 = "yyyy.MM.dd";
    String[] date_format = { date_format_1, date_format_2, date_format_3, date_format_4 };
    for (int i = 0; i < date_format.length; i++)
      try {
        DateFormat formatDate = getDateFormat(date_format[i], new Locale[0]);
        formatDate.setLenient(false);
        ParsePosition pos = new ParsePosition(0);
        java.util.Date tempDate = formatDate.parse(dateStr, pos);
        tempDate.getTime();
        return tempDate;
      }
      catch (Exception localException) {
      }
    return null;
  }

  public static Calendar getCalendar(java.util.Date d)
  {
    Calendar cal = Calendar.getInstance();
    cal.setTime(d);
    return cal;
  }

  public static String getDayOfWeek(String dateStr)
  {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(getDate(dateStr, "yyyy-MM-dd"));
    int dayOfWeek = calendar.get(7);
    return dayNames[(dayOfWeek - 1)];
  }

  public static int getWeekOfDate(java.util.Date dt)
  {
    Calendar cal = Calendar.getInstance();
    cal.setTime(dt);

    int w = cal.get(7) - 1;
    if (w < 0)
      w = 0;
    return w;
  }

  public static Long getHMSLong(Calendar c)
  {
    int ap = c.get(9);
    int h = c.get(10);
    if (ap == 1)
      h += 12;
    int m = c.get(12);
    int s = c.get(13);
    return Long.valueOf(s + m * 60 + h * 60 * 60);
  }

  public static boolean compare(String hm) throws Exception {
    java.util.Date dat = getDate(getDateString(new java.util.Date(), "yyyyMMdd") + " " + hm, "yyyyMMdd HH:mm");
    if (new java.util.Date().getTime() > dat.getTime()) {
      return true;
    }
    return false;
  }

  public static String getDateString(java.util.Date _date)
  {
    String dateString = "";
    if (_date != null) {
      dateString = getDateFormat("yyyy-MM-dd", new Locale[0]).format(_date);
    }
    return dateString;
  }

  public static String getDiminishTime(java.util.Date start, java.util.Date end)
  {
    if (!start.before(end)) {
      return "0";
    }
    String str = "";
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    long l = end.getTime() - start.getTime();
    long day = l / 86400000L;
    long hour = l / 3600000L - day * 24L;
    long min = l / 60000L - day * 24L * 60L - hour * 60L;
    long s = l / 1000L - day * 24L * 60L * 60L - hour * 60L * 60L - min * 60L;

    if (day > 0L) {
      str = str + day + "天";
    }
    if (hour > 0L) {
      str = str + hour + "小时";
    }
    if (min > 0L) {
      str = str + min + "分";
    }
    if (day > 100L) {
      str = "长期有效";
    }
    return str;
  }

  public static long diminishTime(String lx, java.util.Date start, java.util.Date end)
  {
    long day = 0L;
    if (lx.equals("date"))
    {
      day = (start.getTime() - end.getTime()) / 86400000L > 0L ? (start.getTime() - end.getTime()) / 86400000L : 
        (end
        .getTime() - start.getTime()) / 86400000L;
    }
    else if (lx.equalsIgnoreCase("HH"))
    {
      day = (start.getTime() - end.getTime()) / 3600000L > 0L ? (start.getTime() - end.getTime()) / 3600000L : 
        (end
        .getTime() - start.getTime()) / 3600000L;
    }
    else if (lx.equalsIgnoreCase("mm"))
    {
      day = (start.getTime() - end.getTime()) / 60000L > 0L ? (start.getTime() - end.getTime()) / 60000L : 
        (end
        .getTime() - start.getTime()) / 60000L;
    }
    else
    {
      day = (start.getTime() - end.getTime()) / 1000L > 0L ? (start.getTime() - end.getTime()) / 1000L : 
        (end
        .getTime() - start.getTime()) / 1000L;
    }

    return day;
  }

  public static java.util.Date getAfterSecond(java.util.Date baseDate, int second)
  {
    if (null == baseDate) {
      return null;
    }
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(baseDate);
    calendar.add(13, second);
    return calendar.getTime();
  }

  public static int getBetweenMonths(java.util.Date date1, java.util.Date date2)
  {
    Calendar cal1 = Calendar.getInstance();
    cal1.setTime(date1);
    Calendar cal2 = Calendar.getInstance();
    cal2.setTime(date2);

    int c = (cal1.get(1) - cal2.get(1)) * 12 + cal1.get(2) - cal2
      .get(2);

    return c;
  }

  public static void main(String[] args)
    throws Exception
  {
    System.out.println(isDate("2014-12-32"));
  }

  public static boolean isDateFormat(String datestr, String format)
  {
    SimpleDateFormat sdf = new SimpleDateFormat(format);
    try {
      java.util.Date d = sdf.parse(datestr);

      return datestr.equals(sdf.format(d)); } catch (Exception e) {
    }
    return false;
  }

  public static String isDateFormat(String datestr)
  {
    for (String format : dateFormats) {
      if (isDateFormat(datestr, format)) {
        return format;
      }
    }
    return null;
  }

  public static java.util.Date dateToDate(java.util.Date _date, String patten)
  {
    String dateStr = "";
    SimpleDateFormat formatter = new SimpleDateFormat(patten);
    try {
      if (_date != null) {
        dateStr = formatter.format(_date);
      }
      return formatter.parse(dateStr);
    } catch (ParseException localParseException) {
    }
    return null;
  }
}