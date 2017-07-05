package com.webapp.framework.base.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.webapp.framework.base.common.utils.BeanUtil;
import com.webapp.framework.base.common.utils.page.Page;
import com.webapp.framework.base.common.utils.sql.SQLTools;
import com.webapp.framework.base.vo.BaseForm;
import com.webapp.framework.core.exception.BaseException;
import com.webapp.framework.core.utils.StringUtil;

@Repository("baseDao")
public class BaseDao extends AbstractDao {
	protected SqlMapper sqlMapper;

	protected void initDao() throws Exception {
		this.sqlMapper = new SqlMapper(getSqlSession());
	}

	protected int getCountInteger(Object o) {
		if (null == o)
			return 0;
		return Integer.parseInt(o.toString());
	}

	public Date getDate() {
		return (Date) this.sqlMapper.selectOne("select now()", Date.class);
	}

	private String changeHqlToSql(String sql, Map<String, Object> map, Object[] args) {
		if ((null == args) || (args.length == 0)) {
			return sql;
		}
		int index = 0;
		while (sql.indexOf("?") > 0) {
			if (index >= args.length) {
				BaseException.throwException("参数个数大于给定的值个数");
			}
			String arg = "arg" + index;
			sql = sql.replaceFirst("\\?", String.format("#{%s}", new Object[] { arg }));
			map.put(arg, args[index]);
			index++;
		}

		if (index < args.length) {
			BaseException.throwException("给定的值个数大于参数个数");
		}
		return sql;
	}

	protected <T> T uniqueSQLResult(String sql, Class<T> clazz, Object[] args) {
		Map map = new HashMap();

		sql = changeHqlToSql(sql, map, args);
		return (T) uniqueSQLResult(sql, clazz, map);
	}

	protected <T> T uniqueSQLResult(String sql, Class<T> clazz, Map<String, Object> value) {
		if (null == clazz) {
			BaseException.throwException("返回类型不允许为空");
		}
		if (isInSQL(sql)) {
			return getSqlSession().selectOne(sql, value);
		}
		sql = checkSqlFrom(sql);

		sql = SQLTools.convert(sql);

		Map map = this.sqlMapper.selectOne(sql, value);
		if ((null == map) || (map.size() == 0)) {
			return null;
		}

		if ((clazz.getSimpleName().equals("int")) || (clazz.getSimpleName().equals("Integer"))
				|| (clazz.getSimpleName().equals("Double")) || (clazz.getSimpleName().equals("double"))
				|| (clazz.getSimpleName().equals("Short")) || (clazz.getSimpleName().equals("short"))
				|| (clazz.getSimpleName().equals("Long")) || (clazz.getSimpleName().equals("long"))
				|| (clazz.getSimpleName().equals("Float")) || (clazz.getSimpleName().equals("float"))
				|| (clazz.getSimpleName().equals("BigDecimal")) || (clazz.getSimpleName().equals("String")))
			return (T) getOneColumn(map);
		try {
			return (T) BeanUtil.map2Bean(map, clazz);
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
			BaseException.throwException(e.getMessage());
		}

		return null;
	}

	private String checkSqlFrom(String sql) {
		if (sql.trim().toLowerCase().startsWith("from "))
			return "select * " + sql;
		return sql;
	}

	private Object getOneColumn(Map<String, Object> map) {
		if (map.size() != 1)
			BaseException.throwException("返回类型错误，实际返回的数据多于1个字段");
		Iterator localIterator = map.keySet().iterator();
		if (localIterator.hasNext()) {
			String key = (String) localIterator.next();
			return map.get(key);
		}

		return null;
	}

	protected Object[] uniqueSQLResult(String sql, Class<?>[] clazzs, Object[] args) {
		Map map = new HashMap();

		sql = changeHqlToSql(sql, map, args);
		return uniqueSQLResult(sql, clazzs, map);
	}

	protected Object[] uniqueSQLResult(String sql, Class<?>[] clazzs, Map<String, Object> value) {
		if (StringUtil.isNull(sql))
			BaseException.throwException("SQL命令不允许为空");
		if ((null == clazzs) || (clazzs.length == 0)) {
			BaseException.throwException("返回类型不允许为空");
		}
		sql = checkSqlFrom(sql);

		sql = SQLTools.convert(sql);
		Map map = this.sqlMapper.selectOne(sql, value);
		if ((null == map) || (map.size() == 0)) {
			return null;
		}
		Object[] result = new Object[clazzs.length];
		try {
			for (int i = 0; i < clazzs.length; i++) {
				Class clazz = clazzs[i];
				result[i] = BeanUtil.map2Bean(map, clazz);
			}
			return result;
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
			BaseException.throwException(e.getMessage());
		}
		return null;
	}

	protected int execSQLUpdate(String sql, Object[] args) {
		Map map = new HashMap();

		sql = changeHqlToSql(sql, map, args);
		return execSQLUpdate(sql, map);
	}

	protected int execSQLUpdate(String sql, Map<String, Object> value) {
		if (isInSQL(sql)) {
			if (sql.trim().indexOf("insert") >= 0)
				return getSqlSession().insert(sql, value);
			if (sql.trim().indexOf("delete") >= 0) {
				return getSqlSession().delete(sql, value);
			}
			return getSqlSession().update(sql, value);
		}

		sql = SQLTools.convert(sql);

		if (sql.trim().indexOf("insert") >= 0)
			return this.sqlMapper.insert(sql, value);
		if (sql.trim().indexOf("delete") >= 0) {
			return this.sqlMapper.delete(sql, value);
		}
		return this.sqlMapper.update(sql, value);
	}

	protected <T> List<T> list(String sql, Class<T> clazz, Object[] args) {
		Map map = new HashMap();

		sql = changeHqlToSql(sql, map, args);
		return list(sql, clazz, map);
	}

	protected <T> List<T> list(String sql, Class<T> clazz, Map<String, Object> value) {
		if (null == clazz) {
			BaseException.throwException("返回类型不允许为空");
		}
		if (isInSQL(sql)) {
			return getSqlSession().selectList(sql, value);
		}
		sql = checkSqlFrom(sql);

		sql = SQLTools.convert(sql);

		List list = this.sqlMapper.selectList(sql, value);
		return convertListMap2Bean(list, clazz);
	}

	private <T> List<T> convertListMap2Bean(List<Map<String, Object>> list, Class<T> clazz) {
		List result = new ArrayList();
		if ((null == list) || (list.size() == 0)) {
			return result;
		}
		for (Map item : list) {
			try {
				result.add(BeanUtil.map2Bean(item, clazz));
			} catch (Exception e) {
				this.log.error(e.getMessage(), e);
				BaseException.throwException(e.getMessage());
			}
		}

		return result;
	}

	protected List<Object[]> list(String sql, Class<?>[] clazzs, Object[] args) {
		Map map = new HashMap();

		sql = changeHqlToSql(sql, map, args);
		return list(sql, clazzs, map);
	}

	protected List<Object[]> list(String sql, Class<?>[] clazzs, Map<String, Object> map) {
		if (StringUtil.isNull(sql))
			BaseException.throwException("SQL命令不允许为空");
		if ((null == clazzs) || (clazzs.length == 0)) {
			BaseException.throwException("返回类型不允许为空");
		}
		sql = checkSqlFrom(sql);

		sql = SQLTools.convert(sql);

		List list = this.sqlMapper.selectList(sql, map);
		return convertListMap2Bean(list, clazzs);
	}

	private List<Object[]> convertListMap2Bean(List<Map<String, Object>> list, Class<?>[] clazzs) {
		List result = new ArrayList();
		if ((null == list) || (list.size() == 0)) {
			return result;
		}
		for (Map item : list) {
			Object[] resultItem = new Object[clazzs.length];
			try {
				for (int i = 0; i < clazzs.length; i++) {
					Class clazz = clazzs[i];
					resultItem[i] = BeanUtil.map2Bean(item, clazz);
				}
				result.add(resultItem);
			} catch (Exception e) {
				this.log.error(e.getMessage(), e);
				BaseException.throwException(e.getMessage());
			}
		}
		return result;
	}

	protected <T> Page<T> pageQuery(String sql, Class<T> clazz, BaseForm form) {
		if (0 == form.getPageSize())
			form.setPageSize(20);
		if (0 == form.getPageNumber()) {
			form.setPageNumber(1);
		}
		sql = checkSqlFrom(sql);

		if (isInSQL(sql)) {
			sql = SqlHelper.getNamespaceSql(getSqlSession(), sql);
		}

		sql = SQLTools.convert(sql);

		int count = getPageResultRowCount(sql, form);
		Page page = new Page(form, count);

		int start = page.getThisPageFirstElementNumber() - 1;

		sql = sql + String.format(" limit %d,%d",
				new Object[] { Integer.valueOf(start), Integer.valueOf(form.getPageSize()) });

		List list = this.sqlMapper.selectList(sql, form);
		List result = convertListMap2Bean(list, clazz);
		page.setResult(result);

		return page;
	}

	protected Page<Object[]> pageQuery(String sql, Class<?>[] clazzs, BaseForm form) {
		if (0 == form.getPageSize())
			form.setPageSize(20);
		if (0 == form.getPageNumber()) {
			form.setPageNumber(1);
		}
		sql = checkSqlFrom(sql);

		if (isInSQL(sql)) {
			sql = SqlHelper.getNamespaceSql(getSqlSession(), sql);
		}

		sql = SQLTools.convert(sql);
		int count = getPageResultRowCount(sql, form);
		Page page = new Page(form, count);

		int start = page.getThisPageFirstElementNumber() - 1;

		sql = sql + String.format(" limit %d,%d",
				new Object[] { Integer.valueOf(start), Integer.valueOf(form.getPageSize()) });

		List list = this.sqlMapper.selectList(sql, form);
		List result = convertListMap2Bean(list, clazzs);
		page.setResult(result);

		return page;
	}

	private boolean isInSQL(String sql) {
		if (StringUtil.isNull(sql)) {
			BaseException.throwException("SQL命令不允许为空");
		}
		String hql = sql.trim().toLowerCase();

		if ((hql.startsWith("select ")) || (hql.startsWith("delete ")) || (hql.startsWith("update "))
				|| (hql.startsWith("insert ")) || (hql.startsWith("from "))) {
			return false;
		}
		return true;
	}

	private int getPageResultRowCount(String sql, BaseForm form) {
		String hql = sql.trim();
		hql = hql.replaceAll("\r", " ");
		hql = hql.replaceAll("\n", " ");
		hql = hql.replaceAll("\t", " ");

		while (hql.indexOf("  ") > 0) {
			hql = hql.replaceAll("  ", " ");
		}

		if (hql.toLowerCase().indexOf(" order by ") > 0) {
			hql = hql.substring(0, hql.toLowerCase().indexOf(" order by "));
		}
		String countsql = String.format("select count(0) from (%s) as t", new Object[] { hql });
		return ((Integer) this.sqlMapper.selectOne(countsql, form, Integer.class)).intValue();
	}
}