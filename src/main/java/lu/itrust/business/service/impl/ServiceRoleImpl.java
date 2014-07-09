package lu.itrust.business.service.impl;

import java.util.List;

import lu.itrust.business.TS.usermanagement.Role;
import lu.itrust.business.TS.usermanagement.User;
import lu.itrust.business.dao.DAORole;
import lu.itrust.business.service.ServiceRole;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceRole#get(long)
	 */
	@Override
	public Role get(Integer id) throws Exception {
		return daoRole.get(id);
	}

	/**
	 * getRoleByName: <br>
	 * Description
	 * 
	 * @param name
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceRole#getRoleByName(java.lang.String)
	 */
	@Override
	public Role getByName(String name) throws Exception {
		return this.daoRole.getByName(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceRole#loadAll()
	 */
	@Override
	public List<Role> getAll() throws Exception {
		return daoRole.getAll();
	}

	/**
	 * getFromUser: <br>
	 * Description
	 * 
	 * @param login
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceRole#getFromUser(java.lang.String)
	 */
	@Override
	public List<Role> getAllFromUser(String login) throws Exception {
		return daoRole.getAllFromUser(login);
	}

	/**
	 * getFromUser: <br>
	 * Description
	 * 
	 * @param user
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceRole#getFromUser(lu.itrust.business.TS.usermanagement.User)
	 */
	@Override
	public List<Role> getAllFromUser(User user) throws Exception {
		return daoRole.getAllFromUser(user);
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param role
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceRole#save(lu.itrust.business.TS.usermanagement.Role)
	 */
	@Transactional
	@Override
	public void save(Role role) throws Exception {
		daoRole.save(role);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @param role
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceRole#saveOrUpdate(lu.itrust.business.TS.usermanagement.Role)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(Role role) throws Exception {
		daoRole.saveOrUpdate(role);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param id
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceRole#delete(long)
	 */
	@Transactional
	@Override
	public void delete(Integer id) throws Exception {
		daoRole.delete(id);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param login
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceRole#delete(java.lang.String)
	 */
	@Transactional
	@Override
	public void delete(String login) throws Exception {
		daoRole.delete(login);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param role
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceRole#delete(lu.itrust.business.TS.usermanagement.Role)
	 */
	@Transactional
	@Override
	public void delete(Role role) throws Exception {
		daoRole.delete(role);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param user
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceRole#delete(lu.itrust.business.TS.usermanagement.User)
	 */
	@Transactional
	@Override
	public void delete(User user) throws Exception {
		daoRole.delete(user);
	}
}