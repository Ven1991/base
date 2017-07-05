package com.webapp.framework.base.common.utils.sql;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.Assert;

import com.webapp.framework.core.utils.StringUtil;

public class SqlRemoveUtils {
	private static int indexOfByRegex(String input, String regex) {
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(input);
		if (m.find()) {
			return m.start();
		}
		return -1;
	}

	public static String getSelectCols(String sql) {
		if (sql.trim().toLowerCase().startsWith("from")) {
			return "";
		}
		int beginPos = getBeginPos(sql.trim());
		if (beginPos == 0) {
			return "";
		}
		String str = sql.substring(0, beginPos);
		if (StringUtil.isNull(str)) {
			return str;
		}
		return str.replace("select", "");
	}

	public static String removeSelectLastFrom(String hql) {
		Assert.hasText(hql);
		int beginPos = hql.toLowerCase().lastIndexOf("from");
		Assert.isTrue(beginPos != -1, " hql : " + hql + " must has a keyword 'from'");
		return hql.substring(beginPos);
	}

	public static String removeSelect(String sql) {
		int beginPos = getBeginPos(sql);
		return sql.substring(beginPos);
	}

	private static int getBeginPos(String sql) {
		Assert.hasText(sql);
		int beginPos = indexOfByRegex(sql.toLowerCase(), "\\sfrom\\s");
		Assert.isTrue(beginPos != -1, " sql : " + sql + " must has a keyword 'from'");
		return beginPos;
	}

	public static String removeOrders(String sql) {
		Assert.hasText(sql);
		Pattern p = Pattern.compile("order\\s*by[\\w|\\W|\\s|\\S]*", 2);
		Matcher m = p.matcher(sql);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			m.appendReplacement(sb, "");
		}
		m.appendTail(sb);
		return sb.toString();
	}

	public static String removeFetchKeyword(String sql) {
		return sql.replaceAll("(?i)fetch", "");
	}

	public static String removeXsqlBuilderOrders(String string) {
		Assert.hasText(string);
		Pattern p = Pattern.compile("/~.*order\\s*by[\\w|\\W|\\s|\\S]*~/", 2);
		Matcher m = p.matcher(string);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			m.appendReplacement(sb, "");
		}
		m.appendTail(sb);
		return removeOrders(sb.toString());
	}

	public static String removeGroupBy(String hql) {
		Assert.hasText(hql);
		Pattern p = Pattern.compile("group\\s*by[\\w|\\W|\\s|\\S]*", 2);
		Matcher m = p.matcher(hql);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			m.appendReplacement(sb, "");
		}
		m.appendTail(sb);
		return sb.toString();
	}

	public static boolean hasDistinctOrGroupBy(String str) {
		Pattern p = Pattern.compile("group\\s*by[\\w|\\W|\\s|\\S]*", 2);
		Matcher m = p.matcher(str);
		if (m.find()) {
			return true;
		}
		p = Pattern.compile("distinct ", 2);
		m = p.matcher(str);
		return m.find();
	}
}