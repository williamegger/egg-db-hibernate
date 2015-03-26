package com.egg.edb.hibernate.query;

import java.util.List;
import java.util.Map;

import com.egg.edb.hibernate.DBHelper;

public class HQLQuery {

	private Query query;

	public HQLQuery(Query query) {
		this.query = query;
	}

	public int update() throws Exception {
		String hql = query.getUpdate();
		Map<String, Object> params = query.getUpdateParams();
		return DBHelper.hql.execute(hql, params);
	}

	public int delete() throws Exception {
		String hql = query.getDelete();
		Map<String, Object> params = query.getDeleteParams();
		return DBHelper.hql.execute(hql, params);
	}

	public <T> T one() throws Exception {
		String hql = query.getSelect();
		Map<String, Object> params = query.getSelectParams();
		return DBHelper.hql.one(hql, params);
	}

	public long count() throws Exception {
		String hql = query.getCount();
		Map<String, Object> params = query.getSelectParams();
		return DBHelper.hql.count(hql, params);
	}

	public boolean exist() throws Exception {
		String hql = query.getCount();
		Map<String, Object> params = query.getSelectParams();
		return DBHelper.hql.exist(hql, params);
	}

	public <T> List<T> query() throws Exception {
		String hql = query.getSelect();
		Map<String, Object> params = query.getSelectParams();
		return DBHelper.hql.query(hql, params);
	}

	public <T> List<T> query(int max) throws Exception {
		String hql = query.getSelect();
		Map<String, Object> params = query.getSelectParams();
		return DBHelper.hql.query(hql, params, max);
	}

	public <T> List<T> query(int page, int rows) throws Exception {
		String hql = query.getSelect();
		Map<String, Object> params = query.getSelectParams();
		int start = (page - 1) * rows;
		return DBHelper.hql.query(hql, params, start, rows);
	}

	public PageInfo page(int page, int rows) throws Exception {
		String selectHQL = query.getSelect();
		String countHQL = query.getCount();
		Map<String, Object> params = query.getSelectParams();
		return DBHelper.hql.page(selectHQL, countHQL, params, page, rows);
	}

}
