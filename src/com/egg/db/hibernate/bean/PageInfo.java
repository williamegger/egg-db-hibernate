package com.egg.db.hibernate.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PageInfo implements Serializable {

	private static final long serialVersionUID = -6421128312880432964L;
	public static final int DEFAULT_ROWS = 10;

	private List list;
	private int count;

	private int page;
	private int rows;
	private int firstPage;
	private int lastPage;
	private int prev;
	private int next;
	private boolean first;
	private boolean last;

	private List<Integer> pagination;

	private PageInfo(List list, int count, int page, int rows) {
		this.list = list;
		this.count = count;
		this.page = page;
		this.rows = rows;
	}

	public static PageInfo build(List list, int count, int page, int rows) {
		PageInfo info = new PageInfo(list, count, page, rows);
		info.init();
		return info;
	}

	/*
	 * 初始化
	 */
	private void init() {
		if (rows <= 0) {
			rows = DEFAULT_ROWS;
		}
		if (count > 0) {
			firstPage = 1;
			lastPage = (int) Math.ceil(1d * count / rows);
			page = Math.min(page, lastPage);
			prev = Math.max(firstPage, page - 1);
			next = Math.min(page + 1, lastPage);

			// 计算显示的页面信息
			initPagination();
		} else {
			count = 0;
			page = 0;
		}
		first = (firstPage == page);
		last = (lastPage == page);
	}

	/*
	 * 初始化页码信息（pagination）
	 */
	private void initPagination() {
		int start = firstPage;
		int end = lastPage;
		int maxShowPage = 10;
		int padLeft = 5;
		if (lastPage <= maxShowPage) {
			end = lastPage;
		} else if (page <= padLeft + 1) {
			end = maxShowPage;
		} else if (page >= lastPage - padLeft + 1) {
			start = lastPage - maxShowPage + 1;
			end = lastPage;
		} else {
			start = page - padLeft;
			end = page + padLeft - 1;
		}
		pagination = new ArrayList<Integer>();
		for (int i = start; i <= end; i++) {
			pagination.add(i);
		}
	}

	// ------------
	// get set
	// ------------
	public List getList() {
		return list;
	}

	public void setList(List list) {
		this.list = list;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public int getFirstPage() {
		return firstPage;
	}

	public void setFirstPage(int firstPage) {
		this.firstPage = firstPage;
	}

	public int getLastPage() {
		return lastPage;
	}

	public void setLastPage(int lastPage) {
		this.lastPage = lastPage;
	}

	public int getPrev() {
		return prev;
	}

	public void setPrev(int prev) {
		this.prev = prev;
	}

	public int getNext() {
		return next;
	}

	public void setNext(int next) {
		this.next = next;
	}

	public boolean isFirst() {
		return first;
	}

	public void setFirst(boolean first) {
		this.first = first;
	}

	public boolean isLast() {
		return last;
	}

	public void setLast(boolean last) {
		this.last = last;
	}

	public List<Integer> getPagination() {
		return pagination;
	}

	public void setPagination(List<Integer> pagination) {
		this.pagination = pagination;
	}

}
