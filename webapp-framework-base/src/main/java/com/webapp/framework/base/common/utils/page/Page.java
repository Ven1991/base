package com.webapp.framework.base.common.utils.page;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Page<T> implements Serializable, Iterable<T> {
	private static final long serialVersionUID = 1L;
	protected List<T> result;
	protected int pageSize;
	protected int pageNumber;
	protected int totalCount = 0;
	protected int totalPage;

	public Page(PageQuery p, int totalCount) {
		this(p.getPage(), p.getPageSize(), totalCount);
	}

	public Page(int pageNumber, int pageSize, int totalCount) {
		this(pageNumber, pageSize, totalCount, new ArrayList(0));
	}

	public Page(int pageNumber, int pageSize, int totalCount, List<T> result) {
		if (pageSize <= 0)
			throw new IllegalArgumentException("[pageSize] must great than zero");
		this.pageSize = pageSize;
		this.pageNumber = PageUtils.computePageNumber(pageNumber, pageSize, totalCount);
		this.totalCount = totalCount;
		this.totalPage = PageUtils.computeLastPageNumber(totalCount, pageSize);
		setResult(result);
	}

	public void setResult(List<T> elements) {
		if (elements == null)
			throw new IllegalArgumentException("'result' must be not null");
		this.result = elements;
	}

	public List<T> getResult() {
		return this.result;
	}

	public boolean isFirstPage() {
		return getThisPageNumber() == 1;
	}

	public boolean isLastPage() {
		return getThisPageNumber() >= getLastPageNumber();
	}

	public boolean isHasNextPage() {
		return getLastPageNumber() > getThisPageNumber();
	}

	public int getTotalPage() {
		return this.totalPage;
	}

	public boolean isHasPreviousPage() {
		return getThisPageNumber() > 1;
	}

	public int getLastPageNumber() {
		return PageUtils.computeLastPageNumber(this.totalCount, this.pageSize);
	}

	public int getTotalCount() {
		return this.totalCount;
	}

	public int getThisPageFirstElementNumber() {
		return (getThisPageNumber() - 1) * getPageSize() + 1;
	}

	public int getThisPageLastElementNumber() {
		int fullPage = getThisPageFirstElementNumber() + getPageSize() - 1;
		return getTotalCount() < fullPage ? getTotalCount() : fullPage;
	}

	public int getNextPageNumber() {
		return getThisPageNumber() + 1;
	}

	public int getPreviousPageNumber() {
		return getThisPageNumber() - 1;
	}

	public int getPageSize() {
		return this.pageSize;
	}

	public int getThisPageNumber() {
		return this.pageNumber;
	}

	public Integer[] getLinkPageNumbers() {
		return linkPageNumbers(7);
	}

	public Integer[] linkPageNumbers(int count) {
		return PageUtils.generateLinkPageNumbers(getThisPageNumber(), getLastPageNumber(), count);
	}

	public int getFirstResult() {
		return PageUtils.getFirstResult(this.pageNumber, this.pageSize);
	}

	public Iterator<T> iterator() {
		return this.result == null ? new ArrayList().iterator() : this.result.iterator();
	}

	public Page() {
	}

	public boolean isEmpty() {
		return (null == this) || (null == getResult()) || (getResult().isEmpty());
	}

	public boolean isNotEmpty() {
		return !isEmpty();
	}
}