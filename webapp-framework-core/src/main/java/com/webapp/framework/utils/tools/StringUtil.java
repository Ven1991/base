package com.webapp.framework.utils.tools;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public class StringUtil extends StringUtils
{
  private static char[] chartable = { '啊', 33453, '擦', '搭', 34558, '发', '噶', '哈', '哈', '击', '喀', '垃', '妈', '拿', '哦', '啪', '期', '然', '撒', '塌', '塌', '塌', '挖', '昔', '压', '匝', '座' };

  private static char[] alphatable = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

  private static int[] table = new int[27];

  public static String getParm(String sBuf, String sName)
  {
    return getParm(sBuf, sName, "&");
  }

  public static String getParm(String sBuf, String sName, String sTag) {
    String tagName = new StringBuilder().append(sName).append("=").toString();
    if (sBuf.indexOf(tagName) < 0)
      return null;
    String[] strlist = sBuf.split(sTag);
    for (int i = 0; i < strlist.length; i++) {
      if (strlist[i].indexOf(tagName) >= 0) {
        return strlist[i].substring(tagName.length());
      }
    }
    return null;
  }

  public static long getVersionNumber(String version)
  {
    String[] vs1 = version.split("\\.");
    if (vs1.length == 1) {
      String s1 = vs1[0];
      int i1 = Integer.parseInt(s1);
      return i1 * 10000000;
    }

    if (vs1.length == 2) {
      String s1 = vs1[0];
      int i1 = Integer.parseInt(s1);
      String s2 = vs1[1];
      int i2 = Integer.parseInt(s2);

      return i1 * 10000000 + i2 * 100000;
    }

    if (vs1.length == 3) {
      String s1 = vs1[0];
      int i1 = Integer.parseInt(s1);
      String s2 = vs1[1];
      int i2 = Integer.parseInt(s2);
      String s3 = vs1[2];
      int i3 = Integer.parseInt(s3);
      return i1 * 10000000 + i2 * 100000 + i3 * 10;
    }

    String s1 = vs1[0];
    int i1 = Integer.parseInt(s1);
    String s2 = vs1[1];
    int i2 = Integer.parseInt(s2);
    String s3 = vs1[2];
    int i3 = Integer.parseInt(s3);
    String s4 = vs1[3];
    int i4 = Integer.parseInt(s4);
    return i1 * 10000000 + i2 * 100000 + i3 * 10 + i4;
  }

  public static boolean isNull(Object str) {
    if (null == str)
      return true;
    if ((str instanceof String)) {
      String value = (String)str;
      return value.trim().equals("");
    }
    return false;
  }

  public static boolean isNotNull(Object str)
  {
    return !isNull(str);
  }

  public static String getMd5(String s)
  {
    if (null == s) {
      return null;
    }
    return getMd5(s.getBytes());
  }

  public static String getWeekOfDate(Date dt)
  {
    String[] weekDays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
    Calendar cal = Calendar.getInstance();
    cal.setTime(dt);
    int w = cal.get(7) - 1;
    if (w < 0)
      w = 0;
    return weekDays[w];
  }

  public static String rightTrim(String s)
  {
    if ((s == null) || (s.trim().length() == 0))
      return null;
    if (s.trim().length() == s.length())
      return s;
    if (!s.startsWith(" ")) {
      return s.trim();
    }
    return s.substring(0, s.indexOf(s.trim().substring(0, 1)) + s.trim().length());
  }

  public static String leftTrim(String s)
  {
    if ((s == null) || (s.trim().length() == 0))
      return null;
    if (s.trim().length() == s.length())
      return s;
    if (!s.startsWith(" ")) {
      return s;
    }
    return s.substring(s.indexOf(s.trim().substring(0, 1)));
  }

  public static String getMd5(byte[] bytes)
  {
    char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
    try
    {
      MessageDigest mdTemp = MessageDigest.getInstance("MD5");
      mdTemp.update(bytes);
      byte[] md = mdTemp.digest();
      int j = md.length;
      char[] str = new char[j * 2];
      int k = 0;
      for (int i = 0; i < j; i++) {
        byte byte0 = md[i];
        str[(k++)] = hexDigits[(byte0 >>> 4 & 0xF)];
        str[(k++)] = hexDigits[(byte0 & 0xF)];
      }
      return new String(str); } catch (Exception e) {
    }
    return null;
  }

  public static String GUID()
  {
    return Identities.uuid2();
  }

  public static String getRand(int n)
  {
    Random rnd = new Random();
    String pass = "0";
    int x = rnd.nextInt(10);

    while (x == 0) {
      x = rnd.nextInt(10);
    }
    pass = String.valueOf(x);
    for (int i = 1; i < n; i++) {
      pass = new StringBuilder().append(pass).append(String.valueOf(rnd.nextInt(10))).toString();
    }
    return pass;
  }

  public static String fillLeft(String source, char fillChar, long len)
  {
    StringBuffer ret = new StringBuffer();
    if (null == source)
      ret.append("");
    if (source.length() > len) {
      ret.append(source);
      return ret.toString();
    }
    long slen = source.length();
    while (ret.toString().length() + slen < len) {
      ret.append(fillChar);
    }
    ret.append(source);
    return ret.toString();
  }

  public static String fillStr(int len, String repStr)
  {
    if (len <= 0) {
      return repStr;
    }
    StringBuilder ret = new StringBuilder();
    for (int i = 0; i < len; i++) {
      ret.append(repStr);
    }
    return ret.toString();
  }

  public static String fillRight(String source, char fillChar, int len)
  {
    StringBuffer ret = new StringBuffer();
    if (null == source)
      ret.append("");
    if (source.length() > len) {
      ret.append(source);
    } else {
      ret.append(source);
      while (ret.toString().length() < len) {
        ret.append(fillChar);
      }
    }
    return ret.toString();
  }

  public static String escape(String src)
  {
    StringBuffer tmp = new StringBuffer();
    tmp.ensureCapacity(src.length() * 6);
    for (int i = 0; i < src.length(); i++) {
      char j = src.charAt(i);
      if ((Character.isDigit(j)) || (Character.isLowerCase(j)) || (Character.isUpperCase(j))) {
        tmp.append(j);
      } else if (j < 'Ā') {
        tmp.append("%");
        if (j < '\020')
          tmp.append("0");
        tmp.append(Integer.toString(j, 16));
      } else {
        tmp.append("%u");
        tmp.append(Integer.toString(j, 16));
      }
    }
    return tmp.toString();
  }

  public static String unescape(String src)
  {
    StringBuffer tmp = new StringBuffer();
    tmp.ensureCapacity(src.length());
    int lastPos = 0; int pos = 0;

    while (lastPos < src.length()) {
      pos = src.indexOf("%", lastPos);
      if (pos == lastPos) {
        if (src.charAt(pos + 1) == 'u') {
          char ch = (char)Integer.parseInt(src.substring(pos + 2, pos + 6), 16);
          tmp.append(ch);
          lastPos = pos + 6;
        } else {
          char ch = (char)Integer.parseInt(src.substring(pos + 1, pos + 3), 16);
          tmp.append(ch);
          lastPos = pos + 3;
        }
      }
      else if (pos == -1) {
        tmp.append(src.substring(lastPos));
        lastPos = src.length();
      } else {
        tmp.append(src.substring(lastPos, pos));
        lastPos = pos;
      }
    }

    return tmp.toString();
  }

  public static boolean isOutLen(String str, int num)
  {
    if (isNull(str)) {
      return num < 0;
    }
    return str.getBytes().length > num;
  }

  public static String replaceStrToHTML(String str)
  {
    if (isNull(str)) {
      return str;
    }
    return str.replaceAll("\r\n", "<br>").replaceAll("\r", "<br>").replaceAll("\n", "<br>")
      .replaceAll(" ", "　");
  }

  public static String replaceHtmlToStr(String html)
  {
    if (isNull(html)) {
      return null;
    }
    return html.replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&nbsp;", " ")
      .replaceAll("&quot;", "\"")
      .replaceAll("<br>", "\r\n").replaceAll("&amp;", "&");
  }

  public static String replaceTagName(String str, String tagName, String value)
  {
    if (isNull(value)) {
      return str;
    }
    Pattern p = Pattern.compile("\\{(.*?)\\}");
    Matcher m = p.matcher(str);
    while (m.find()) {
      String name = m.group(1);
      if (null != name)
      {
        if (name.trim().equals(tagName)) {
          if (value.indexOf("$") >= 0) {
            value = value.replaceAll("\\$", "\\\\\\$");
          }
          str = str.replaceFirst(new StringBuilder().append("\\{").append(name).append("\\}").toString(), value);
        }
      }
    }
    return str;
  }

  public static String replace(String str, Object[] values)
  {
    if ((null == values) && (values.length <= 0)) {
      return str;
    }
    Pattern p = Pattern.compile("\\{(.*?)\\}");
    Matcher m = p.matcher(str);
    int i = 0;
    while ((m.find()) && 
      (i < values.length))
    {
      String name = m.group(1);
      String value = null == values[i] ? "null" : values[i].toString();
      if (value.indexOf("$") >= 0) {
        value = value.replaceAll("\\$", "\\\\\\$");
      }
      i++;

      str = str.replaceFirst(new StringBuilder().append("\\{").append(name).append("\\}").toString(), value);
    }
    return str;
  }

  public static List<String> findText(String start, String end, String content, int i)
  {
    StringBuilder builder = new StringBuilder();
    builder.append(start);
    builder.append("([\\s\\S]*?)");
    builder.append(end);

    return find(builder.toString(), i, content);
  }

  public static List<String> find(String regex, int i, String content)
  {
    Pattern p = Pattern.compile(regex);
    Matcher m = p.matcher(content);
    List list = new ArrayList();
    while (m.find()) {
      try {
        list.add(m.group(i));
      }
      catch (IndexOutOfBoundsException e) {
        list.add(m.group());
      }
    }
    return list;
  }

  public static String replaceBlank(String str)
  {
    if (isNull(str)) {
      return str;
    }
    Matcher m = Pattern.compile("\\s*|\t|\r|\n").matcher(str);
    return m.replaceAll("");
  }

  public static String format(double amt)
  {
    Locale locale = new Locale("zh", "CN");
    return format(amt, locale);
  }

  public static String format(double amt, Locale locale)
  {
    NumberFormat currFmt = NumberFormat.getCurrencyInstance(locale);
    return currFmt.format(amt);
  }

  public static String getArge(Date birthday)
  {
    return getArge(birthday, new Date());
  }

  public static String getArge(Date birthday, Date startDate) {
    if ((null == birthday) || (birthday.getTime() > System.currentTimeMillis())) {
      return null;
    }
    Calendar bCalendar = Calendar.getInstance();
    Calendar nCalendar = Calendar.getInstance();
    bCalendar.setTime(birthday);
    nCalendar.setTime(startDate);

    int bYear = bCalendar.get(1);
    int nYear = nCalendar.get(1);

    if (nYear - bYear > 10) {
      return new StringBuilder().append(nYear - bYear).append("").toString();
    }
    if (nYear - bYear > 0) {
      int arge = nYear - bYear;
      int bMonth = bCalendar.get(2) + 1;
      int nMonth = nCalendar.get(2) + 1;

      String strArge = "";
      if (bMonth > nMonth) {
        arge--;
        if (arge == 0) {
          return getOneYearArge(birthday, startDate);
        }
        strArge = new StringBuilder().append(arge).append("Y").append(12 - bMonth + nMonth).append("M").toString();
      } else {
        strArge = new StringBuilder().append(arge).append("Y").append(nMonth - bMonth).append("M").toString();
      }

      return strArge;
    }

    return getOneYearArge(birthday, startDate);
  }

  private static String getOneYearArge(Date birthday, Date startDate)
  {
    Calendar bCalendar = Calendar.getInstance();
    Calendar nCalendar = Calendar.getInstance();
    bCalendar.setTime(birthday);
    nCalendar.setTime(startDate);

    int bMonth = bCalendar.get(2) + 1;
    int nMonth = nCalendar.get(2) + 1;
    int arge = 0;
    int date = 0;
    if (bMonth > nMonth)
      arge = 12 - (bMonth - nMonth);
    else {
      arge = nMonth - bMonth;
    }

    bCalendar.add(2, arge);

    int bDAY = bCalendar.get(5);
    int nDAY = nCalendar.get(5);
    if (bDAY > nDAY) {
      arge--;
      bCalendar.add(2, -1);

      Date d1 = bCalendar.getTime();
      Date d2 = nCalendar.getTime();

      long l = d2.getTime() - d1.getTime();
      date = Long.valueOf(l / 86400000L).intValue();
    }
    else {
      date = nDAY - bDAY;
    }

    if (arge == 0) {
      if (date == 0) {
        date++;
      }
      return new StringBuilder().append(date).append("D").toString();
    }

    return new StringBuilder().append(arge).append("M").append(date).append("D").toString();
  }

  public static double getMonthArge(Date birthday)
  {
    return getMonthArge(birthday, new Date());
  }

  public static double getMonthArge(Date birthday, Date startDate) {
    Calendar bCalendar = Calendar.getInstance();
    Calendar nCalendar = Calendar.getInstance();
    bCalendar.setTime(birthday);
    nCalendar.setTime(startDate);
    if (bCalendar.getTimeInMillis() > nCalendar.getTimeInMillis()) {
      return -1.0D;
    }
    int bYear = bCalendar.get(1);
    int nYear = nCalendar.get(1);

    if (nYear - bYear > 10) {
      return (nYear - bYear) * 12;
    }
    Integer arge = Integer.valueOf(nYear - bYear);
    int bMonth = bCalendar.get(2) + 1;
    int nMonth = nCalendar.get(2) + 1;

    int month = 0;
    if (bMonth > nMonth) {
      Integer localInteger1 = arge; Integer localInteger2 = arge = Integer.valueOf(arge.intValue() - 1);
      month = 12 - (bMonth - nMonth);
    } else {
      month = nMonth - bMonth;
    }

    Integer bDAY = Integer.valueOf(bCalendar.get(5));
    Integer nDAY = Integer.valueOf(nCalendar.get(5));

    Integer date = Integer.valueOf(0);
    if (bDAY.intValue() > nDAY.intValue()) {
      month--;
      bCalendar.set(1, nYear);
      bCalendar.set(2, nMonth - 2);

      Date d1 = bCalendar.getTime();
      Date d2 = nCalendar.getTime();

      long l = d2.getTime() - d1.getTime();
      date = Integer.valueOf(Long.valueOf(l / 86400000L).intValue());
    } else {
      date = Integer.valueOf(nDAY.intValue() - bDAY.intValue());
    }

    Integer m = Integer.valueOf(arge.intValue() * 12 + month);
    Double d = DoubleUtil.precise(Double.valueOf(date.doubleValue()), Double.valueOf(30.0D), "/");
    return DoubleUtil.precise(Double.valueOf(m.doubleValue()), d, "+").doubleValue();
  }

  public static String getPYIndexStr(String strChinese, boolean bUpCase)
  {
    try
    {
      StringBuffer buffer = new StringBuffer();

      byte[] b = strChinese.getBytes("GBK");

      for (int i = 0; i < b.length; i++)
      {
        if ((b[i] & 0xFF) > 128)
        {
          int char1 = b[(i++)] & 0xFF;

          char1 <<= 8;

          int chart = char1 + (b[i] & 0xFF);

          buffer.append(getPYIndexChar((char)chart, bUpCase));
        }
        else
        {
          char c = (char)b[i];

          if (!Character.isJavaIdentifierPart(c))
          {
            c = 'A';
          }
          buffer.append(c);
        }
      }

      return buffer.toString();
    }
    catch (Exception e)
    {
      System.out
        .println(new StringBuilder()
        .append("取中文拼音有错")
        .append(e
        .getMessage()).toString());
    }

    return null;
  }

  private static char getPYIndexChar(char strChinese, boolean bUpCase)
  {
    int charGBK = strChinese;
    char result;
//    char result;
    if ((charGBK >= 45217) && (charGBK <= 45252))
    {
      result = 'A';
    }
    else
    {
//      char result;
      if ((charGBK >= 45253) && (charGBK <= 45760))
      {
        result = 'B';
      }
      else
      {
//        char result;
        if ((charGBK >= 45761) && (charGBK <= 46317))
        {
          result = 'C';
        }
        else
        {
//          char result;
          if ((charGBK >= 46318) && (charGBK <= 46825))
          {
            result = 'D';
          }
          else
          {
//            char result;
            if ((charGBK >= 46826) && (charGBK <= 47009))
            {
              result = 'E';
            }
            else
            {
//              char result;
              if ((charGBK >= 47010) && (charGBK <= 47296))
              {
                result = 'F';
              }
              else
              {
//                char result;
                if ((charGBK >= 47297) && (charGBK <= 47613))
                {
                  result = 'G';
                }
                else
                {
//                  char result;
                  if ((charGBK >= 47614) && (charGBK <= 48118))
                  {
                    result = 'H';
                  }
                  else
                  {
//                    char result;
                    if ((charGBK >= 48119) && (charGBK <= 49061))
                    {
                      result = 'J';
                    }
                    else
                    {
//                      char result;
                      if ((charGBK >= 49062) && (charGBK <= 49323))
                      {
                        result = 'K';
                      }
                      else
                      {
//                        char result;
                        if ((charGBK >= 49324) && (charGBK <= 49895))
                        {
                          result = 'L';
                        }
                        else
                        {
//                          char result;
                          if ((charGBK >= 49896) && (charGBK <= 50370))
                          {
                            result = 'M';
                          }
                          else
                          {
//                            char result;
                            if ((charGBK >= 50371) && (charGBK <= 50613))
                            {
                              result = 'N';
                            }
                            else
                            {
//                              char result;
                              if ((charGBK >= 50614) && (charGBK <= 50621))
                              {
                                result = 'O';
                              }
                              else
                              {
//                                char result;
                                if ((charGBK >= 50622) && (charGBK <= 50905))
                                {
                                  result = 'P';
                                }
                                else
                                {
//                                  char result;
                                  if ((charGBK >= 50906) && (charGBK <= 51386))
                                  {
                                    result = 'Q';
                                  }
                                  else
                                  {
//                                    char result;
                                    if ((charGBK >= 51387) && (charGBK <= 51445))
                                    {
                                      result = 'R';
                                    }
                                    else
                                    {
//                                      char result;
                                      if ((charGBK >= 51446) && (charGBK <= 52217))
                                      {
                                        result = 'S';
                                      }
                                      else
                                      {
//                                        char result;
                                        if ((charGBK >= 52218) && (charGBK <= 52697))
                                        {
                                          result = 'T';
                                        }
                                        else
                                        {
//                                          char result;
                                          if ((charGBK >= 52698) && (charGBK <= 52979))
                                          {
                                            result = 'W';
                                          }
                                          else
                                          {
//                                            char result;
                                            if ((charGBK >= 52980) && (charGBK <= 53688))
                                            {
                                              result = 'X';
                                            }
                                            else
                                            {
//                                              char result;
                                              if ((charGBK >= 53689) && (charGBK <= 54480))
                                              {
                                                result = 'Y';
                                              }
                                              else
                                              {
//                                                char result;
                                                if ((charGBK >= 54481) && (charGBK <= 55289))
                                                {
                                                  result = 'Z';
                                                }
                                                else
                                                {
                                                  result = (char)(65 + new Random().nextInt(25));
                                                }
                                              }
                                            }
                                          }
                                        }
                                      }
                                    }
                                  }
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
    if (!bUpCase)
    {
      result = Character.toLowerCase(result);
    }
    return result;
  }

  public static String cut(String value, int len)
  {
    if (isNull(value))
      return value;
    if (len <= 0)
      return value;
    try
    {
      byte[] buf = value.getBytes("UTF-8");
      if (buf.length <= len) {
        return value;
      }
      return new String(value.getBytes("UTF-8"), 0, len - 1, "UTF-8"); } catch (UnsupportedEncodingException e) {
    }
    return value;
  }

  public static String decode(String str)
  {
    return decode(str, "UTF-8");
  }

  public static String decode(String str, String encode)
  {
    try
    {
      str = URLDecoder.decode(str, encode);
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return str;
  }

  public static String isoToUTF(String src)
  {
    String strRet = null;
    try {
      strRet = new String(src.getBytes("ISO_8859_1"), "UTF-8");
    }
    catch (Exception localException) {
    }
    return strRet;
  }

  public static String utfToGbk(String src)
  {
    String strRet = null;
    try {
      strRet = new String(src.getBytes("ISO_8859_1"), "UTF-8");
    }
    catch (Exception localException) {
    }
    return strRet;
  }

  public static String isBlankToMsg(String str, String msg)
  {
    String returnstr = "";
    if (StringUtils.isBlank(str)) {
      returnstr = new StringBuilder().append(msg).append(",").toString();
    }
    return returnstr;
  }

  public static String fillLeft(String source, char fillChar, int len)
  {
    StringBuffer ret = new StringBuffer();
    if (null == source)
      ret.append("");
    if (source.length() > len) {
      ret.append(source);
    } else {
      int slen = source.length();
      while (ret.toString().length() + slen < len) {
        ret.append(fillChar);
      }
      ret.append(source);
    }
    return ret.toString();
  }

  public static String filRight(String source, char fillChar, int len)
  {
    StringBuffer ret = new StringBuffer();
    if (null == source)
      ret.append("");
    if (source.length() > len) {
      ret.append(source);
    } else {
      ret.append(source);
      while (ret.toString().length() < len) {
        ret.append(fillChar);
      }
    }
    return ret.toString();
  }

  public static String filterStr(String str) {
    if ((null == str) || ("".equals(str))) {
      return str;
    }
    str = str.replaceAll("'", "''");
    return str;
  }

  public static int indexOfAll(String tagetStr, String str)
  {
    int i = 0;
    if (null != tagetStr) {
      i = tagetStr.length() - tagetStr.replace(str, "").length();
    }
    return i;
  }

  public static Boolean isBlankOne(Object[] args)
  {
    Boolean flag = Boolean.valueOf(false);
    for (int i = 0; i < args.length; i++) {
      if ((args[i] instanceof String)) {
        if (isBlank((String)args[i])) {
          flag = Boolean.valueOf(true);
        }
      }
      else if (null == args[i]) {
        flag = Boolean.valueOf(true);
      }
    }

    return flag;
  }

  public static boolean isNumber(String obj)
  {
    if (null == obj) {
      obj = "";
    }
    return obj.matches("-?\\d+\\.?\\d*");
  }

  private static boolean match(int i, int gb)
  {
    if (gb < table[i])
      return false;
    int j = i + 1;

    while ((j < 26) && (table[j] == table[i]))
      j++;
    if (j == 26) {
      return gb <= table[j];
    }
    return gb < table[j];
  }

  private static int gbValue(char ch)
  {
    String str = new String();
    str = new StringBuilder().append(str).append(ch).toString();
    try {
      byte[] bytes = str.getBytes("GB2312");
      if (bytes.length < 2) {
        return 0;
      }
      return (bytes[0] << 8 & 0xFF00) + (bytes[1] & 0xFF); } catch (Exception e) {
    }
    return 0;
  }

  private static char Char2Alpha(char ch)
  {
    if ((ch >= 'a') && (ch <= 'z'))
      return (char)(ch - 'a' + 65);
    if ((ch >= 'A') && (ch <= 'Z'))
      return ch;
    int gb = gbValue(ch);
    if (gb < table[0]) {
      return '0';
    }
    int i = 0;
    for (i = 0; (i < 26) && (!match(i, gb)); i++);
    if (i >= 26) {
      return '0';
    }
    return alphatable[i];
  }

  public static String String2Alpha(String SourceStr)
  {
    String Result = "";
    int StrLength = SourceStr.length();
    try
    {
      for (int i = 0; i < StrLength; i++)
        Result = new StringBuilder().append(Result).append(Char2Alpha(SourceStr.charAt(i))).toString();
    }
    catch (Exception e) {
      Result = "";
    }
    return Result;
  }

  static
  {
    for (int i = 0; i < 27; i++)
      table[i] = gbValue(chartable[i]);
  }
}