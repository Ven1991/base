package com.webapp.framework.utils.excel;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class AssertUtil
{
  public static boolean isVal(String string)
  {
    if ((string == null) || ("null".equals(string)) || (string.isEmpty())) {
      return false;
    }
    return true;
  }

  public static boolean isInval(String string)
  {
    return !isVal(string);
  }

  public static boolean isVal(StringBuffer stringBuffer)
  {
    if ((stringBuffer == null) || (stringBuffer.length() == 0)) {
      return false;
    }
    return true;
  }

  public static boolean isInval(StringBuffer stringBuffer)
  {
    return !isVal(stringBuffer);
  }

  public static boolean isVal(Number number)
  {
    if (number == null) {
      return false;
    }
    return true;
  }

  public static boolean isInval(Number number)
  {
    return !isVal(number);
  }

  public static boolean isVal(Object object)
  {
    if (object == null) {
      return false;
    }
    return true;
  }

  public static boolean isInval(Object object)
  {
    return !isVal(object);
  }

  public static boolean isVal(Object[] array)
  {
    if ((array == null) || (array.length == 0)) {
      return false;
    }
    return true;
  }

  public static boolean isInval(Object[] array)
  {
    return !isVal(array);
  }

  public static boolean isVal(Map map)
  {
    if ((map != null) && (!map.isEmpty())) {
      return true;
    }
    return false;
  }

  public static boolean isInval(Map map)
  {
    return !isVal(map);
  }

  public static boolean isVal(Collection collection)
  {
    if ((collection != null) && (!collection.isEmpty())) {
      return true;
    }
    return false;
  }

  public static boolean isInval(Collection collection)
  {
    return !isVal(collection);
  }

  public static boolean isInteger(String string)
  {
    boolean isInteger = false;
    if (isVal(string)) {
      isInteger = string.matches("\\d*");
    }
    return isInteger;
  }

  public static boolean isInteger(Number number)
  {
    boolean isInteger = false;
    if (isVal(number)) {
      isInteger = number.toString().matches("\\d*");
    }
    return isInteger;
  }

  public static boolean isPositiveNumber(BigDecimal number)
  {
    if ((isVal(number)) && (number.compareTo(BigDecimal.ZERO) > 0)) {
      return true;
    }
    return false;
  }

  public static boolean isNegativeNumber(BigDecimal number)
  {
    if ((isVal(number)) && (number.compareTo(BigDecimal.ZERO) < 0)) {
      return true;
    }
    return false;
  }

  public static boolean isZero(BigDecimal number)
  {
    boolean isZero = false;
    if ((isVal(number)) && (BigDecimal.ZERO.compareTo(number) == 0)) {
      isZero = true;
    }
    return isZero;
  }

  public static int intValue(Object obj)
  {
    return isVal(obj) ? Integer.parseInt(obj.toString()) : 0;
  }

  public static String stringValue(Object obj)
  {
    return isVal(obj) ? obj.toString() : "";
  }

  public static String removeZero(BigDecimal obj)
  {
    return (isVal(obj)) && (obj.compareTo(BigDecimal.ZERO) == 0) ? "" : obj.toString();
  }

  public static String removeZero(int num)
  {
    return num == 0 ? "" : String.valueOf(num);
  }

  public static BigDecimal bigDecimalValue(Object obj)
  {
    return isVal(obj) ? new BigDecimal(obj.toString()) : BigDecimal.ZERO;
  }

  public static Object objectValue(Object obj, Object realObj)
  {
    return isVal(obj) ? obj : realObj;
  }

  public static boolean isNumeric(String str)
  {
    Pattern pattern = Pattern.compile("^[+|-]*[0-9|,|.]*");
    if ((str == null) || ("".equals(str))) {
      return false;
    }
    if ("|".equals(str)) {
      return false;
    }
    Matcher isNum = pattern.matcher(str);
    if (!isNum.matches()) {
      return false;
    }
    return true;
  }

  public static BigDecimal getDecimal(String amt)
  {
    if (("".equals(amt)) || (null == amt)) {
      return BigDecimal.ZERO;
    }
    return new BigDecimal(amt.replaceAll(",", "").trim());
  }
}