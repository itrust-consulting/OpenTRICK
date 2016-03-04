package lu.itrust.business.TS.database.service.impl;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.TS.database.dao.DAOUser;
import lu.itrust.business.TS.database.service.ServiceUser;
import lu.itrust.business.TS.model.general.Customer;
import lu.itrust.business.TS.usermanagement.Role;
import lu.itrust.business.TS.usermanagement.User;

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


	/**
	 * get: <br>
	 * Description
	 * 
	 * @param id
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceUser#get(int)
	 */
	@Override
	public User get(Integer id)  {
		return daoUser.get(id);
	}

	/**
	 * get: <br>
	 * Description
	 * 
	 * @param login
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceUser#get(java.lang.String)
	 */
	@Override
	public User get(String login)  {
		return daoUser.get(login);
	}

	/**
	 * get: <br>
	 * Description
	 * 
	 * @param login
	 * @param password
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceUser#get(java.lang.String, java.lang.String)
	 */
	@Override
	public User get(String login, String password)  {
		return daoUser.get(login, password);
	}

	/**
	 * noUsers: <br>
	 * Description
	 * 
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceUser#noUsers()
	 */
	@Transactional
	@Override
	public boolean noUsers()  {
		return daoUser.noUsers();
	}

	/**
	 * getAllUsers: <br>
	 * Description
	 * 
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceUser#getAllUsers()
	 */
	@Override
	public List<User> getAll()  {
		return daoUser.getAll();
	}

	/**
	 * getAllByFirstName: <br>
	 * Description
	 * 
	 * @param name
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceUser#getAllByFirstName(java.lang.String)
	 */
	@Override
	public List<User> getAllByFirstName(String name)  {
		return daoUser.getAllByFirstName(name);
	}

	/**
	 * getAllByCountry: <br>
	 * Description
	 * 
	 * @param name
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceUser#getAllByCountry(java.lang.String)
	 */
	@Override
	public List<User> getAllByCountry(String name)  {
		return daoUser.getAllByCountry(name);
	}

	/**
	 * getAllUsersFromCustomer: <br>
	 * Description
	 * 
	 * @param customer
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceUser#getAllUsersFromCustomer(int)
	 */
	@Override
	public List<User> getAllFromCustomer(Integer customer)  {
		return daoUser.getAllFromCustomer(customer);
	}

	/**
	 * getAllUsersFromCustomer: <br>
	 * Description
	 * 
	 * @param customer
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceUser#getAllUsersFromCustomer(lu.itrust.business.TS.model.general.Customer)
	 */
	@Override
	public List<User> getAllFromCustomer(Customer customer)  {
		return daoUser.getAllFromCustomer(customer);
	}

	/**
	 * getAllAdministrators: <br>
	 * Description
	 *
	 * @{tags}
	 *
	 * @see lu.itrust.business.TS.database.service.ServiceUser#getAllAdministrators()
	 */
	@Override
	public List<User> getAllAdministrators()  {
		return daoUser.getAllAdministrators();
	}
	
	/**
	 * 
	 * hasRole: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceUser#hasRole(lu.itrust.business.TS.usermanagement.User,
	 *      lu.itrust.business.TS.usermanagement.Role)
	 */
	@Transactional
	@Override
	public boolean hasRole(User user, Role role)  {
		return daoUser.hasRole(user, role);
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param user
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceUser#save(lu.itrust.business.TS.usermanagement.User)
	 */
	@Transactional
	@Override
	public void save(User user)  {
		daoUser.save(user);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @param user
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceUser#saveOrUpdate(lu.itrust.business.TS.usermanagement.User)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(User user)  {
		daoUser.saveOrUpdate(user);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param id
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceUser#delete(int)
	 */
	@Transactional
	@Override
	public void delete(Integer id)  {
		daoUser.delete(id);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param user
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceUser#delete(lu.itrust.business.TS.usermanagement.User)
	 */
	@Transactional
	@Override
	public void delete(User user)  {
		daoUser.delete(user);
	}

	@Override
	public User getByEmail(String email) {
		return daoUser.getByEmail(email);
	}

	@Override
	public List<User> getAllOthers(Collection<User> users) {
		return daoUser.getAllOthers(users);
	}

	@Override
	public List<User> getAllOthers(User user) {
		return daoUser.getAllOthers(user);
	}

	@Override
	public boolean existByUsername(String username) {
		return daoUser.existByUsername(username);
	}

	@Override
	public boolean existByEmail(String email) {
		return daoUser.existByEmail(email);
	}
}