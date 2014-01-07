package lu.itrust.business.dao.hbm;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * HibernateDAO.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.�.rl. :
 * @version
 * @since 11 janv. 2013
 */
@Repository
public class DAOHibernate {

	public DAOHibernate() {
	}

	public DAOHibernate(Session session) {
		this.session = session;
	}

	private Session session;

	@Autowired
	private SessionFactory sessionFactory;

	/**
	 * getSession()<br>
	 * retrieves current session
	 * 
	 * @return current session
	 */
	public Session getSession() {
		return session == null ? sessionFactory.getCurrentSession() : session;
	}

	@SuppressWarnings("unchecked")
	public static <T> T Initialise(T object) {
		Hibernate.initialize(object);
		if (object instanceof HibernateProxy) {
			return (T) ((HibernateProxy) object).getHibernateLazyInitializer()
					.getImplementation();
		}

		return object;

	}
}
