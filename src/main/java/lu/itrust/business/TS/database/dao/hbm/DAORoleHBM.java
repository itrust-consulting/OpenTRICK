package lu.itrust.business.TS.database.dao.hbm;

import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.TS.database.dao.DAORole;
import lu.itrust.business.TS.usermanagement.Role;
import lu.itrust.business.TS.usermanagement.RoleType;
import lu.itrust.business.TS.usermanagement.User;

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
	 * @see lu.itrust.business.TS.database.dao.DAORole#get(long)
	 */
	@Override
	public Role get(Integer id)  {
		return (Role) getSession().get(Role.class, id);
	}

	/**
	 * getRoleByName: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAORole#getRoleByName(java.lang.String)
	 */
	@Override
	public Role getByName(String name)  {
		return getByType(RoleType.valueOf(name));
	}

	/**
	 * getAllRoles: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAORole#getAllRoles()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Role> getAll()  {
		return getSession().createQuery("From Role").getResultList();
	}

	/**
	 * getFromUser: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAORole#getFromUser(java.lang.String)
	 */
	@Override
	public List<Role> getAllFromUser(String login)  {
		User aUser = (User) getSession().createQuery("From User where login = :user").setParameter("user", login).getSingleResult();
		List<Role> roles = aUser.getRoles();
		return roles;
	}

	/**
	 * getFromUser: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAORole#getFromUser(lu.itrust.business.TS.usermanagement.User)
	 */
	@Override
	public List<Role> getAllFromUser(User user)  {
		return user.getRoles();
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAORole#save(lu.itrust.business.TS.usermanagement.Role)
	 */
	@Override
	public void save(Role role)  {
		getSession().save(role);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAORole#saveOrUpdate(lu.itrust.business.TS.usermanagement.Role)
	 */
	@Override
	public void saveOrUpdate(Role role)  {
		getSession().saveOrUpdate(role);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAORole#delete(long)
	 */
	@Override
	public void delete(Integer id)  {
		getSession().delete(get(id));
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAORole#delete(java.lang.String)
	 */
	@Override
	public void delete(String login)  {
		for (Role role : getAllFromUser(login))
			delete(role);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAORole#delete(lu.itrust.business.TS.usermanagement.Role)
	 */
	@Override
	public void delete(Role role)  {
		getSession().delete(role);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAORole#delete(lu.itrust.business.TS.usermanagement.User)
	 */
	@Override
	public void delete(User user)  {
		for (Role role : getAllFromUser(user))
			delete(role);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Role getByType(RoleType type) {
		return (Role) getSession().createQuery("FROM Role role WHERE role.type = :type").setParameter("type",type).uniqueResultOptional().orElse(null);
	}
}