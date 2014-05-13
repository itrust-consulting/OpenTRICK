package lu.itrust.business.service.impl;

import java.util.List;

import lu.itrust.business.TS.Customer;
import lu.itrust.business.TS.usermanagement.Role;
import lu.itrust.business.TS.usermanagement.User;
import lu.itrust.business.dao.DAORole;
import lu.itrust.business.dao.DAOUser;
import lu.itrust.business.service.ServiceUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ServiceUserImpl.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl.
 * @version
 * @since Jan 1, 2013
 */
@Service
public class ServiceUserImpl implements ServiceUser {

	@Autowired
	private DAOUser daoUser;

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
	 * @see lu.itrust.business.service.ServiceUser#get(int)
	 */
	@Override
	public User get(Integer id) throws Exception {
		return daoUser.get(id);
	}

	/**
	 * get: <br>
	 * Description
	 * 
	 * @param login
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceUser#get(java.lang.String)
	 */
	@Override
	public User get(String login) throws Exception {
		return daoUser.get(login);
	}

	/**
	 * get: <br>
	 * Description
	 * 
	 * @param login
	 * @param password
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceUser#get(java.lang.String, java.lang.String)
	 */
	@Override
	public User get(String login, String password) throws Exception {
		return daoUser.get(login, password);
	}

	/**
	 * noUsers: <br>
	 * Description
	 * 
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceUser#noUsers()
	 */
	@Transactional
	@Override
	public boolean noUsers() throws Exception {
		return daoUser.noUsers();
	}

	/**
	 * getAllUsers: <br>
	 * Description
	 * 
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceUser#getAllUsers()
	 */
	@Override
	public List<User> getAll() throws Exception {
		return daoUser.getAll();
	}

	/**
	 * getAllByFirstName: <br>
	 * Description
	 * 
	 * @param name
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceUser#getAllByFirstName(java.lang.String)
	 */
	@Override
	public List<User> getAllByFirstName(String name) throws Exception {
		return daoUser.getAllByFirstName(name);
	}

	/**
	 * getAllByCountry: <br>
	 * Description
	 * 
	 * @param name
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceUser#getAllByCountry(java.lang.String)
	 */
	@Override
	public List<User> getAllByCountry(String name) throws Exception {
		return daoUser.getAllByCountry(name);
	}

	/**
	 * getAllUsersFromCustomer: <br>
	 * Description
	 * 
	 * @param customer
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceUser#getAllUsersFromCustomer(int)
	 */
	@Override
	public List<User> getAllFromCustomer(Integer customer) throws Exception {
		return daoUser.getAllFromCustomer(customer);
	}

	/**
	 * getAllUsersFromCustomer: <br>
	 * Description
	 * 
	 * @param customer
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceUser#getAllUsersFromCustomer(lu.itrust.business.TS.Customer)
	 */
	@Override
	public List<User> getAllFromCustomer(Customer customer) throws Exception {
		return daoUser.getAllFromCustomer(customer);
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

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param user
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceUser#save(lu.itrust.business.TS.usermanagement.User)
	 */
	@Transactional
	@Override
	public void save(User user) throws Exception {
		daoUser.save(user);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @param user
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceUser#saveOrUpdate(lu.itrust.business.TS.usermanagement.User)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(User user) throws Exception {
		daoUser.saveOrUpdate(user);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param id
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceUser#delete(int)
	 */
	@Transactional
	@Override
	public void delete(Integer id) throws Exception {
		daoUser.delete(id);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param user
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceUser#delete(lu.itrust.business.TS.usermanagement.User)
	 */
	@Transactional
	@Override
	public void delete(User user) throws Exception {
		daoUser.delete(user);
	}
}