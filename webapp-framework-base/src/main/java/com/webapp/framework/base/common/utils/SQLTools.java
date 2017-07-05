package com.webapp.framework.base.common.utils;

import java.util.ArrayList;
import java.util.List;

import com.webapp.framework.base.beans.BeanFactory;
import com.webapp.framework.base.service.CommonService;
import com.webapp.framework.core.beans.DBDialect;
import com.webapp.framework.core.utils.StringUtil;

public class SQLTools
{
  private static DBDialect dbTYPE;
  private static final String funName = "SQLTools.";
  private static final String isNull = "isNull";
  private static final String toDate = "toDate";
  private static final String toChar = "toChar";
  private static final String getDate = "getDate";
  private static final String dateAdd = "dateAdd";
  private static final String concat = "concat";
  private static List<String> nums = new ArrayList();

  private static void loadDbTYPE()
  {
    CommonService commonService = (CommonService)BeanFactory.getBean("commonService");
    if (null == commonService) {
      throw new RuntimeException("commonService服务未找到");
    }
    dbTYPE = commonService.getDbTYPE();
  }

  public static String isNull(String column, String defValue)
  {
    if (StringUtil.isNull(defValue))
      defValue = "''";
    if (null == dbTYPE)
      loadDbTYPE();
    if (DBDialect.MYSQL == dbTYPE) {
      return "ifnull(" + column + "," + defValue + ")";
    }
    return "nvl(" + column + "," + defValue + ")";
  }

  public static String toDate(String columnName, String dateFormat)
  {
    if (StringUtil.isNull(columnName))
      throw new RuntimeException("字段名必须填写");
    if (StringUtil.isNull(dateFormat))
      throw new RuntimeException("日期格式必须填写");
    if (null == dbTYPE)
      loadDbTYPE();
    String format = getDateFormat(dateFormat);

    if (nums.contains(columnName.substring(0, 1))) {
      columnName = "'" + columnName + "'";
    }

    if (DBDialect.MYSQL == dbTYPE) {
      return "str_to_date(" + columnName + ",'" + format + "')";
    }

    return "to_date(" + columnName + ",'" + format + "')";
  }

  public static String toChar(String columnName)
  {
    if (null == dbTYPE)
      loadDbTYPE();
    if (DBDialect.MYSQL == dbTYPE) {
      return "cast(" + columnName + " as char(250))";
    }

    return "to_char(" + columnName + ")";
  }

  public static String toChar(String columnName, String dateFormat)
  {
    if (StringUtil.isNull(columnName))
      throw new RuntimeException("字段名必须填写");
    if (StringUtil.isNull(dateFormat))
      throw new RuntimeException("日期格式必须填写");
    if (null == dbTYPE)
      loadDbTYPE();
    String format = getDateFormat(dateFormat);

    if (DBDialect.MYSQL == dbTYPE) {
      return "date_format(" + columnName + ",'" + format + "')";
    }

    return "to_char(" + columnName + ",'" + format + "')";
  }

  public static String getDate()
  {
    if (null == dbTYPE) {
      loadDbTYPE();
    }
    return DBDialect.MYSQL == dbTYPE ? "now()" : "sysdate";
  }

  public static String dateAdd(String column, String num)
  {
    if (StringUtil.isNull(column))
      throw new RuntimeException("字段必须填写");
    if ((column.startsWith("'")) || (nums.contains(column.substring(0, 1)))) {
      throw new RuntimeException("column必须是字段，不允许是具体的日期值");
    }
    num = cut(num);
    if (null == dbTYPE) {
      loadDbTYPE();
    }
    if (DBDialect.MYSQL == dbTYPE) {
      return "date_add(" + column + ",interval " + num + " day)";
    }
    return column + " - " + num;
  }

  public static String dateAdd(String column, Integer num)
  {
    if (null == num) {
      throw new RuntimeException("增加天数必须填写");
    }
    return dateAdd(column, num.toString());
  }

  private static String getDateFormat(String jDateFormat)
  {
    if ((jDateFormat.toLowerCase().equals("yyyyMM")) || 
      (jDateFormat
      .toLowerCase().equals("%Y%m")))
    {
      return DBDialect.MYSQL == dbTYPE ? "%Y%m" : "yyyyMM";
    }if ((jDateFormat.toLowerCase().equals("yyyymmdd")) || 
      (jDateFormat
      .toLowerCase().equals("%y%m%d")))
    {
      return DBDialect.MYSQL == dbTYPE ? "%Y%m%d" : "yyyyMMdd";
    }if ((jDateFormat.toLowerCase().equals("yyyy-mm-dd")) || 
      (jDateFormat
      .toLowerCase().equals("%y-%m-%d")))
    {
      return DBDialect.MYSQL == dbTYPE ? "%Y-%m-%d" : "yyyy-MM-dd";
    }if ((jDateFormat.toLowerCase().equals("yyyy.mm.dd")) || 
      (jDateFormat
      .toLowerCase().equals("%y.%m.%d")))
    {
      return DBDialect.MYSQL == dbTYPE ? "%Y.%m.%d" : "yyyy.MM.dd";
    }if ((jDateFormat.toLowerCase().equals("yyyy-mm-dd hh:mm:ss")) || 
      (jDateFormat
      .toLowerCase().equals("yyyy-mm-dd hh24:mi:ss")) || 
      (jDateFormat
      .toLowerCase().equals("%y-%m-%d %h:%i:%s")))
    {
      return DBDialect.MYSQL == dbTYPE ? "%Y-%m-%d %H:%i:%s" : "yyyy-MM-dd HH24:mi:ss";
    }if ((jDateFormat.toLowerCase().equals("yyyy.mm.dd hh:mm:ss")) || 
      (jDateFormat
      .toLowerCase().equals("%y.%m.%d %h:%i:%s")) || 
      (jDateFormat
      .toLowerCase().equals("yyyy.mm.dd hh24:mi:ss"))) {
      return DBDialect.MYSQL == dbTYPE ? "%Y.%m.%d %H:%i:%s" : "yyyy.MM.dd HH24:mi:ss";
    }
    throw new RuntimeException("不支持此日期格式：" + jDateFormat);
  }

  public static String convert(String sql)
  {
    if (StringUtil.isNull(sql)) {
      return sql;
    }
    if (null == dbTYPE) {
      loadDbTYPE();
    }
    sql = sql.replaceAll("\r", " ");
    sql = sql.replaceAll("\n", " ");
    sql = sql.replaceAll("\t", " ");

    if (dbTYPE == DBDialect.MYSQL)
      sql = sql.replaceAll("escape '\\\\'", "escape '\\\\\\\\'");
    else {
      sql = sql.replaceAll("escape '\\\\\\\\'", "escape '\\\\'");
    }

    if (sql.indexOf("SQLTools.") < 0)
      return sql;
    StringBuffer sb = new StringBuffer();

    while (sql.indexOf("SQLTools.") >= 0)
    {
      sb.append(sql.substring(0, sql.indexOf("SQLTools.")));
      StringBuffer tmpsql = new StringBuffer("SQLTools.");
      sql = sql.substring(sql.indexOf("SQLTools.") + "SQLTools.".length(), sql.length());

      tmpsql.append(getLeftSpace(sql));
      sql = StringUtil.leftTrim(sql);

      if (sql.startsWith("isNull"))
        sql = parse(sb, tmpsql, sql, "isNull");
      else if (sql.startsWith("toDate"))
        sql = parse(sb, tmpsql, sql, "toDate");
      else if (sql.startsWith("toChar"))
        sql = parse(sb, tmpsql, sql, "toChar");
      else if (sql.startsWith("getDate"))
        sql = parse(sb, tmpsql, sql, "getDate");
      else if (sql.startsWith("dateAdd"))
        sql = parse(sb, tmpsql, sql, "dateAdd");
      else if (sql.startsWith("concat")) {
        sql = parse(sb, tmpsql, sql, "concat");
      }
      else {
        sb.append(tmpsql);
      }

    }

    return sb.append(sql).toString();
  }

  private static String parse(StringBuffer sb, StringBuffer tmpsql, String sql, String funName) {
    tmpsql.append(funName);
    sql = sql.substring(sql.indexOf(funName) + funName.length(), sql.length());
    tmpsql.append(getLeftSpace(sql));
    sql = StringUtil.leftTrim(sql);

    if (!sql.startsWith("(")) {
      sb.append(tmpsql);
      return sql;
    }

    if (sql.length() < 0) {
      return sql;
    }

    String funsql = sql.substring(1, sql.indexOf(")"));
    funsql = call(funsql, funName);
    if (null == funsql) {
      sb.append(tmpsql);
      return sql;
    }
    sb.append(funsql);

    sql = sql.substring(sql.indexOf(")") + 1, sql.length());

    return sql;
  }

  private static String call(String funsql, String funName)
  {
    String[] ss = funsql.split(",");

    if (funName.equals("isNull")) {
      if (ss.length != 2)
        return null;
      return isNull(ss[0], ss[1]);
    }if (funName.equals("toDate")) {
      if (ss.length != 2)
        return null;
      String format = cut(ss[1]);
      return toDate(ss[0], format);
    }if (funName.equals("toChar")) {
      if (ss.length != 2)
      {
        return toChar(ss[0]);
      }
      String format = cut(ss[1]);
      return toChar(ss[0], format);
    }if (funName.equals("dateAdd")) {
      if (ss.length != 2)
        return null;
      return dateAdd(ss[0], ss[1]);
    }if (funName.equals("concat"))
      return concat(ss);
    if (funName.equals("getDate")) {
      return getDate();
    }

    return null;
  }

  private static String concat(String[] args)
  {
    StringBuffer sb = new StringBuffer();

    List list = new ArrayList();
    for (int i = 0; i < args.length; i++) {
      String value = args[i];

      if (!StringUtil.isNull(value))
      {
        if ((value.trim().startsWith("'")) && (!value.trim().endsWith("'"))) {
          String topvalue = value;
          while (true) {
            i++;
            value = args[i];
            topvalue = topvalue + "," + value;
            if (value.endsWith("'"))
              break;
          }
          value = topvalue;
        }
        list.add(value);
      }
    }
    if (dbTYPE == DBDialect.MYSQL) {
      sb.append("concat").append("(");
      for (int i = 0; i < list.size(); i++) {
        String value = (String)list.get(i);
        sb.append(value);
        if (i + 1 < list.size()) {
          sb.append(",");
        }
      }
      sb.append(")");
    } else {
      for (int i = 0; i < list.size(); i++) {
        String value = (String)list.get(i);
        sb.append(value);
        if (i + 1 < list.size()) {
          sb.append("||");
        }
      }
    }
    return sb.toString();
  }

  private static String cut(String value)
  {
    String format = value.trim();
    if (format.startsWith("'")) {
      format = format.substring(1);
    }
    if (format.endsWith("'")) {
      format = format.substring(0, format.length() - 1);
    }

    return format;
  }

  private static String getLeftSpace(String s)
  {
    if ((null == s) || (!s.startsWith(" ")))
      return "";
    if (s.trim().equals("")) {
      return s;
    }
    return s.substring(0, s.indexOf(s.trim().substring(0, 1)));
  }

  static
  {
    nums.add("0");
    nums.add("1");
    nums.add("2");
    nums.add("3");
    nums.add("4");
    nums.add("5");
    nums.add("6");
    nums.add("7");
    nums.add("8");
    nums.add("9");
  }
}