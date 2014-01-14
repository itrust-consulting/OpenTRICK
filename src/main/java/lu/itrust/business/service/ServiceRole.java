/**
 * 
 */
package lu.itrust.business.service;

import java.util.List;

import lu.itrust.business.TS.usermanagment.Role;
import lu.itrust.business.TS.usermanagment.User;

/**
 * @author oensuifudine
 * 
 */
public interface ServiceRole {

	Role get(long id) throws Exception;
	
	Role findByName(String name) throws Exception;
	
	List<Role> getByUser(String login) throws Exception;
	
	List<Role> getByUser(User user) throws Exception;
		
	List<Role> loadAll() throws Exception;
	
	void save(Role role)throws Exception;
	
	void saveOrUpdate(Role role)throws Exception;
	
	void delete(Role role)throws Exception;
	
	void delete(long id)throws Exception;
	
	void delete(User user)throws Exception;
	
	void delete(String login)throws Exception;
}
