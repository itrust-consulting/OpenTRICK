/**
 * 
 */
package lu.itrust.business.service;

import java.util.List;

import lu.itrust.business.TS.Customer;
import lu.itrust.business.TS.usermanagement.Role;
import lu.itrust.business.TS.usermanagement.User;

/**
 * @author oensuifudine
 *
 */
public interface ServiceUser {
	
	User get(long id) throws Exception;
	
	User get(String login) throws Exception;
	
	User get(String login,String password) throws Exception;
	
	List<User> loadAll()throws Exception;
	
	List<User> loadByName(String name)throws Exception;
	
	List<User> loadByCountry(String name)throws Exception;
	
	List<User> loadByCustomer(int customer)throws Exception;
	
	List<User> loadByCustomer(Customer customer)throws Exception;
	
	boolean addRole(User user, Role role) throws Exception;
	
	boolean removeRole(User user, Role role) throws Exception;
	
	boolean hasRole(User user, Role role) throws Exception;
	
	void save(User user)throws Exception;
	
	void saveOrUpdate(User user)throws Exception;
	
	void delete(User user)throws Exception;
	
	void delete(long id)throws Exception;

	boolean hasUsers() throws Exception;
}
