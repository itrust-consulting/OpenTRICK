/**
 * 
 */
package lu.itrust.business.dao.hbm;

import java.util.List;

import lu.itrust.business.TS.Role;
import lu.itrust.business.TS.User;
import lu.itrust.business.dao.DAORole;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

/**
 * @author oensuifudine
 * 
 */
@Repository
public class DAORoleHBM extends DAOHibernate implements DAORole {
	
	/**
	 * 
	 */
	public DAORoleHBM() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param sessionFactory
	 */
	public DAORoleHBM(Session session) {
		super(session);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAORole#get(long)
	 */
	@Override
	public Role get(long id) throws Exception {
		return (Role) getSession().get(Role.class, id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAORole#get(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Role> load(String login) throws Exception {
		return (List<Role>) getSession()
				.createQuery("From Role where user.login = :login")
				.setString("login", login).list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAORole#get(lu.itrust.business.TS.User)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Role> load(User user) throws Exception {
		return (List<Role>) getSession()
				.createQuery("From Role where user = :user")
				.setParameter("user", user).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Role> load(String login, String password) throws Exception {
		return (List<Role>) getSession()
				.createQuery(
						"From Role where user.login = :login and user.password = :password")
				.setParameter("login", login).setString("password", password)
				.list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAORole#loadAll()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Role> loadAll() throws Exception {
		return getSession().createQuery("From Role").list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAORole#save(lu.itrust.business.TS.Role)
	 */
	@Override
	public void save(Role role) throws Exception {
		getSession().save(role);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.dao.DAORole#saveOrUpdate(lu.itrust.business.TS.Role)
	 */
	@Override
	public void saveOrUpdate(Role role) throws Exception {
		getSession().saveOrUpdate(role);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAORole#delete(lu.itrust.business.TS.Role)
	 */
	@Override
	public void delete(Role role) throws Exception {
		getSession().delete(role);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAORole#delete(long)
	 */
	@Override
	public void delete(long id) throws Exception {
		getSession().delete(get(id));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAORole#delete(lu.itrust.business.TS.User)
	 */
	@Override
	public void delete(User user) throws Exception {
		
		for (Role role : load(user))
			delete(role);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAORole#delete(java.lang.String)
	 */
	@Override
	public void delete(String login) throws Exception {
		
		for (Role role : load(login))
			delete(role);
	}

	@Override
	public Role findByName(String name) throws Exception {
		// TODO Auto-generated method stub
		return (Role) getSession().createQuery("FROM Role WHERE dtType=:RoleType").setString("RoleType", name).uniqueResult();
		
	}

}
