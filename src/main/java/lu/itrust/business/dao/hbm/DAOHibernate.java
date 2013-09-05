package lu.itrust.business.dao.hbm;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * HibernateDAO.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.à.rl. :
 * @version
 * @since 11 janv. 2013
 */
@Repository
public class DAOHibernate {

	@Autowired
	private SessionFactory sessionFactory;
	
	
	/**
	 * setSessionFactory<br>
	 * Session manager<br>
	 * it uses by spring
	 * @param sessionFactory
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	/**
	 * getSession()<br>
	 * retrieves current session
	 * @return current session
	 */
	public Session getSession() {
		return sessionFactory.getCurrentSession();
	}

}
