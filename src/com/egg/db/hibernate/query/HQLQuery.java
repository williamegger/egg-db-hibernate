package com.egg.db.hibernate.query;

import java.util.List;
import java.util.Map;

import com.egg.db.hibernate.bean.PageInfo;
import com.egg.db.hibernate.helper.DBHelper;
import com.egg.db.hibernate.template.Arrayer;
import com.egg.db.hibernate.template.TemplateUtil;

public class HQLQuery {

	private Query query;
	private Arrayer<?> arrayer;

	public HQLQuery(Query query) {
		this.query = query;
	}

	public HQLQuery setTemplate(Arrayer<?> arrayer) {
		this.arrayer = arrayer;
		return this;
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
		Object rst = DBHelper.hql.one(hql, params);
		return doTemplate(rst);
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
		Object rst = DBHelper.hql.query(hql, params);
		return doTemplateList(rst);
	}

	public <T> List<T> query(int max) throws Exception {
		String hql = query.getSelect();
		Map<String, Object> params = query.getSelectParams();
		Object rst = DBHelper.hql.query(hql, params, max);
		return doTemplateList(rst);
	}

	public <T> List<T> query(int page, int rows) throws Exception {
		String hql = query.getSelect();
		Map<String, Object> params = query.getSelectParams();
		int start = (page - 1) * rows;
		Object rst = DBHelper.hql.query(hql, params, start, rows);
		return doTemplateList(rst);
	}

	public PageInfo page(int page, int rows) throws Exception {
		String selectHQL = query.getSelect();
		String countHQL = query.getCount();
		Map<String, Object> params = query.getSelectParams();
		return DBHelper.hql.page(selectHQL, countHQL, params, page, rows);
	}

	public Map map(String... keys) throws Exception {
		Object[] objs = one();
		return DBHelper.arrayToMap(objs, keys);
	}

	public List<Map> queryMaps(int max, String... keys) throws Exception {
		List<Object[]> list = query(max);
		return DBHelper.arrayToMap(list, keys);
	}

	private <T> T doTemplate(Object rst) {
		if (arrayer != null && rst != null) {
			return (T) arrayer.build((Object[]) rst);
		}
		return (T) rst;
	}

	private <T> List<T> doTemplateList(Object rst) {
		if (arrayer != null && rst != null) {
			return TemplateUtil.build((List<Object[]>) rst, arrayer);
		}
		return (List<T>) rst;
	}
}
