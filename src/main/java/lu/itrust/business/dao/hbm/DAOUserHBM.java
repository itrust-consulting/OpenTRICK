/**
 * 
 */
package lu.itrust.business.dao.hbm;

import java.util.List;

import lu.itrust.business.TS.usermanagement.Role;
import lu.itrust.business.TS.usermanagement.User;
import lu.itrust.business.dao.DAOUser;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

/**
 * @author oensuifudine
 * 
 */
@Repository
public class DAOUserHBM extends DAOHibernate implements DAOUser {

	/**
	 * 
	 */
	public DAOUserHBM() {
	}

	/**
	 * @param sessionFactory
	 */
	public DAOUserHBM(Session session) {
		super(session);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAOUser#get(long)
	 */
	@Override
	public User get(long id) throws Exception {
		return (User) getSession().get(User.class, id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAOUser#get(java.lang.String)
	 */
	@Override
	public User get(String login) throws Exception {
		return (User) getSession().createQuery("From User where login = :login").setString("login", login).uniqueResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAOUser#get(java.lang.String, java.lang.String)
	 */
	@Override
	public User get(String login, String password) throws Exception {
		return (User) getSession().createQuery("From User where login = :login and password = :password").setString("login", login).setString("password", password).uniqueResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAOUser#loadAll()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<User> loadAll() throws Exception {
		return getSession().createQuery("From User").list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAOUser#loadByName(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<User> loadByName(String name) throws Exception {
		return getSession().createQuery("From User where firstName = :name").setString("name", name).list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAOUser#loadByCountry(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<User> loadByCountry(String name) throws Exception {
		return getSession().createQuery("From User where country = :country").setString("country", name).list();
	}

	/**
	 * addRole: <br>
	 * Description
	 *
	 * @see lu.itrust.business.dao.DAOUser#addRole(lu.itrust.business.TS.usermanagement.Role)
	 */
	public boolean addRole(User user, Role role) throws Exception {
		boolean result = false;
		try {
			user.addRole(role);
			getSession().saveOrUpdate(user);
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result=false;
		}
		return result;
	}
	
	/**
	 * removeRole: <br>
	 * Description
	 *
	 * @see lu.itrust.business.dao.DAOUser#removeRole(lu.itrust.business.TS.usermanagement.Role)
	 */
	public boolean removeRole(User user, Role role) throws Exception {
		boolean result = false;
		try {
			user.removeRole(role);
			getSession().saveOrUpdate(user);
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result=false;
		}
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAOUser#save(lu.itrust.business.TS.User)
	 */
	@Override
	public void save(User user) throws Exception {
		getSession().save(user);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAOUser#saveOrUpdate(lu.itrust.business.TS.User)
	 */
	@Override
	public void saveOrUpdate(User user) throws Exception {
		getSession().saveOrUpdate(user);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAOUser#delete(lu.itrust.business.TS.User)
	 */
	@Override
	public void delete(User user) throws Exception {
		getSession().delete(user);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAOUser#delete(long)
	 */
	@Override
	public void delete(long id) throws Exception {
		delete(get(id));
	}

	@Override
	public boolean hasUsers() throws Exception {
		return ((Long) getSession().createQuery("Select count(*) From User").uniqueResult()).intValue() > 0;
	}

	/**
	 * hasRole: <br>
	 * Description
	 *
	 * @see lu.itrust.business.dao.DAOUser#hasRole(lu.itrust.business.TS.usermanagement.User, lu.itrust.business.TS.usermanagement.Role)
	 */
	@Override
	public boolean hasRole(User user, Role role) throws Exception {
		return user.hasRole(role.getType());
	}
}
