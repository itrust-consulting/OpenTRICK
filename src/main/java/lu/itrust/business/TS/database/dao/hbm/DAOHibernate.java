package lu.itrust.business.TS.database.dao.hbm;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * HibernateDAO.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.à.rl. :
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