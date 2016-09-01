package lu.itrust.business.TS.database.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.TS.database.dao.DAORole;
import lu.itrust.business.TS.database.service.ServiceRole;
import lu.itrust.business.TS.usermanagement.Role;
import lu.itrust.business.TS.usermanagement.RoleType;
import lu.itrust.business.TS.usermanagement.User;

/**
 * ServiceRoleImpl.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl.
 * @version
 * @since Jan 16, 2013
 */
@Transactional
@Service
public class ServiceRoleImpl implements ServiceRole {

	@Autowired
	private DAORole daoRole;

	/**
	 * get: <br>
	 * Description
	 * 
	 * @param id
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceRole#get(long)
	 */
	@Override
	public Role get(Integer id)  {
		return daoRole.get(id);
	}

	/**
	 * getRoleByName: <br>
	 * Description
	 * 
	 * @param name
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceRole#getRoleByName(java.lang.String)
	 */
	@Override
	public Role getByName(String name)  {
		return this.daoRole.getByName(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceRole#loadAll()
	 */
	@Override
	public List<Role> getAll()  {
		return daoRole.getAll();
	}

	/**
	 * getFromUser: <br>
	 * Description
	 * 
	 * @param login
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceRole#getFromUser(java.lang.String)
	 */
	@Override
	public List<Role> getAllFromUser(String login)  {
		return daoRole.getAllFromUser(login);
	}

	/**
	 * getFromUser: <br>
	 * Description
	 * 
	 * @param user
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceRole#getFromUser(lu.itrust.business.TS.usermanagement.User)
	 */
	@Override
	public List<Role> getAllFromUser(User user)  {
		return daoRole.getAllFromUser(user);
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param role
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceRole#save(lu.itrust.business.TS.usermanagement.Role)
	 */
	@Transactional
	@Override
	public void save(Role role)  {
		daoRole.save(role);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @param role
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceRole#saveOrUpdate(lu.itrust.business.TS.usermanagement.Role)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(Role role)  {
		daoRole.saveOrUpdate(role);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param id
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceRole#delete(long)
	 */
	@Transactional
	@Override
	public void delete(Integer id)  {
		daoRole.delete(id);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param login
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceRole#delete(java.lang.String)
	 */
	@Transactional
	@Override
	public void delete(String login)  {
		daoRole.delete(login);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param role
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceRole#delete(lu.itrust.business.TS.usermanagement.Role)
	 */
	@Transactional
	@Override
	public void delete(Role role)  {
		daoRole.delete(role);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param user
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceRole#delete(lu.itrust.business.TS.usermanagement.User)
	 */
	@Transactional
	@Override
	public void delete(User user)  {
		daoRole.delete(user);
	}

	@Override
	public Role getByType(RoleType type) {
		return daoRole.getByType(type);
	}
}