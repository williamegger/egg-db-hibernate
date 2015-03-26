package com.egg.edb.hibernate.helper;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.egg.edb.hibernate.HibernateUtil;
import com.egg.edb.hibernate.bean.PageInfo;
import com.egg.edb.hibernate.bean.SqlEntity;
import com.egg.edb.hibernate.bean.SqlScalar;

public class SQLHelper {

	private static final Logger LOG = LoggerFactory.getLogger(SQLHelper.class);
	private static SQLHelper instance = null;

	static synchronized SQLHelper getInstance() {
		if (instance == null) {
			instance = new SQLHelper();
		}
		return instance;
	}

	private SQLHelper() {
	}

	// ---------------------
	// TODO executeUpdate
	// ---------------------
	public int execute(CharSequence sql) throws Exception {
		return execute(sql, null, null);
	}

	public int execute(CharSequence sql, Map<String, Object> params) throws Exception {
		return execute(sql, params, null);
	}

	public int execute(CharSequence sql, Object[] params) throws Exception {
		return execute(sql, null, params);
	}

	public int update(String tableName, String[] sets, String[] wheres, Map<String, Object> params) throws Exception {
		if (isBlank(tableName) || isBlank(sets)) {
			throw error(".update() : String tableName 和 String[] wheres 不能为空");
		}

		String sql = buildSQLForUpdate(tableName, sets, wheres);
		return execute(sql, params, null);
	}

	public int update(String tableName, String[] sets, String[] wheres, Object[] params) throws Exception {
		if (isBlank(tableName) || isBlank(sets)) {
			throw error(".update() : String tableName 和 String[] wheres 不能为空");
		}

		String sql = buildSQLForUpdate(tableName, sets, wheres);
		return execute(sql, null, params);
	}

	public int delete(String tableName, String[] wheres, Map<String, Object> params) throws Exception {
		if (isBlank(tableName)) {
			throw error(".update() : String tableName 不能为空");
		}

		String sql = buildSQLForDelete(tableName, wheres);
		return execute(sql, params, null);
	}

	public int delete(String tableName, String[] wheres, Object[] params) throws Exception {
		if (isBlank(tableName)) {
			throw error(".update() : String tableName 不能为空");
		}

		String sql = buildSQLForDelete(tableName, wheres);
		return execute(sql, null, params);
	}

	/**
	 * 构建update操作的SQL语句
	 */
	private String buildSQLForUpdate(String tableName, String[] sets, String[] wheres) {
		StringBuffer sql = new StringBuffer();
		sql.append("update ").append(tableName);

		sql.append(" set ");
		for (int i = 0, len = sets.length; i < len; i++) {
			if (i > 0) {
				sql.append(", ");
			}
			sql.append(sets[i]);
		}

		if (!isBlank(wheres)) {
			sql.append(" where ");
			for (int i = 0, len = wheres.length; i < len; i++) {
				if (i > 0) {
					sql.append(" and ");
				}
				sql.append(wheres[i]);
			}
		}

		return sql.toString();
	}

	/**
	 * 构建delete操作的SQL语句
	 */
	private String buildSQLForDelete(String tableName, String[] wheres) {
		StringBuffer sql = new StringBuffer();
		sql.append("delete from ").append(tableName);

		if (!isBlank(wheres)) {
			sql.append(" where ");
			for (int i = 0, len = wheres.length; i < len; i++) {
				if (i > 0) {
					sql.append(" and ");
				}
				sql.append(wheres[i]);
			}
		}

		return sql.toString();
	}

	/**
	 * 使用SQL语句，执行executeUpdate操作
	 */
	private int execute(CharSequence sql, Map<String, Object> map, Object[] params) throws Exception {
		Object idf = null;
		Session session = null;
		Transaction tx = null;
		try {
			idf = HibernateUtil.createSession();
			session = HibernateUtil.getSession();
			tx = session.beginTransaction();

			SQLQuery sq = session.createSQLQuery(sql.toString());
			if (!isBlank(map)) {
				sq.setProperties(map);
			}
			if (params != null) {
				for (int i = 0, len = params.length; i < len; i++) {
					sq.setParameter(i, params[i]);
				}
			}
			int rows = sq.executeUpdate();

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
	public <T> T one(CharSequence sql) throws Exception {
		return one(sql, null, null, null, null);
	}

	public <T> T one(CharSequence sql, Map<String, Object> params) throws Exception {
		return one(sql, null, null, params, null);
	}

	public <T> T one(CharSequence sql, Object[] params) throws Exception {
		return one(sql, null, null, null, params);
	}

	public <T> T one(CharSequence sql, SqlScalar scalar) throws Exception {
		return one(sql, scalar, null, null, null);
	}

	public <T> T one(CharSequence sql, SqlScalar scalar, Map<String, Object> params) throws Exception {
		return one(sql, scalar, null, params, null);
	}

	public <T> T one(CharSequence sql, SqlScalar scalar, Object[] params) throws Exception {
		return one(sql, scalar, null, null, params);
	}

	public <T> T one(CharSequence sql, SqlEntity entity) throws Exception {
		return one(sql, null, entity, null, null);
	}

	public <T> T one(CharSequence sql, SqlEntity entity, Map<String, Object> params) throws Exception {
		return one(sql, null, entity, params, null);
	}

	public <T> T one(CharSequence sql, SqlEntity entity, Object[] params) throws Exception {
		return one(sql, null, entity, null, params);
	}

	/**
	 * 使用SQL语句，得到一条记录
	 */
	private <T> T one(CharSequence sql, SqlScalar scalar, SqlEntity entity, Map<String, Object> map, Object[] params)
			throws Exception {
		List<SqlScalar> scalars = null;
		if (scalar != null) {
			scalars = new ArrayList<SqlScalar>();
			scalars.add(scalar);
		}

		List<T> list = query(sql, scalars, entity, map, params, 0, 0, false);
		return (isBlank(list) ? null : list.get(0));
	}

	public long count(CharSequence sql) throws Exception {
		return count(sql, null, null);
	}

	public long count(CharSequence sql, Map<String, Object> params) throws Exception {
		return count(sql, params, null);
	}

	public long count(CharSequence sql, Object[] params) throws Exception {
		return count(sql, null, params);
	}

	/**
	 * 使用SQL语句，得到一条记录
	 */
	private long count(CharSequence sql, Map<String, Object> map, Object[] params) throws Exception {
		BigInteger count = one(sql, null, null, map, params);
		return (count == null ? 0 : count.longValue());
	}

	public boolean exist(CharSequence sql) throws Exception {
		long count = count(sql);
		return count > 0;
	}

	public boolean exist(CharSequence sql, Map<String, Object> params) throws Exception {
		long count = count(sql, params);
		return count > 0;
	}

	public boolean exist(CharSequence sql, Object[] params) throws Exception {
		long count = count(sql, params);
		return count > 0;
	}

	// ------------
	// TODO query
	// ------------
	public <T> List<T> query(CharSequence sql, List<SqlScalar> scalars, boolean toMap) throws Exception {
		return query(sql, scalars, null, null, null, 0, 0, toMap);
	}

	public <T> List<T> query(CharSequence sql, List<SqlScalar> scalars, Map<String, Object> params, boolean toMap)
			throws Exception {
		return query(sql, scalars, null, params, null, 0, 0, toMap);
	}

	public <T> List<T> query(CharSequence sql, List<SqlScalar> scalars, Object[] params, boolean toMap)
			throws Exception {
		return query(sql, scalars, null, null, params, 0, 0, toMap);
	}

	public <T> List<T> query(CharSequence sql, List<SqlScalar> scalars, int max, boolean toMap) throws Exception {
		return query(sql, scalars, null, null, null, 0, max, toMap);
	}

	public <T> List<T> query(CharSequence sql, List<SqlScalar> scalars, Map<String, Object> params, int max,
			boolean toMap) throws Exception {
		return query(sql, scalars, null, params, null, 0, max, toMap);
	}

	public <T> List<T> query(CharSequence sql, List<SqlScalar> scalars, Object[] params, int max, boolean toMap)
			throws Exception {
		return query(sql, scalars, null, null, params, 0, max, toMap);
	}

	public <T> List<T> query(CharSequence sql, List<SqlScalar> scalars, int start, int max, boolean toMap)
			throws Exception {
		return query(sql, scalars, null, null, null, start, max, toMap);
	}

	public <T> List<T> query(CharSequence sql, List<SqlScalar> scalars, Map<String, Object> params, int start, int max,
			boolean toMap) throws Exception {
		return query(sql, scalars, null, params, null, start, max, toMap);
	}

	public <T> List<T> query(CharSequence sql, List<SqlScalar> scalars, Object[] params, int start, int max,
			boolean toMap) throws Exception {
		return query(sql, scalars, null, null, params, start, max, toMap);
	}

	public <T> List<T> query(CharSequence sql, SqlEntity entity) throws Exception {
		return query(sql, null, entity, null, null, 0, 0, false);
	}

	public <T> List<T> query(CharSequence sql, SqlEntity entity, Map<String, Object> params) throws Exception {
		return query(sql, null, entity, params, null, 0, 0, false);
	}

	public <T> List<T> query(CharSequence sql, SqlEntity entity, Object[] params) throws Exception {
		return query(sql, null, entity, null, params, 0, 0, false);
	}

	public <T> List<T> query(CharSequence sql, SqlEntity entity, int max) throws Exception {
		return query(sql, null, entity, null, null, 0, max, false);
	}

	public <T> List<T> query(CharSequence sql, SqlEntity entity, Map<String, Object> params, int max) throws Exception {
		return query(sql, null, entity, params, null, 0, max, false);
	}

	public <T> List<T> query(CharSequence sql, SqlEntity entity, Object[] params, int max) throws Exception {
		return query(sql, null, entity, null, params, 0, max, false);
	}

	public <T> List<T> query(CharSequence sql, SqlEntity entity, int start, int max) throws Exception {
		return query(sql, null, entity, null, null, start, max, false);
	}

	public <T> List<T> query(CharSequence sql, SqlEntity entity, Map<String, Object> params, int start, int max)
			throws Exception {
		return query(sql, null, entity, params, null, start, max, false);
	}

	public <T> List<T> query(CharSequence sql, SqlEntity entity, Object[] params, int start, int max) throws Exception {
		return query(sql, null, entity, null, params, start, max, false);
	}

	/**
	 * 使用SQL，执行查询操作
	 */
	@SuppressWarnings("unchecked")
	private <T> List<T> query(CharSequence sql, List<SqlScalar> scalars, SqlEntity entity, Map<String, Object> map,
			Object[] params, int start, int max, boolean toMap) throws Exception {
		Object idf = null;
		Session session = null;
		Transaction tx = null;
		try {
			idf = HibernateUtil.createSession();
			session = HibernateUtil.getSession();
			tx = session.beginTransaction();

			SQLQuery sq = session.createSQLQuery(sql.toString());
			// List<SqlScalar>
			if (!isBlank(scalars)) {
				SqlScalar scalar;
				for (int i = 0, len = scalars.size(); i < len; i++) {
					scalar = scalars.get(i);
					sq.addScalar(scalar.getName(), scalar.getType());
				}
			}
			// SqlEntity
			if (entity != null) {
				sq.addEntity(entity.getName(), entity.getEntityClass());
			}
			// Parameters - Map
			if (!isBlank(map)) {
				sq.setProperties(map);
			}
			// Parameters - Object[]
			if (params != null) {
				for (int i = 0, len = params.length; i < len; i++) {
					sq.setParameter(i, params[i]);
				}
			}
			// start
			sq.setFirstResult(start);
			// max
			if (max > 0) {
				sq.setMaxResults(max);
			}
			// toMap
			if (toMap) {
				sq.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			}
			List<T> list = sq.list();

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
	public PageInfo page(CharSequence selectSQL, CharSequence countSQL, List<SqlScalar> scalars, int page, int rows,
			boolean toMap) throws Exception {
		return page(selectSQL, countSQL, scalars, null, null, null, page, rows, toMap);
	}

	public PageInfo page(CharSequence selectSQL, CharSequence countSQL, List<SqlScalar> scalars, Map<String, Object> params,
			int page, int rows, boolean toMap) throws Exception {
		return page(selectSQL, countSQL, scalars, null, params, null, page, rows, toMap);
	}

	public PageInfo page(CharSequence selectSQL, CharSequence countSQL, List<SqlScalar> scalars, Object[] params, int page,
			int rows, boolean toMap) throws Exception {
		return page(selectSQL, countSQL, scalars, null, null, params, page, rows, toMap);
	}

	public PageInfo page(CharSequence selectSQL, CharSequence countSQL, SqlEntity entity, int page, int rows) throws Exception {
		return page(selectSQL, countSQL, null, entity, null, null, page, rows, false);
	}

	public PageInfo page(CharSequence selectSQL, CharSequence countSQL, SqlEntity entity, Map<String, Object> params,
			int page, int rows) throws Exception {
		return page(selectSQL, countSQL, null, entity, params, null, page, rows, false);
	}

	public PageInfo page(CharSequence selectSQL, CharSequence countSQL, SqlEntity entity, Object[] params, int page, int rows)
			throws Exception {
		return page(selectSQL, countSQL, null, entity, null, params, page, rows, false);
	}

	/**
	 * 使用SQL语句，执行分页操作
	 */
	private PageInfo page(CharSequence selectSQL, CharSequence countSQL, List<SqlScalar> scalars, SqlEntity entity,
			Map<String, Object> map, Object[] params, int page, int rows, boolean toMap) throws Exception {
		if (isBlank(selectSQL) || isBlank(countSQL)) {
			return null;
		}

		List<?> list = null;
		int count = (int) count(countSQL, map, params);
		if (count > 0) {
			page = Math.max(page, 1);
			int start = (page - 1) * rows;
			list = query(selectSQL, scalars, entity, map, params, start, rows, toMap);
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
		String errmsg = SQLHelper.class.getName() + msg;
		LOG.error(errmsg);
		return new Exception(errmsg);
	}

	private Exception error(String msg, Throwable e) {
		String errmsg = SQLHelper.class.getName() + msg + ":" + e.getMessage();
		LOG.error(errmsg, e);
		return new Exception(errmsg, e);
	}

}
