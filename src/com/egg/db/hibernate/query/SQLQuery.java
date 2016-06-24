package com.egg.db.hibernate.query;

import java.util.List;
import java.util.Map;

import com.egg.db.hibernate.bean.PageInfo;
import com.egg.db.hibernate.bean.SqlType;
import com.egg.db.hibernate.helper.DBHelper;
import com.egg.db.hibernate.template.Arrayer;
import com.egg.db.hibernate.template.Mapper;
import com.egg.db.hibernate.template.TemplateUtil;

public class SQLQuery {

	private Query query;
	private Arrayer arrayer;
	private Mapper mapper;

	public SQLQuery(Query query) {
		this.query = query;
	}

	public SQLQuery setTemplate(Arrayer arrayer) {
		this.arrayer = arrayer;
		return this;
	}

	public SQLQuery setTemplate(Mapper mapper) {
		this.mapper = mapper;
		return this;
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
		Object rst = DBHelper.sql.one(sql, params, toMap);
		return doTemplate(rst, toMap);
	}

	public <T> T one(List<SqlType> types, boolean toMap) throws Exception {
		String sql = query.getSelect();
		Map<String, Object> params = query.getSelectParams();
		Object rst = DBHelper.sql.one(sql, types, params, toMap);
		return doTemplate(rst, toMap);
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
		Object rst = DBHelper.sql.query(sql, types, params, toMap);
		return doTemplateList(rst, toMap);
	}

	public <T> List<T> query(List<SqlType> types, int max, boolean toMap) throws Exception {
		String sql = query.getSelect();
		Map<String, Object> params = query.getSelectParams();
		Object rst = DBHelper.sql.query(sql, types, params, max, toMap);
		return doTemplateList(rst, toMap);
	}

	public <T> List<T> query(List<SqlType> types, int page, int rows, boolean toMap) throws Exception {
		String sql = query.getSelect();
		Map<String, Object> params = query.getSelectParams();
		int start = (page - 1) * rows;
		Object rst = DBHelper.sql.query(sql, types, params, start, rows, toMap);
		return doTemplateList(rst, toMap);
	}

	public PageInfo page(List<SqlType> types, int page, int rows, boolean toMap) throws Exception {
		String selectSQL = query.getSelect();
		String countSQL = query.getCount();
		Map<String, Object> params = query.getSelectParams();
		return DBHelper.sql.page(selectSQL, countSQL, types, params, page, rows, toMap);
	}

	private <T> T doTemplate(Object rst, boolean toMap) {
		if (rst != null) {
			if (toMap && mapper != null) {
				return mapper.build((Map) rst);
			} else if (!toMap && arrayer != null) {
				return arrayer.build((Object[]) rst);
			}
		}
		return (T) rst;
	}

	private <T> List<T> doTemplateList(Object rst, boolean toMap) {
		if (rst != null) {
			if (toMap && mapper != null) {
				return TemplateUtil.build((List<Map>) rst, mapper);
			} else if (!toMap && arrayer != null) {
				return TemplateUtil.build((List<Object[]>) rst, arrayer);
			}
		}
		return (List<T>) rst;
	}
}
