package lu.itrust.business.dao.hbm;

import java.util.List;

import lu.itrust.business.TS.usermanagement.Role;
import lu.itrust.business.TS.usermanagement.User;
import lu.itrust.business.dao.DAORole;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

/**
 * DAORoleHBM.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.à.rl.
 * @version
 * @since Jan 12, 2013
 */
@Repository
public class DAORoleHBM extends DAOHibernate implements DAORole {

	/**
	 * Constructor: <br>
	 */
	public DAORoleHBM() {
	}

	/**
	 * Constructor: <br>
	 * 
	 * @param session
	 */
	public DAORoleHBM(Session session) {
		super(session);
	}

	/**
	 * get: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAORole#get(long)
	 */
	@Override
	public Role get(Integer id) throws Exception {
		return (Role) getSession().get(Role.class, id);
	}

	/**
	 * getRoleByName: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAORole#getRoleByName(java.lang.String)
	 */
	@Override
	public Role getByName(String name) throws Exception {
		return (Role) getSession().createQuery("FROM Role WHERE dtType=:RoleType").setString("RoleType", name).uniqueResult();
	}

	/**
	 * getAllRoles: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAORole#getAllRoles()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Role> getAll() throws Exception {
		return getSession().createQuery("From Role").list();
	}

	/**
	 * getFromUser: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAORole#getFromUser(java.lang.String)
	 */
	@Override
	public List<Role> getAllFromUser(String login) throws Exception {
		User aUser = (User) getSession().createQuery("From User where login = :user").setParameter("user", login).uniqueResult();
		List<Role> roles = aUser.getRoles();
		return roles;
	}

	/**
	 * getFromUser: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAORole#getFromUser(lu.itrust.business.TS.usermanagement.User)
	 */
	@Override
	public List<Role> getAllFromUser(User user) throws Exception {
		return user.getRoles();
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAORole#save(lu.itrust.business.TS.usermanagement.Role)
	 */
	@Override
	public void save(Role role) throws Exception {
		getSession().save(role);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAORole#saveOrUpdate(lu.itrust.business.TS.usermanagement.Role)
	 */
	@Override
	public void saveOrUpdate(Role role) throws Exception {
		getSession().saveOrUpdate(role);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAORole#delete(long)
	 */
	@Override
	public void delete(Integer id) throws Exception {
		getSession().delete(get(id));
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAORole#delete(java.lang.String)
	 */
	@Override
	public void delete(String login) throws Exception {
		for (Role role : getAllFromUser(login))
			delete(role);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAORole#delete(lu.itrust.business.TS.usermanagement.Role)
	 */
	@Override
	public void delete(Role role) throws Exception {
		getSession().delete(role);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAORole#delete(lu.itrust.business.TS.usermanagement.User)
	 */
	@Override
	public void delete(User user) throws Exception {
		for (Role role : getAllFromUser(user))
			delete(role);
	}
}