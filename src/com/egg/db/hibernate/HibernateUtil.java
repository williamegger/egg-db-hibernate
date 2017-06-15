package com.egg.db.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hibernate工具类
 */
public class HibernateUtil {
	// private static String current_dir = HibernateUtil.class.getClassLoader().getResource("").getPath() + "hibernate.cfg.xml";
	private static Logger LOG = LoggerFactory.getLogger(HibernateUtil.class);

	private static SessionFactory sessionFactory;
	private static final ThreadLocal<Session> currentSession = new ThreadLocal<Session>();
	private static final Owner trueOwner = new Owner(true);
	private static final Owner fakeOwner = new Owner(false);

	public static Object createSession() throws Exception {
		Session session = (Session) currentSession.get();
		if (session == null) {
			session = getSessionFactory().openSession();
			currentSession.set(session);
			return trueOwner;
		}
		return fakeOwner;
	}

	public synchronized static void closeSession(Object ownership) {
		if (ownership != null && ((Owner) ownership).identity) {
			Session session = (Session) currentSession.get();
			if (session != null) {
				try {
					session.close();
				} catch (Exception e) {
				}
			}
			currentSession.set(null);
		}
	}

	public synchronized static Session getSession() {
		return (Session) currentSession.get();
	}

	private synchronized static SessionFactory getSessionFactory() {
		try {
			if (sessionFactory == null) {
				sessionFactory = new Configuration().configure().buildSessionFactory();
			}
			return sessionFactory;
		} catch (Exception e) {
			LOG.error("getSessionFactory", e);
		}
		return null;
	}

	private static class Owner {
		public Owner(boolean identity) {
			this.identity = identity;
		}

		boolean identity = false;
	}
}
