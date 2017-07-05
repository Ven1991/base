package com.webapp.framework.base.common.utils.page;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class PageQuery implements Serializable {
	private static final long serialVersionUID = -8000900575354501298L;
	public static final int DEFAULT_PAGE_SIZE = 30;
	private String sortColumns;
	private int page;
	private int pageSize = 30;

	protected int totalCount = 0;

	private boolean needPage = true;
	private boolean needTotal;
	private boolean needGroup;
	private boolean beginTotal;
	private boolean priorTotal;
	private Map<String, String> totalProperty;
	private Map<String, String> summaryMap;
	private Map<String, String> beginMap;
	private Map<String, Map<String, String>> subtotalMap;
	private String group;
	private boolean last = false;
	private String otherGroupHql;
	private List<Object> otherGroupParams;

	public PageQuery() {
	}

	public PageQuery(int pageSize) {
		this.pageSize = pageSize;
	}

	public PageQuery(PageQuery query) {
		this.page = query.page;
		this.pageSize = query.pageSize;
	}

	public PageQuery(int page, int pageSize) {
		this.page = page;
		this.pageSize = pageSize;
	}

	public int getPage() {
		return this.page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPageNumber() {
		return this.page;
	}

	public void setPageNumber(int page) {
		this.page = page;
	}

	public int getPageSize() {
		return this.pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

	public String getSortColumns() {
		return this.sortColumns;
	}

	public void setSortColumns(String sortColumns) {
		this.sortColumns = sortColumns;
	}

	public boolean isNeedPage() {
		return this.needPage;
	}

	public void setNeedPage(boolean needPage) {
		this.needPage = needPage;
	}

	public boolean isNeedTotal() {
		return this.needTotal;
	}

	public void setNeedTotal(boolean needTotal) {
		this.needTotal = needTotal;
	}

	public boolean isNeedGroup() {
		return this.needGroup;
	}

	public void setNeedGroup(boolean needGroup) {
		this.needGroup = needGroup;
	}

	public boolean isBeginTotal() {
		return this.beginTotal;
	}

	public void setBeginTotal(boolean beginTotal) {
		this.beginTotal = beginTotal;
	}

	public boolean isPriorTotal() {
		return this.priorTotal;
	}

	public void setPriorTotal(boolean priorTotal) {
		this.priorTotal = priorTotal;
	}

	public Map<String, String> getTotalProperty() {
		return this.totalProperty;
	}

	public void setTotalProperty(Map<String, String> totalProperty) {
		this.totalProperty = totalProperty;
	}

	public Map<String, String> getSummaryMap() {
		return this.summaryMap;
	}

	public void setSummaryMap(Map<String, String> summaryMap) {
		this.summaryMap = summaryMap;
	}

	public Map<String, String> getBeginMap() {
		return this.beginMap;
	}

	public void setBeginMap(Map<String, String> beginMap) {
		this.beginMap = beginMap;
	}

	public Map<String, Map<String, String>> getSubtotalMap() {
		return this.subtotalMap;
	}

	public void setSubtotalMap(Map<String, Map<String, String>> subtotalMap) {
		this.subtotalMap = subtotalMap;
	}

	public String getGroup() {
		return this.group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public int getTotalCount() {
		return this.totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getFirstResult() {
		return PageUtils.getFirstResult(this.page, this.pageSize);
	}

	public boolean isLast() {
		return this.last;
	}

	public void setLast(boolean last) {
		this.last = last;
	}

	public String getOtherGroupHql() {
		return this.otherGroupHql;
	}

	public void setOtherGroupHql(String otherGroupHql) {
		this.otherGroupHql = otherGroupHql;
	}

	public List<Object> getOtherGroupParams() {
		return this.otherGroupParams;
	}

	public void setOtherGroupParams(List<Object> otherGroupParams) {
		this.otherGroupParams = otherGroupParams;
	}
}