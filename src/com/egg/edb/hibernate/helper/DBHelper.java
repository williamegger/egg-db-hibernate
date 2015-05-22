package com.egg.edb.hibernate.helper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.egg.edb.hibernate.HibernateUtil;
import com.egg.edb.hibernate.bean.Worker;
import com.egg.edb.hibernate.exception.BLException;

public class DBHelper {

	private static final Logger LOG = LoggerFactory.getLogger(DBHelper.class);

	private static final int OPT_SAVE = 0;
	private static final int OPT_UPDATE = 1;
	private static final int OPT_DELETE = 2;

	public static final HQLHelper hql = HQLHelper.getInstance();
	public static final SQLHelper sql = SQLHelper.getInstance();

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<Map> arrayToMap(List<Object[]> list, String... keys) {
		if (list == null || list.isEmpty()) {
			return null;
		}
		if (keys == null || keys.length == 0) {
			return null;
		}
		List<Map> maps = new ArrayList<Map>();
		Map map = null;
		int len = keys.length;
		for (Object[] objs : list) {
			map = new HashMap();
			for (int i = 0; i < len; i++) {
				map.put(keys[i].trim(), objs[i]);
			}
			maps.add(map);
		}
		return maps;
	}

	// --------------------------------
	// TODO Worker/save/delete/update
	// --------------------------------
	public static <Params, Result> Result doWorker(Worker<Params, Result> worker, Params... initParams)
			throws BLException, Exception {
		if (worker == null) {
			return null;
		}

		Object idf = null;
		Session session = null;
		Transaction tx = null;
		try {
			idf = HibernateUtil.createSession();
			session = HibernateUtil.getSession();
			tx = session.beginTransaction();

			Result res = worker.execute(session, initParams);

			session.flush();
			session.clear();
			tx.commit();
			return res;
		} catch (BLException e) {
			if (tx != null) {
				tx.rollback();
				session.clear();
			}

			throw e;
		} catch (Exception e) {
			if (tx != null) {
				tx.rollback();
				session.clear();
			}

			throw error(".doWorker()", e);
		} finally {
			if (idf != null && session != null) {
				closeQuickly(idf);
			}
		}
	}

	public static Serializable save(Object entity) throws Exception {
		return optEntity(entity, OPT_SAVE);
	}

	public static void update(Object entity) throws Exception {
		optEntity(entity, OPT_UPDATE);
	}

	public static void delete(Object entity) throws Exception {
		optEntity(entity, OPT_DELETE);
	}

	public static void batchSave(List<?> entities) throws Exception {
		optEntities(entities, OPT_SAVE);
	}

	public static void batchUpdate(List<?> entities) throws Exception {
		optEntities(entities, OPT_UPDATE);
	}

	public static void batchDelete(List<?> entities) throws Exception {
		optEntities(entities, OPT_DELETE);
	}

	// ------------
	// private
	// ------------
	/**
	 * 操作对象：添加、修改、删除
	 */
	private static Serializable optEntity(final Object entity, final int opt) throws Exception {
		if (entity == null) {
			return null;
		}

		Object idf = null;
		Session session = null;
		Transaction tx = null;
		Serializable id = null;
		try {
			idf = HibernateUtil.createSession();
			session = HibernateUtil.getSession();
			tx = session.beginTransaction();

			switch (opt) {
			case OPT_SAVE:
				id = session.save(entity);
				break;
			case OPT_UPDATE:
				session.update(entity);
				break;
			case OPT_DELETE:
				session.delete(entity);
				break;
			default:
				break;
			}

			session.flush();
			session.clear();
			tx.commit();
			return id;
		} catch (Exception e) {
			if (tx != null) {
				tx.rollback();
				session.clear();
			}

			throw error(".optEntity()", e);
		} finally {
			if (idf != null && session != null) {
				closeQuickly(idf);
			}
		}
	}

	/**
	 * 批量操作对象：添加、修改、删除
	 */
	private static void optEntities(final List<?> entities, final int opt) throws Exception {
		if (entities == null || entities.isEmpty()) {
			return;
		}

		Object idf = null;
		Session session = null;
		Transaction tx = null;
		try {
			idf = HibernateUtil.createSession();
			session = HibernateUtil.getSession();
			tx = session.beginTransaction();

			Object entity;
			for (int i = 0, len = entities.size(); i < len; i++) {
				entity = entities.get(i);
				if (entity == null) {
					continue;
				}

				switch (opt) {
				case OPT_SAVE:
					session.save(entity);
					break;
				case OPT_UPDATE:
					session.update(entity);
					break;
				case OPT_DELETE:
					session.delete(entity);
					break;
				default:
					break;
				}

				if ((i + 1) % 50 == 0) {
					session.flush();
					session.clear();
				}
			}

			session.flush();
			session.clear();
			tx.commit();
		} catch (Exception e) {
			if (tx != null) {
				tx.rollback();
				session.clear();
			}

			throw error(".optEntities()", e);
		} finally {
			if (idf != null && session != null) {
				closeQuickly(idf);
			}
		}
	}

	/**
	 * 关闭Session
	 */
	private static void closeQuickly(Object idf) {
		try {
			HibernateUtil.closeSession(idf);
		} catch (Exception e) {
			error(".closeQuickly()", e);
		}
	}

	private static Exception error(String msg, Throwable e) {
		String errmsg = DBHelper.class.getName() + msg + ":" + e.getMessage();
		LOG.error(errmsg, e);
		return new Exception(errmsg, e);
	}

}
