/**
 * 
 */
package lu.itrust.business.dao.hbm;

import java.util.List;

import lu.itrust.business.TS.usermanagment.Role;
import lu.itrust.business.TS.usermanagment.User;
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
	@Override
	public List<Role> getFromUser(String login) throws Exception {
		User aUser = (User) getSession().createQuery("From User where login = :user").setParameter("user", login).uniqueResult();
		List<Role> roles = aUser.getRoles();
		
		return roles;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAORole#get(lu.itrust.business.TS.User)
	 */
	@Override
	public List<Role> getFromUser(User user) throws Exception {
		
		User aUser = (User) getSession().createQuery("From User where id = :user").setParameter("user", user.getId()).uniqueResult();
		
		List<Role> roles = aUser.getRoles();
				
		return roles;
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
	 * @see lu.itrust.business.dao.DAORole#saveOrUpdate(lu.itrust.business.TS.Role)
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

		for (Role role : getFromUser(user))
			delete(role);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAORole#delete(java.lang.String)
	 */
	@Override
	public void delete(String login) throws Exception {

		for (Role role : getFromUser(login))
			delete(role);
	}

	@Override
	public Role findByName(String name) throws Exception {
		// TODO Auto-generated method stub
		return (Role) getSession().createQuery("FROM Role WHERE dtType=:RoleType").setString("RoleType", name).uniqueResult();

	}

}
