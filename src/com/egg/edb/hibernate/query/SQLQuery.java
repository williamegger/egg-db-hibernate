package com.egg.edb.hibernate.query;

import java.util.List;
import java.util.Map;

import com.egg.edb.hibernate.bean.PageInfo;
import com.egg.edb.hibernate.bean.SqlEntity;
import com.egg.edb.hibernate.bean.SqlScalar;
import com.egg.edb.hibernate.helper.DBHelper;

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

	public <T> T one() throws Exception {
		String sql = query.getSelect();
		Map<String, Object> params = query.getSelectParams();
		return DBHelper.sql.one(sql, params);
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

	public <T> List<T> query(SqlEntity entity) throws Exception {
		String sql = query.getSelect();
		Map<String, Object> params = query.getSelectParams();
		return DBHelper.sql.query(sql, entity, params);
	}

	public <T> List<T> query(SqlEntity entity, int max) throws Exception {
		String sql = query.getSelect();
		Map<String, Object> params = query.getSelectParams();
		return DBHelper.sql.query(sql, entity, params, max);
	}

	public <T> List<T> query(SqlEntity entity, int page, int rows) throws Exception {
		String sql = query.getSelect();
		Map<String, Object> params = query.getSelectParams();
		int start = (page - 1) * rows;
		return DBHelper.sql.query(sql, entity, params, start, rows);
	}

	public PageInfo page(SqlEntity entity, int page, int rows) throws Exception {
		String selectSQL = query.getSelect();
		String countSQL = query.getCount();
		Map<String, Object> params = query.getSelectParams();
		return DBHelper.sql.page(selectSQL, countSQL, entity, params, page, rows);
	}

	public <T> List<T> query(List<SqlScalar> scalars, boolean toMap) throws Exception {
		String sql = query.getSelect();
		Map<String, Object> params = query.getSelectParams();
		return DBHelper.sql.query(sql, scalars, params, toMap);
	}

	public <T> List<T> query(List<SqlScalar> scalars, boolean toMap, int max) throws Exception {
		String sql = query.getSelect();
		Map<String, Object> params = query.getSelectParams();
		return DBHelper.sql.query(sql, scalars, params, max, toMap);
	}

	public <T> List<T> query(List<SqlScalar> scalars, boolean toMap, int page, int rows) throws Exception {
		String sql = query.getSelect();
		Map<String, Object> params = query.getSelectParams();
		int start = (page - 1) * rows;
		return DBHelper.sql.query(sql, scalars, params, start, rows, toMap);
	}

	public PageInfo page(List<SqlScalar> scalars, boolean toMap, int page, int rows) throws Exception {
		String selectSQL = query.getSelect();
		String countSQL = query.getCount();
		Map<String, Object> params = query.getSelectParams();
		return DBHelper.sql.page(selectSQL, countSQL, scalars, params, page, rows, toMap);
	}

}
