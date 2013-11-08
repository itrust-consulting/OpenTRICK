/**
 * 
 */
package lu.itrust.business.service;

import java.util.List;

import lu.itrust.business.view.model.Role;
import lu.itrust.business.view.model.User;

/**
 * @author oensuifudine
 * 
 */
public interface ServiceRole {

	Role get(long id) throws Exception;
	
	Role findByName(String name) throws Exception;
	
	List<Role> load(String login) throws Exception;
	
	List<Role> load(User user) throws Exception;
	
	List<Role> load(String login, String password)throws Exception;
	
	List<Role> loadAll() throws Exception;
	
	void save(Role role)throws Exception;
	
	void saveOrUpdate(Role role)throws Exception;
	
	void delete(Role role)throws Exception;
	
	void delete(long id)throws Exception;
	
	void delete(User user)throws Exception;
	
	void delete(String login)throws Exception;
}
