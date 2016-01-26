package com.egg.edb.hibernate.helper;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.egg.edb.hibernate.HibernateUtil;
import com.egg.edb.hibernate.bean.PageInfo;

/**
 * Hibernate使用HQL语句的辅助类
 */
public class HQLHelper {

	private static final Logger LOG = LoggerFactory.getLogger(HQLHelper.class);
	private static HQLHelper instance = null;

	static synchronized HQLHelper getInstance() {
		if (instance == null) {
			instance = new HQLHelper();
		}
		return instance;
	}

	private HQLHelper() {
	}

	// ----------------------
	// TODO executeUpdate
	// ----------------------
	public int execute(CharSequence hql) throws Exception {
		return execute(hql, null, null);
	}

	public int execute(CharSequence hql, Map<String, Object> params) throws Exception {
		return execute(hql, params, null);
	}

	public int execute(CharSequence hql, Object[] params) throws Exception {
		return execute(hql, null, params);
	}

	public int update(String entityName, String[] sets, String[] wheres, Map<String, Object> params) throws Exception {
		if (isBlank(entityName) || isBlank(sets)) {
			throw error(".update() : String entityName 和 String[] sets 不能为空");
		}

		String hql = buildHQLForUpdate(entityName, sets, wheres);
		return execute(hql, params, null);
	}

	public int update(String entityName, String[] sets, String[] wheres, Object[] params) throws Exception {
		if (isBlank(entityName) || isBlank(sets)) {
			throw error(".update() : String entityName 和 String[] sets 不能为空");
		}

		String hql = buildHQLForUpdate(entityName, sets, wheres);
		return execute(hql, null, params);
	}

	public int delete(String entityName, String[] wheres, Map<String, Object> params) throws Exception {
		if (isBlank(entityName)) {
			throw error(".delete() : String entityName 不能为空");
		}

		String hql = buildHQLForDelete(entityName, wheres);
		return execute(hql, params, null);
	}

	public int delete(String entityName, String[] wheres, Object[] params) throws Exception {
		if (isBlank(entityName)) {
			throw error(".delete() : String entityName 不能为空");
		}

		String hql = buildHQLForDelete(entityName, wheres);
		return execute(hql, null, params);
	}

	/**
	 * 构建update操作的HQL语句
	 */
	private String buildHQLForUpdate(String entityName, String[] sets, String[] wheres) {
		StringBuffer hql = new StringBuffer();
		hql.append("update ").append(entityName);

		hql.append(" set ");
		for (int i = 0, len = sets.length; i < len; i++) {
			if (i > 0) {
				hql.append(", ");
			}
			hql.append(sets[i]);
		}

		if (!isBlank(wheres)) {
			hql.append(" where ");
			for (int i = 0, len = wheres.length; i < len; i++) {
				if (i > 0) {
					hql.append(" and ");
				}
				hql.append(wheres[i]);
			}
		}

		return hql.toString();
	}

	/**
	 * 构建delete操作的HQL语句
	 */
	private String buildHQLForDelete(String entityName, String[] wheres) {
		StringBuffer hql = new StringBuffer();
		hql.append("delete from ").append(entityName);

		if (!isBlank(wheres)) {
			hql.append(" where ");
			for (int i = 0, len = wheres.length; i < len; i++) {
				if (i > 0) {
					hql.append(" and ");
				}
				hql.append(wheres[i]);
			}
		}

		return hql.toString();
	}

	/**
	 * 使用HQL语句，执行executeUpdate操作
	 */
	private int execute(CharSequence hql, Map<String, Object> map, Object[] params) throws Exception {
		Object idf = null;
		Session session = null;
		Transaction tx = null;
		try {
			idf = HibernateUtil.createSession();
			session = HibernateUtil.getSession();
			tx = session.beginTransaction();

			Query q = session.createQuery(hql.toString());
			if (!isBlank(map)) {
				q.setProperties(map);
			}
			if (params != null) {
				for (int i = 0, len = params.length; i < len; i++) {
					q.setParameter(i, params[i]);
				}
			}
			int rows = q.executeUpdate();

			session.flush();
			session.clear();
			tx.commit();
			return rows;
		} catch (Exception e) {
			if (tx != null) {
				tx.rollback();
				session.clear();
			}

			throw error(".execute()", e);
		} finally {
			if (idf != null && session != null) {
				closeQuickly(idf);
			}
		}
	}

	// ----------------------
	// TODO one/count/exist
	// ----------------------
	public <T> T one(CharSequence hql) throws Exception {
		return one(hql, null, null);
	}

	public <T> T one(CharSequence hql, Map<String, Object> params) throws Exception {
		return one(hql, params, null);
	}

	public <T> T one(CharSequence hql, Object[] params) throws Exception {
		return one(hql, null, params);
	}

	/**
	 * 得到一条记录
	 */
	private <T> T one(CharSequence hql, Map<String, Object> map, Object[] params) throws Exception {
		List<T> list = query(hql, map, params, 0, 1);
		return (isBlank(list) ? null : list.get(0));
	}

	public long count(CharSequence hql) throws Exception {
		return count(hql, null, null);
	}

	public long count(CharSequence hql, Map<String, Object> params) throws Exception {
		return count(hql, params, null);
	}

	public long count(CharSequence hql, Object[] params) throws Exception {
		return count(hql, null, params);
	}

	/**
	 * 得到数量
	 */
	private long count(CharSequence hql, Map<String, Object> map, Object[] params) throws Exception {
		Long c = one(hql, map, params);
		return (c == null ? 0 : c.longValue());
	}

	public boolean exist(CharSequence hql) throws Exception {
		long c = count(hql);
		return c > 0;
	}

	public boolean exist(CharSequence hql, Map<String, Object> params) throws Exception {
		long c = count(hql, params);
		return c > 0;
	}

	public boolean exist(CharSequence hql, Object[] params) throws Exception {
		long c = count(hql, params);
		return c > 0;
	}

	// ------------
	// TODO query
	// ------------
	public <T> List<T> query(CharSequence hql) throws Exception {
		return query(hql, null, null, 0, 0);
	}

	public <T> List<T> query(CharSequence hql, int max) throws Exception {
		return query(hql, null, null, 0, max);
	}

	public <T> List<T> query(CharSequence hql, int start, int max) throws Exception {
		return query(hql, null, null, start, max);
	}

	public <T> List<T> query(CharSequence hql, Map<String, Object> params) throws Exception {
		return query(hql, params, null, 0, 0);
	}

	public <T> List<T> query(CharSequence hql, Map<String, Object> params, int max) throws Exception {
		return query(hql, params, null, 0, max);
	}

	public <T> List<T> query(CharSequence hql, Map<String, Object> params, int start, int max) throws Exception {
		return query(hql, params, null, start, max);
	}

	public <T> List<T> query(CharSequence hql, Object[] params) throws Exception {
		return query(hql, null, params, 0, 0);
	}

	public <T> List<T> query(CharSequence hql, Object[] params, int max) throws Exception {
		return query(hql, null, params, 0, max);
	}

	public <T> List<T> query(CharSequence hql, Object[] params, int start, int max) throws Exception {
		return query(hql, null, params, start, max);
	}

	/**
	 * 使用HQL，执行查询操作
	 */
	@SuppressWarnings("unchecked")
	private <T> List<T> query(CharSequence hql, Map<String, Object> map, Object[] params, int start, int max)
			throws Exception {
		Object idf = null;
		Session session = null;
		Transaction tx = null;
		try {
			idf = HibernateUtil.createSession();
			session = HibernateUtil.getSession();
			tx = session.beginTransaction();

			Query q = session.createQuery(hql.toString());
			if (!isBlank(map)) {
				q.setProperties(map);
			}
			if (params != null) {
				for (int i = 0, len = params.length; i < len; i++) {
					q.setParameter(i, params[i]);
				}
			}
			q.setFirstResult(start);
			if (max > 0) {
				q.setMaxResults(max);
			}
			List<T> list = q.list();

			session.flush();
			session.clear();
			tx.commit();
			return list;
		} catch (Exception e) {
			if (tx != null) {
				tx.rollback();
				session.clear();
			}

			throw error(".query()", e);
		} finally {
			if (idf != null && session != null) {
				closeQuickly(idf);
			}
		}
	}

	// ------------
	// TODO page
	// ------------
	public PageInfo page(CharSequence selectHQL, CharSequence countHQL, int page, int rows) throws Exception {
		return page(selectHQL, countHQL, null, null, page, rows);
	}

	public PageInfo page(CharSequence selectHQL, CharSequence countHQL, Map<String, Object> params, int page, int rows)
			throws Exception {
		return page(selectHQL, countHQL, params, null, page, rows);
	}

	public PageInfo page(CharSequence selectHQL, CharSequence countHQL, Object[] params, int page, int rows) throws Exception {
		return page(selectHQL, countHQL, null, params, page, rows);
	}

	/**
	 * 使用HQL语句，执行分页操作
	 */
	private PageInfo page(CharSequence selectHQL, CharSequence countHQL, Map<String, Object> map, Object[] params, int page,
			int rows) throws Exception {
		if (isBlank(selectHQL) || isBlank(countHQL)) {
			return null;
		}

		List<?> list = null;
		int count = (int) count(countHQL, map, params);
		if (count > 0) {
			page = Math.max(page, 1);
			int start = (page - 1) * rows;
			list = query(selectHQL, map, params, start, rows);
		}

		return PageInfo.build(list, count, page, rows);
	}

	// ------------
	// private
	// ------------
	/**
	 * 关闭Session
	 */
	private void closeQuickly(Object idf) {
		try {
			HibernateUtil.closeSession(idf);
		} catch (Exception e) {
			error(".closeQuickly()", e);
		}
	}

	private boolean isBlank(Collection<?> list) {
		return (list == null || list.isEmpty());
	}

	private boolean isBlank(Map<?, ?> map) {
		return (map == null || map.isEmpty());
	}

	private boolean isBlank(Object[] objs) {
		return (objs == null || objs.length == 0);
	}

	private boolean isBlank(CharSequence str) {
		if (str == null) {
			return true;
		}
		for (int i = 0, len = str.length(); i < len; i++) {
			if (str.charAt(i) > ' ') {
				return false;
			}
		}
		return true;
	}

	private Exception error(String msg) {
		String errmsg = HQLHelper.class.getName() + msg;
		LOG.error(errmsg);
		return new Exception(errmsg);
	}

	private Exception error(String msg, Throwable e) {
		String errmsg = HQLHelper.class.getName() + msg + ":" + e.getMessage();
		LOG.error(errmsg, e);
		return new Exception(errmsg, e);
	}

}
