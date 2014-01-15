/**
 * 
 */
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
 * @author oensuifudine
 * 
 */
@Transactional
@Service
public class ServiceRoleImpl implements ServiceRole {

	@Autowired
	private DAORole daoRole;

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceRole#get(long)
	 */
	@Override
	public Role get(long id) throws Exception {
		return daoRole.get(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceRole#get(java.lang.String)
	 */
	@Override
	public List<Role> getByUser(String login) throws Exception {
		// TODO Auto-generated method stub
		return daoRole.getFromUser(login);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceRole#get(lu.itrust.business.TS.User)
	 */
	@Override
	public List<Role> getByUser(User user) throws Exception {
		return daoRole.getFromUser(user);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceRole#loadAll()
	 */
	@Override
	public List<Role> loadAll() throws Exception {
		return daoRole.loadAll();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceRole#save(lu.itrust.business.TS.Role)
	 */
	@Transactional
	@Override
	public void save(Role role) throws Exception {
		daoRole.save(role);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceRole#saveOrUpdate(lu.itrust.business
	 * .TS.Role)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(Role role) throws Exception {
		daoRole.saveOrUpdate(role);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceRole#delete(lu.itrust.business.TS.Role)
	 */
	@Transactional
	@Override
	public void delete(Role role) throws Exception {
		daoRole.delete(role);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceRole#delete(long)
	 */
	@Transactional
	@Override
	public void delete(long id) throws Exception {
		daoRole.delete(id);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceRole#delete(lu.itrust.business.TS.User)
	 */
	@Transactional
	@Override
	public void delete(User user) throws Exception {
		daoRole.delete(user);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceRole#delete(java.lang.String)
	 */
	@Transactional
	@Override
	public void delete(String login) throws Exception {
		daoRole.delete(login);

	}

	public DAORole getDaoRole() {
		return daoRole;
	}

	public void setDaoRole(DAORole daoRole) {
		this.daoRole = daoRole;
	}

	@Override
	public Role findByName(String name) throws Exception {
		// TODO Auto-generated method stub
		return this.daoRole.findByName(name);
	}

}
