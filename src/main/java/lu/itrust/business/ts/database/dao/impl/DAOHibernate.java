package lu.itrust.business.ts.database.dao.impl;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * HibernateDAO.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.à r.l
 * @version
 * @since 11 janv. 2013
 */
public class DAOHibernate {

	/** The Session */
	private Session session;

	@Autowired
	private SessionFactory sessionFactory;

	/**
	 * Constructor: <br>
	 */
	public DAOHibernate() {
	}

	/**
	 * Constructor: <br>
	 * 
	 * @param session
	 */
	public DAOHibernate(Session session) {
		this.session = session;
	}

	/**
	 * getSession: <br>
	 * retrieve current session
	 * 
	 * @return
	 */
	public Session getSession() {
		return session == null ? sessionFactory.getCurrentSession() : session;
	}

	protected Query createQuery(String query) {
		return getSession().createQuery(query);
	}

	protected <T> Query<T> createQuery(String query, Class<T> resultClass) {
		return getSession().createQuery(query,resultClass);
	}

	protected Query createQueryWithCache(String query) {
		return getSession().createQuery(query).setHint("org.hibernate.cacheable", true);
	}

	protected <T> Query<T> createQueryWithCache(String query, Class<T> resultClass) {
		return getSession().createQuery(query,resultClass).setHint("org.hibernate.cacheable", true);
	}

	/**
	 * Initialise: <br>
	 * initialise given object
	 * 
	 * @param object
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T initialise(T object) {
		Hibernate.initialize(object);
		if (object instanceof HibernateProxy) {
			return (T) ((HibernateProxy) object).getHibernateLazyInitializer().getImplementation();
		}
		return object;
	}
}