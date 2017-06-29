package com.webapp.framework.utils.converter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.converters.BigDecimalConverter;
import org.apache.commons.beanutils.converters.BigIntegerConverter;
import org.apache.commons.beanutils.converters.BooleanConverter;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.beanutils.converters.DateTimeConverter;
import org.apache.commons.beanutils.converters.DoubleConverter;
import org.apache.commons.beanutils.converters.FloatConverter;
import org.apache.commons.beanutils.converters.IntegerConverter;
import org.apache.commons.beanutils.converters.LongConverter;
import org.apache.commons.beanutils.converters.ShortConverter;
import org.apache.commons.beanutils.converters.SqlDateConverter;
import org.apache.commons.beanutils.converters.SqlTimeConverter;
import org.apache.commons.beanutils.converters.SqlTimestampConverter;

public class ConvertRegisterHelper
{
//  public static void registerConverters()
//  {
//    Map map = getConverMap();
//    for (Converter o : map.keySet())
//      ConvertUtils.register(o, (Class)map.get(o));
//  }
	
	
 public static void registerConverters() {
		ConvertUtils.register(new StringConverter(), String.class);
		//date 
		ConvertUtils.register(new DateConverter(null),java.util.Date.class);
        ConvertUtils.register(new SqlDateConverter(null),java.sql.Date.class);
		ConvertUtils.register(new SqlTimeConverter(null),Time.class);
		ConvertUtils.register(new SqlTimestampConverter(null),Timestamp.class);
		//number
		ConvertUtils.register(new BooleanConverter(null), Boolean.class);
		ConvertUtils.register(new ShortConverter(null), Short.class);
		ConvertUtils.register(new IntegerConverter(null), Integer.class);
		ConvertUtils.register(new LongConverter(null), Long.class);
		ConvertUtils.register(new FloatConverter(null), Float.class);
		ConvertUtils.register(new DoubleConverter(null), Double.class);
		ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class); 
		ConvertUtils.register(new BigIntegerConverter(null), BigInteger.class);	
  }

  public static void registerConverters(ConvertUtilsBean convertUtils, String[] datePatterns)
  {
    convertUtils.register(new StringConverter(), String.class);

    convertUtils.register(setPatterns(new DateConverter(null), datePatterns), java.util.Date.class);
    convertUtils.register(setPatterns(new SqlDateConverter(null), datePatterns), java.sql.Date.class);
    convertUtils.register(setPatterns(new SqlTimeConverter(null), datePatterns), Time.class);
    convertUtils.register(setPatterns(new SqlTimestampConverter(null), datePatterns), Timestamp.class);

    convertUtils.register(new BooleanConverter(null), Boolean.class);
    convertUtils.register(new ShortConverter(null), Short.class);
    convertUtils.register(new IntegerConverter(null), Integer.class);
    convertUtils.register(new LongConverter(null), Long.class);
    convertUtils.register(new FloatConverter(null), Float.class);
    convertUtils.register(new DoubleConverter(null), Double.class);
    convertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
    convertUtils.register(new BigIntegerConverter(null), BigInteger.class);
  }

  public static <T extends DateTimeConverter> T setPatterns(T converter, String[] patterns) {
    converter.setPatterns(patterns);
    return converter;
  }

  public static Map<Converter, Class> getConverMap()
  {
    Map map = new LinkedHashMap();
    map.put(new StringConverter(), String.class);

    map.put(new DateConvert(), java.util.Date.class);
    map.put(new SqlDateConverter(null), java.sql.Date.class);
    map.put(new SqlTimeConverter(null), Time.class);
    map.put(new SqlTimestampConverter(null), Timestamp.class);

    map.put(new BooleanConverter(null), Boolean.class);
    map.put(new ShortConverter(null), Short.class);
    map.put(new IntegerConverter(null), Integer.class);
    map.put(new LongConverter(null), Long.class);
    map.put(new FloatConverter(null), Float.class);
    map.put(new DoubleConverter(null), Double.class);
    map.put(new BigDecimalConverter(null), BigDecimal.class);
    map.put(new BigIntegerConverter(null), BigInteger.class);
    return map;
  }
}