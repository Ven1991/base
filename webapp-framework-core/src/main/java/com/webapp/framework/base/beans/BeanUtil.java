package com.webapp.framework.base.beans;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import com.webapp.framework.utils.converter.ConvertRegisterHelper;

public class BeanUtil extends BeanUtils
{
  protected static Log log = LogFactory.getLog(BeanUtil.class);
  static Hashtable<Class<?>, Method[]> objectMethods = new Hashtable();

  private static void handleReflectionException(Exception e)
  {
    ReflectionUtils.handleReflectionException(e);
  }

  public static Object cloneBean(Object bean)
  {
    try
    {
      return BeanUtils.cloneBean(bean);
    } catch (Exception e) {
      handleReflectionException(e);
    }return null;
  }

  public static <T> T copyProperties(Class<T> destClass, Object orig)
  {
    try
    {
      Object target = destClass.newInstance();
      copyProperties(target, orig);
      return (T) target;
    } catch (Exception e) {
      handleReflectionException(e);
    }return null;
  }

  public static void copyProperties(Object dest, Object orig)
  {
    try
    {
      BeanUtils.copyProperties(dest, orig);
    } catch (Exception e) {
      handleReflectionException(e);
    }
  }

  public static void copyProperty(Object bean, String name, Object value)
  {
    try
    {
      BeanUtils.copyProperty(bean, name, value);
    } catch (Exception e) {
      handleReflectionException(e);
    }
  }

  public static Map<String, Object> bean2Map(Object bean)
  {
    try
    {
      return BeanUtils.describe(bean);
    } catch (Exception e) {
      handleReflectionException(e);
    }return null;
  }

  public static <T> T map2Bean(Map<String, Object> map, Class<T> clazz)
    throws InstantiationException, IllegalAccessException
  {
    Object bean = clazz.newInstance();

    for (String column : map.keySet()) {
      Object value = map.get(column);
      setValue(bean, changeColumnName(column), value);
    }

    return (T) bean;
  }

  private static String changeColumnName(String column)
  {
    String[] columns = column.split("_");
    String cc = "";
    for (int i = 0; i < columns.length; i++) {
      String c = columns[i];
      if (!c.equals(""))
      {
        c = c.toLowerCase();
        if (!cc.equals("")) {
          char[] chars = c.toCharArray();
          chars[0] = Character.toUpperCase(chars[0]);
          c = new String(chars);
          cc = cc + c;
        } else {
          cc = c;
        }
      }
    }
    return cc;
  }

  public static String getSimpleProperty(Object bean, String name) {
    try {
      return BeanUtils.getSimpleProperty(bean, name);
    } catch (Exception e) {
      handleReflectionException(e);
    }return null;
  }

  public static void populate(Object bean, Map properties)
  {
    try {
      BeanUtils.populate(bean, properties);
    } catch (Exception e) {
      handleReflectionException(e);
    }
  }

  public static void setValue(Object bean, String name, Object value) {
    try {
      BeanUtils.setProperty(bean, name, value);
    } catch (Exception e) {
      setValue(bean, name, value, true);
    }
  }

  public static void cpValue(Object src, Object dest, String name)
  {
    setValue(dest, name, getValue(src, name));
  }

  public static void setValue(Object src, Object dest, String srcName, String destName)
  {
    setValue(dest, destName, getValue(src, srcName));
  }

  public static boolean setValue(Object o, String name, Object value, boolean invokeSetProperty)
  {
    if (Map.class.isAssignableFrom(o.getClass())) {
      ((Map)o).put(name, value);
      return true;
    }
    if (log.isDebugEnabled()) {
      log.debug("IntrospectionUtils: setProperty(" + o
        .getClass() + " " + name + "=" + value + ")");
    }
    String setter = "set" + capitalize(name);
    try
    {
      Method[] methods = findMethods(o.getClass());
      Method setPropertyMethodVoid = null;
      Method setPropertyMethodBool = null;

      for (int i = 0; i < methods.length; i++) {
        Class[] paramT = methods[i].getParameterTypes();
        if (paramT.length != 0)
        {
          if ((setter.equals(methods[i].getName())) && (paramT.length == 1) && 
            (value
            .getClass().equals(paramT[0]))) {
            methods[i].invoke(o, new Object[] { value });
            return true;
          }
        }
      }
      if ((invokeSetProperty) && ((setPropertyMethodBool != null) || (setPropertyMethodVoid != null)))
      {
        Object[] params = new Object[2];
        params[0] = name;
        params[1] = value;
        if (setPropertyMethodBool != null) {
          try {
            return ((Boolean)setPropertyMethodBool.invoke(o, params)).booleanValue();
          }
          catch (IllegalArgumentException biae)
          {
            if (setPropertyMethodVoid != null) {
              setPropertyMethodVoid.invoke(o, params);
              return true;
            }
            throw biae;
          }
        }

        setPropertyMethodVoid.invoke(o, params);
        return true;
      }
    }
    catch (IllegalArgumentException ex2)
    {
      log.warn("IAE " + o + " " + name + " " + value, ex2);
    } catch (SecurityException ex1) {
      if (log.isDebugEnabled())
        log.debug("IntrospectionUtils: SecurityException for " + o.getClass() + " " + name + "=" + value + ")", ex1);
    }
    catch (IllegalAccessException iae)
    {
      if (log.isDebugEnabled())
        log.debug("IntrospectionUtils: IllegalAccessException for " + o.getClass() + " " + name + "=" + value + ")", iae);
    }
    catch (InvocationTargetException ie)
    {
      if (log.isDebugEnabled()) {
        log.debug("IntrospectionUtils: InvocationTargetException for " + o.getClass() + " " + name + "=" + value + ")", ie);
      }
    }

    return false;
  }

  public static Object getValue(Object o, String name)
  {
    if (Map.class.isAssignableFrom(o.getClass())) {
      return ((Map)o).get(name);
    }

    String getter = "get" + capitalize(name);
    String isGetter = "is" + capitalize(name);
    try
    {
      Method[] methods = findMethods(o.getClass());
      Method getPropertyMethod = null;
      for (int i = 0; i < methods.length; i++) {
        Class[] paramT = methods[i].getParameterTypes();
        if ((getter.equals(methods[i].getName())) && (paramT.length == 0)) {
          return methods[i].invoke(o, (Object[])null);
        }
        if ((isGetter.equals(methods[i].getName())) && (paramT.length == 0)) {
          return methods[i].invoke(o, (Object[])null);
        }

        if ("getProperty".equals(methods[i].getName())) {
          getPropertyMethod = methods[i];
        }
      }
      if (getPropertyMethod != null) {
        Object[] params = new Object[1];
        params[0] = name;
        return getPropertyMethod.invoke(o, params);
      }
    }
    catch (Exception ex) {
      log.error("IAE " + o + " " + name, ex);
    }
    return null;
  }

  public static Method findMethod(Class<?> c, String name, Class<?>[] params) {
    Method[] methods = findMethods(c);
    if (methods == null)
      return null;
    for (int i = 0; i < methods.length; i++)
      if (methods[i].getName().equals(name)) {
        Class[] methodParams = methods[i].getParameterTypes();
        if ((methodParams == null) && (
          (params == null) || (params.length == 0)))
          return methods[i];
        if ((params == null) && (
          (methodParams == null) || (methodParams.length == 0)))
          return methods[i];
        if (params.length == methodParams.length)
        {
          boolean found = true;
          for (int j = 0; j < params.length; j++) {
            if (params[j] != methodParams[j]) {
              found = false;
              break;
            }
          }
          if (found)
            return methods[i];
        }
      }
    return null;
  }

  public static Method[] findMethods(Class<?> c) {
    Method[] methods = (Method[])objectMethods.get(c);
    if (methods != null) {
      return methods;
    }
    methods = c.getMethods();
    objectMethods.put(c, methods);
    return methods;
  }

  public static String capitalize(String name) {
    if ((name == null) || (name.length() == 0)) {
      return name;
    }
    char[] chars = name.toCharArray();
    chars[0] = Character.toUpperCase(chars[0]);
    return new String(chars);
  }

  public static String getFieldType(Class<?> c, String columnId)
  {
    try
    {
      String methodName = "get" + columnId.substring(0, 1).toUpperCase() + columnId
        .substring(1);

      Method method = c.getMethod(methodName, new Class[0]);
      return method.getReturnType().getSimpleName(); } catch (Exception e) {
    }
    return null;
  }

  public static List map2list(Map map)
  {
    if (null == map)
      return null;
    Object[] oo = map.keySet().toArray();
    List list = new ArrayList();
    for (Object key : oo) {
      Object value = map.get(key);
      if (null != value)
      {
        list.add(value);
      }
    }
    return list;
  }

  public static Object loadClass(String fullClass)
  {
    try
    {
      return Class.forName(fullClass).newInstance();
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(e.getMessage());
    }
  }

  public static Class<?> loadClassType(String fullClass)
  {
    try
    {
      return Class.forName(fullClass);
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(e.getMessage());
    }
  }

  public static Field getDeclaredField(Object object, String propertyName) throws NoSuchFieldException
  {
    Assert.notNull(object);
    Assert.hasText(propertyName);
    return getDeclaredField(object.getClass(), propertyName);
  }

  public static Field getDeclaredField(Class clazz, String propertyName) throws NoSuchFieldException
  {
    Assert.notNull(clazz);
    Assert.hasText(propertyName);
    for (Class superClass = clazz; superClass != Object.class; superClass = superClass.getSuperclass())
      try {
        return superClass.getDeclaredField(propertyName);
      }
      catch (NoSuchFieldException localNoSuchFieldException)
      {
      }
    throw new NoSuchFieldException("No such field: " + clazz.getName() + '.' + propertyName);
  }

  public static String getObjType(Object obj, String name)
  {
    String type = "";
    try {
      Field field = getDeclaredField(obj, name);
      if (null != field) {
        Class fclass = field.getType();
        type = fclass.getName();
      }
    } catch (Exception localException) {
    }
    return type;
  }

  static
  {
    ConvertRegisterHelper.registerConverters();
  }
}