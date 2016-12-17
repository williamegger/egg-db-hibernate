package com.egg.db.hibernate.helper;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.jdbc.Work;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.egg.db.hibernate.HibernateUtil;
import com.egg.db.hibernate.bean.PageInfo;
import com.egg.db.hibernate.bean.SqlType;

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
			throw error(".update() : String tableName 和 String[] sets 不能为空");
		}

		String sql = buildSQLForUpdate(tableName, sets, wheres);
		return execute(sql, params, null);
	}

	public int update(String tableName, String[] sets, String[] wheres, Object[] params) throws Exception {
		if (isBlank(tableName) || isBlank(sets)) {
			throw error(".update() : String tableName 和 String[] sets 不能为空");
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
	public <T> T one(CharSequence sql, boolean toMap) throws Exception {
		return one(sql, null, null, null, toMap);
	}

	public <T> T one(CharSequence sql, Map<String, Object> params, boolean toMap) throws Exception {
		return one(sql, null, params, null, toMap);
	}

	public <T> T one(CharSequence sql, Object[] params, boolean toMap) throws Exception {
		return one(sql, null, null, params, toMap);
	}

	public <T> T one(CharSequence sql, List<SqlType> types, boolean toMap) throws Exception {
		return one(sql, types, null, null, toMap);
	}

	public <T> T one(CharSequence sql, List<SqlType> types, Map<String, Object> params, boolean toMap) throws Exception {
		return one(sql, types, params, null, toMap);
	}

	public <T> T one(CharSequence sql, List<SqlType> types, Object[] params, boolean toMap) throws Exception {
		return one(sql, types, null, params, toMap);
	}

	/**
	 * 使用SQL语句，得到一条记录
	 */
	private <T> T one(CharSequence sql, List<SqlType> types, Map<String, Object> map, Object[] params, boolean toMap)
			throws Exception {

		List<T> list = query(sql, types, map, params, 0, 1, toMap);
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
	 * 使用SQL语句，得到count
	 */
	private long count(CharSequence sql, Map<String, Object> map, Object[] params) throws Exception {
		BigInteger count = one(sql, null, map, params, false);
		return (count == null ? 0 : count.longValue());
	}

	public boolean exist(CharSequence sql) throws Exception {
		return (count(sql) > 0);
	}

	public boolean exist(CharSequence sql, Map<String, Object> params) throws Exception {
		return (count(sql, params) > 0);
	}

	public boolean exist(CharSequence sql, Object[] params) throws Exception {
		return (count(sql, params) > 0);
	}

	// ------------
	// TODO query
	// ------------
	public <T> List<T> query(CharSequence sql, List<SqlType> types, boolean toMap) throws Exception {
		return query(sql, types, null, null, 0, 0, toMap);
	}

	public <T> List<T> query(CharSequence sql, List<SqlType> types, Map<String, Object> params, boolean toMap)
			throws Exception {
		return query(sql, types, params, null, 0, 0, toMap);
	}

	public <T> List<T> query(CharSequence sql, List<SqlType> types, Object[] params, boolean toMap) throws Exception {
		return query(sql, types, null, params, 0, 0, toMap);
	}

	public <T> List<T> query(CharSequence sql, List<SqlType> types, int max, boolean toMap) throws Exception {
		return query(sql, types, null, null, 0, max, toMap);
	}

	public <T> List<T> query(CharSequence sql, List<SqlType> types, Map<String, Object> params, int max, boolean toMap)
			throws Exception {
		return query(sql, types, params, null, 0, max, toMap);
	}

	public <T> List<T> query(CharSequence sql, List<SqlType> types, Object[] params, int max, boolean toMap)
			throws Exception {
		return query(sql, types, null, params, 0, max, toMap);
	}

	public <T> List<T> query(CharSequence sql, List<SqlType> types, int start, int max, boolean toMap) throws Exception {
		return query(sql, types, null, null, start, max, toMap);
	}

	public <T> List<T> query(CharSequence sql, List<SqlType> types, Map<String, Object> params, int start, int max,
			boolean toMap) throws Exception {
		return query(sql, types, params, null, start, max, toMap);
	}

	public <T> List<T> query(CharSequence sql, List<SqlType> types, Object[] params, int start, int max, boolean toMap)
			throws Exception {
		return query(sql, types, null, params, start, max, toMap);
	}

	/**
	 * 使用SQL，执行查询操作
	 */
	@SuppressWarnings("unchecked")
	private <T> List<T> query(CharSequence sql, List<SqlType> types, Map<String, Object> map, Object[] params,
			int start, int max, boolean toMap) throws Exception {
		Object idf = null;
		Session session = null;
		Transaction tx = null;
		try {
			idf = HibernateUtil.createSession();
			session = HibernateUtil.getSession();
			tx = session.beginTransaction();

			SQLQuery sq = session.createSQLQuery(sql.toString());
			// List<SqlType>
			if (!isBlank(types)) {
				for (SqlType type : types) {
					if (type.getScalar() != null) {
						sq.addScalar(type.getName(), type.getScalar());
					} else if (type.getEntityClass() != null) {
						sq.addEntity(type.getName(), type.getEntityClass());
					}
				}
			}
			// Parameters - Map
			if (!isBlank(map)) {
				sq.setProperties(map);
			}
			// Parameters - Object[]
			if (!isBlank(params)) {
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
	
	/**
     * 使用JDBC查询SQL语句
     */
    public List<Map<String, Object>> queryByJDBC(final CharSequence sql, final Object... params) throws Exception {
        Object idf = null;
        Session session = null;
        try {
            idf = HibernateUtil.createSession();
            session = HibernateUtil.getSession();

            final List<Map<String, Object>> resList = new ArrayList<Map<String, Object>>();

            session.doWork(new Work() {
                @Override
                public void execute(Connection connection) throws SQLException {
                    PreparedStatement ps = null;
                    ResultSet res = null;

                    try {
                        ps = connection.prepareStatement(sql.toString());
                        if (params != null) {
                            for (int i = 0, len = params.length; i < len; i++) {
                                ps.setObject(i + 1, params[i]);
                            }
                        }

                        res = ps.executeQuery();

                        if (res != null) {
                            ResultSetMetaData md = res.getMetaData();
                            Map<String, Object> resMap;
                            String lbl;
                            while (res.next()) {
                                resMap = new HashMap<String, Object>();

                                for (int i = 1, len = md.getColumnCount(); i <= len; i++) {
                                    lbl = md.getColumnLabel(i);
                                    if (StringUtils.isNotBlank(lbl)) {
                                        lbl = md.getColumnName(i);
                                    }
                                    resMap.put(lbl, res.getObject(i));
                                }

                                resList.add(resMap);
                            }
                        }
                    } finally {
                        if (res != null) {
                            res.close();
                        }
                        if (ps != null) {
                            ps.close();
                        }
                    }
                }
            });
            return resList;
        } catch (Exception e) {
            throw error(".queryByJDBC()", e);
        } finally {
            if (idf != null && session != null) {
                closeQuickly(idf);
            }
        }
    }

	// ------------
	// TODO page
	// ------------
	public PageInfo page(CharSequence selectSQL, CharSequence countSQL, List<SqlType> types, int page, int rows,
			boolean toMap) throws Exception {
		return page(selectSQL, countSQL, types, null, null, page, rows, toMap);
	}

	public PageInfo page(CharSequence selectSQL, CharSequence countSQL, List<SqlType> types,
			Map<String, Object> params, int page, int rows, boolean toMap) throws Exception {
		return page(selectSQL, countSQL, types, params, null, page, rows, toMap);
	}

	public PageInfo page(CharSequence selectSQL, CharSequence countSQL, List<SqlType> types, Object[] params, int page,
			int rows, boolean toMap) throws Exception {
		return page(selectSQL, countSQL, types, null, params, page, rows, toMap);
	}

	/**
	 * 使用SQL语句，执行分页操作
	 */
	private PageInfo page(CharSequence selectSQL, CharSequence countSQL, List<SqlType> types, Map<String, Object> map,
			Object[] params, int page, int rows, boolean toMap) throws Exception {
		if (isBlank(selectSQL) || isBlank(countSQL)) {
			return null;
		}

		List<?> list = null;
		int count = (int) count(countSQL, map, params);
		if (count > 0) {
			page = Math.max(page, 1);
			int start = (page - 1) * rows;
			list = query(selectSQL, types, map, params, start, rows, toMap);
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
