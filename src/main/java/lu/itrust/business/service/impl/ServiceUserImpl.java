/**
 * 
 */
package lu.itrust.business.service.impl;

import java.util.List;

import lu.itrust.business.TS.Customer;
import lu.itrust.business.TS.usermanagement.Role;
import lu.itrust.business.TS.usermanagement.User;
import lu.itrust.business.dao.DAOUser;
import lu.itrust.business.service.ServiceUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author oensuifudine
 * 
 */
@Service
public class ServiceUserImpl implements ServiceUser {

	@Autowired
	private DAOUser daoUser;

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceUser#get(long)
	 */
	@Override
	public User get(long id) throws Exception {
		return daoUser.get(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceUser#get(java.lang.String)
	 */
	@Override
	public User get(String login) throws Exception {
		return daoUser.get(login);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceUser#get(java.lang.String, java.lang.String)
	 */
	@Override
	public User get(String login, String password) throws Exception {
		return daoUser.get(login, password);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceUser#loadAll()
	 */
	@Override
	public List<User> loadAll() throws Exception {
		return daoUser.loadAll();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceUser#loadByName(java.lang.String)
	 */
	@Override
	public List<User> loadByName(String name) throws Exception {
		return daoUser.loadByName(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceUser#loadByCountry(java.lang.String)
	 */
	@Override
	public List<User> loadByCountry(String name) throws Exception {
		return daoUser.loadByCountry(name);
	}

	/**
	 * addRole: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.service.ServiceUser#addRole(lu.itrust.business.TS.usermanagement.User,
	 *      lu.itrust.business.TS.usermanagement.Role)
	 */
	@Transactional
	public boolean addRole(User user, Role role) throws Exception {
		return daoUser.addRole(user, role);
	}

	/**
	 * removeRole: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.service.ServiceUser#removeRole(lu.itrust.business.TS.usermanagement.User,
	 *      lu.itrust.business.TS.usermanagement.Role)
	 */
	@Transactional
	public boolean removeRole(User user, Role role) throws Exception {
		return daoUser.removeRole(user, role);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceUser#save(lu.itrust.business.TS.User)
	 */
	@Transactional
	@Override
	public void save(User user) throws Exception {
		daoUser.save(user);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceUser#saveOrUpdate(lu.itrust.business.TS.User)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(User user) throws Exception {
		daoUser.saveOrUpdate(user);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceUser#delete(lu.itrust.business.TS.User)
	 */
	@Transactional
	@Override
	public void delete(User user) throws Exception {
		daoUser.delete(user);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceUser#delete(long)
	 */
	@Transactional
	@Override
	public void delete(long id) throws Exception {
		daoUser.delete(id);
	}

	/**
	 * 
	 * hasUsers: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.service.ServiceUser#hasUsers()
	 */
	@Transactional
	@Override
	public boolean hasUsers() throws Exception {
		return daoUser.hasUsers();
	}

	/**
	 * 
	 * hasRole: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.service.ServiceUser#hasRole(lu.itrust.business.TS.usermanagement.User,
	 *      lu.itrust.business.TS.usermanagement.Role)
	 */
	@Transactional
	@Override
	public boolean hasRole(User user, Role role) throws Exception {
		return daoUser.hasRole(user, role);
	}

	public DAOUser getDaoUser() {
		return daoUser;
	}

	public void setDaoUser(DAOUser daoUser) {
		this.daoUser = daoUser;
	}

	@Override
	public List<User> loadByCustomer(int customer) throws Exception {
		return daoUser.loadByCustomer(customer);
	}

	@Override
	public List<User> loadByCustomer(Customer customer) throws Exception {
		return daoUser.loadByCustomer(customer);
	}
}
