package com.webapp.framework.base.dao;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandlerRegistry;

public class SqlHelper {
	public static String getMapperSql(Object mapper, String methodName, Object[] args) {
		MetaObject metaObject = SystemMetaObject.forObject(mapper);
		SqlSession session = (SqlSession) metaObject.getValue("h.sqlSession");
		Class mapperInterface = (Class) metaObject.getValue("h.mapperInterface");
		String fullMethodName = mapperInterface.getCanonicalName() + "." + methodName;
		if ((args == null) || (args.length == 0)) {
			return getNamespaceSql(session, fullMethodName, null);
		}
		return getMapperSql(session, mapperInterface, methodName, args);
	}

	public static String getMapperSql(SqlSession session, String fullMapperMethodName, Object[] args) {
		if ((args == null) || (args.length == 0)) {
			return getNamespaceSql(session, fullMapperMethodName, null);
		}

		String methodName = fullMapperMethodName.substring(fullMapperMethodName.lastIndexOf('.') + 1);
		Class mapperInterface = null;
		try {
			mapperInterface = Class.forName(fullMapperMethodName.substring(0, fullMapperMethodName.lastIndexOf('.')));

			return getMapperSql(session, mapperInterface, methodName, args);
		} catch (ClassNotFoundException e) {
		}
		throw new IllegalArgumentException("参数" + fullMapperMethodName + "无效！");
	}

	public static String getMapperSql(SqlSession session, Class mapperInterface, String methodName, Object[] args) {
		String fullMapperMethodName = mapperInterface.getCanonicalName() + "." + methodName;
		if ((args == null) || (args.length == 0)) {
			return getNamespaceSql(session, fullMapperMethodName, null);
		}
		Method method = getDeclaredMethods(mapperInterface, methodName);
		Map params = new HashMap();
		Class[] argTypes = method.getParameterTypes();
		for (int i = 0; i < argTypes.length; i++) {
			if ((!RowBounds.class.isAssignableFrom(argTypes[i]))
					&& (!ResultHandler.class.isAssignableFrom(argTypes[i]))) {
				String paramName = "param" + String.valueOf(params.size() + 1);
				paramName = getParamNameFromAnnotation(method, i, paramName);
				params.put(paramName, i >= args.length ? null : args[i]);
			}
		}
		if ((args != null) && (args.length == 1)) {
			Object _params = wrapCollection(args[0]);
			if ((_params instanceof Map)) {
				params.putAll((Map) _params);
			}
		}
		return getNamespaceSql(session, fullMapperMethodName, params);
	}

	public static String getNamespaceSql(SqlSession session, String namespace) {
		return getNamespaceSql(session, namespace, null);
	}

	public static String getNamespaceSql(SqlSession session, String namespace, Object params) {
		params = wrapCollection(params);
		Configuration configuration = session.getConfiguration();
		MappedStatement mappedStatement = configuration.getMappedStatement(namespace);

		TypeHandlerRegistry typeHandlerRegistry = mappedStatement.getConfiguration().getTypeHandlerRegistry();
		BoundSql boundSql = mappedStatement.getBoundSql(params);
		List parameterMappings = boundSql.getParameterMappings();
		String sql = boundSql.getSql();
		if (parameterMappings != null) {
			for (int i = 0; i < parameterMappings.size(); i++) {
				ParameterMapping parameterMapping = (ParameterMapping) parameterMappings.get(i);
				if (parameterMapping.getMode() != ParameterMode.OUT) {
					String propertyName = parameterMapping.getProperty();
					Object value;
					if (boundSql.hasAdditionalParameter(propertyName)) {
						value = boundSql.getAdditionalParameter(propertyName);
					} else {
						if (params == null) {
							value = null;
						} else {
							if (typeHandlerRegistry.hasTypeHandler(params.getClass())) {
								value = params;
							} else {
								MetaObject metaObject = configuration.newMetaObject(params);
								value = metaObject.getValue(propertyName);
							}
						}
					}
					JdbcType jdbcType = parameterMapping.getJdbcType();
					if ((value == null) && (jdbcType == null))
						jdbcType = configuration.getJdbcTypeForNull();
					sql = replaceParameter(sql, value, jdbcType, parameterMapping.getJavaType());
				}
			}
		}
		return sql;
	}

	private static String replaceParameter(String sql, Object value, JdbcType jdbcType, Class javaType)
  {
    String strValue = String.valueOf(value);
    if (jdbcType != null) {
      switch (jdbcType.ordinal())
      {
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:
      case 7:
      case 8:
      case 9:
      case 10:
        break;
      case 11:
      case 12:
      case 13:
      default:
        strValue = "'" + strValue + "'"; break;
      }
    }
    else if (!Number.class.isAssignableFrom(javaType))
    {
      strValue = "'" + strValue + "'";
    }
    return sql.replaceFirst("\\?", strValue);
  }

	private static Method getDeclaredMethods(Class clazz, String methodName) {
		Method[] methods = clazz.getDeclaredMethods();
		for (Method method : methods) {
			if (method.getName().equals(methodName)) {
				return method;
			}
		}
		throw new IllegalArgumentException("方法" + methodName + "不存在！");
	}

	private static String getParamNameFromAnnotation(Method method, int i, String paramName) {
		Object[] paramAnnos = method.getParameterAnnotations()[i];
		for (Object paramAnno : paramAnnos) {
			if ((paramAnno instanceof Param)) {
				paramName = ((Param) paramAnno).value();
			}
		}
		return paramName;
	}

	private static Object wrapCollection(Object object) {
		if ((object instanceof List)) {
			Map map = new HashMap();
			map.put("list", object);
			return map;
		}
		if ((object != null) && (object.getClass().isArray())) {
			Map map = new HashMap();
			map.put("array", object);
			return map;
		}
		return object;
	}
}