package com.egg.db.hibernate;

import java.io.File;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hibernate工具类
 */
public class HibernateUtil {
	private static String current_dir = HibernateUtil.class.getClassLoader().getResource("").getPath() + "hibernate.cfg.xml";
	private static Logger LOG = LoggerFactory.getLogger(HibernateUtil.class);

	private static SessionFactory sessionFactory;
	private static final ThreadLocal<Session> currentSession = new ThreadLocal<Session>();
	private static final Owner trueOwner = new Owner(true);
	private static final Owner fakeOwner = new Owner(false);
	private static final boolean isLife = false;

	public static Object createSession() throws Exception {
		Session session = (Session) currentSession.get();
		if (session == null) {
			session = getSessionFactory().openSession();
			currentSession.set(session);
			return trueOwner;
		}
		return fakeOwner;
	}

	public synchronized static void closeSession(Object ownership) throws Exception {
		if (((Owner) ownership).identity) {
			Session session = (Session) currentSession.get();
			session.flush();
			session.close();
			currentSession.set(null);
		}
	}

	public synchronized static Session getSession() throws HibernateException {
		return (Session) currentSession.get();
	}

	private synchronized static SessionFactory getSessionFactory() {
		try {
			if (sessionFactory == null) {
				if (isLife) {
					Configuration cc = new Configuration().configure();
					sessionFactory = cc.configure(new File(current_dir)).buildSessionFactory();
				} else {
					Configuration cc = new Configuration();
					sessionFactory = cc.configure().buildSessionFactory();
				}
			}
			if (sessionFactory == null) {
				LOG.error("Failed to buildSessionFactory, sessionFactory is null.");
			}
			return sessionFactory;
		} catch (HibernateException e) {
			LOG.error("getSessionFactory", e);
		} catch (Exception err) {
			LOG.error("getSessionFactory", err);
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