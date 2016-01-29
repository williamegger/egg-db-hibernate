package com.egg.db.hibernate.query;

import java.util.List;
import java.util.Map;

import com.egg.db.hibernate.bean.PageInfo;
import com.egg.db.hibernate.bean.SqlType;
import com.egg.db.hibernate.helper.DBHelper;

public class SQLQuery {

	private Query query;

	public SQLQuery(Query query) {
		this.query = query;
	}

	public void insert() throws Exception {
		String sql = query.getInsert();
		Map<String, Object> params = query.getInsertParams();
		DBHelper.sql.execute(sql, params);
	}

	public int update() throws Exception {
		String sql = query.getUpdate();
		Map<String, Object> params = query.getUpdateParams();
		return DBHelper.sql.execute(sql, params);
	}

	public int delete() throws Exception {
		String sql = query.getDelete();
		Map<String, Object> params = query.getDeleteParams();
		return DBHelper.sql.execute(sql, params);
	}

	public <T> T one(boolean toMap) throws Exception {
		String sql = query.getSelect();
		Map<String, Object> params = query.getSelectParams();
		return DBHelper.sql.one(sql, params, toMap);
	}

	public <T> T one(List<SqlType> types, boolean toMap) throws Exception {
		String sql = query.getSelect();
		Map<String, Object> params = query.getSelectParams();
		return DBHelper.sql.one(sql, types, params, toMap);
	}

	public long count() throws Exception {
		String sql = query.getCount();
		Map<String, Object> params = query.getSelectParams();
		return DBHelper.sql.count(sql, params);
	}

	public boolean exist() throws Exception {
		String sql = query.getCount();
		Map<String, Object> params = query.getSelectParams();
		return DBHelper.sql.exist(sql, params);
	}

	public <T> List<T> query(List<SqlType> types, boolean toMap) throws Exception {
		String sql = query.getSelect();
		Map<String, Object> params = query.getSelectParams();
		return DBHelper.sql.query(sql, types, params, toMap);
	}

	public <T> List<T> query(List<SqlType> types, int max, boolean toMap) throws Exception {
		String sql = query.getSelect();
		Map<String, Object> params = query.getSelectParams();
		return DBHelper.sql.query(sql, types, params, max, toMap);
	}

	public <T> List<T> query(List<SqlType> types, int page, int rows, boolean toMap) throws Exception {
		String sql = query.getSelect();
		Map<String, Object> params = query.getSelectParams();
		int start = (page - 1) * rows;
		return DBHelper.sql.query(sql, types, params, start, rows, toMap);
	}

	public PageInfo page(List<SqlType> types, int page, int rows, boolean toMap) throws Exception {
		String selectSQL = query.getSelect();
		String countSQL = query.getCount();
		Map<String, Object> params = query.getSelectParams();
		return DBHelper.sql.page(selectSQL, countSQL, types, params, page, rows, toMap);
	}

}
