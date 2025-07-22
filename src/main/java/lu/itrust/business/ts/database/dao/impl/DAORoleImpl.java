package lu.itrust.business.ts.database.dao.impl;

import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.ts.database.dao.DAORole;
import lu.itrust.business.ts.usermanagement.Role;
import lu.itrust.business.ts.usermanagement.RoleType;
import lu.itrust.business.ts.usermanagement.User;

/**
 * DAORoleImpl.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.à.rl.
 * @version
 * @since Jan 12, 2013
 */
@Repository
public class DAORoleImpl extends DAOHibernate implements DAORole {

	/**
	 * Constructor: <br>
	 */
	public DAORoleImpl() {
	}

	/**
	 * Constructor: <br>
	 * 
	 * @param session
	 */
	public DAORoleImpl(Session session) {
		super(session);
	}

	/**
	 * get: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAORole#get(long)
	 */
	@Override
	public Role get(Integer id)  {
		return (Role) getSession().get(Role.class, id);
	}

	/**
	 * getRoleByName: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAORole#getRoleByName(java.lang.String)
	 */
	@Override
	public Role getByName(String name)  {
		return getByType(RoleType.valueOf(name));
	}

	/**
	 * getAllRoles: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAORole#getAllRoles()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Role> getAll()  {
		return createQueryWithCache("From Role").getResultList();
	}

	/**
	 * getFromUser: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAORole#getFromUser(java.lang.String)
	 */
	@Override
	public List<Role> getAllFromUser(String login)  {
		User aUser = (User) createQueryWithCache("From User where login = :user").setParameter("user", login).getSingleResult();
		List<Role> roles = aUser.getRoles();
		return roles;
	}

	/**
	 * getFromUser: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAORole#getFromUser(lu.itrust.business.ts.usermanagement.User)
	 */
	@Override
	public List<Role> getAllFromUser(User user)  {
		return user.getRoles();
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAORole#save(lu.itrust.business.ts.usermanagement.Role)
	 */
	@Override
	public void save(Role role)  {
		getSession().save(role);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAORole#saveOrUpdate(lu.itrust.business.ts.usermanagement.Role)
	 */
	@Override
	public void saveOrUpdate(Role role)  {
		getSession().saveOrUpdate(role);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAORole#delete(long)
	 */
	@Override
	public void delete(Integer id)  {
		getSession().delete(get(id));
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAORole#delete(java.lang.String)
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
	 * @see lu.itrust.business.ts.database.dao.DAORole#delete(lu.itrust.business.ts.usermanagement.Role)
	 */
	@Override
	public void delete(Role role)  {
		getSession().delete(role);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAORole#delete(lu.itrust.business.ts.usermanagement.User)
	 */
	@Override
	public void delete(User user)  {
		for (Role role : getAllFromUser(user))
			delete(role);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Role getByType(RoleType type) {
		return (Role) createQueryWithCache("FROM Role role WHERE role.type = :type").setParameter("type",type).uniqueResultOptional().orElse(null);
	}
}