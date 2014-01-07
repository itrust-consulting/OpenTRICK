/**
 * 
 */
package lu.itrust.business.dao;

import java.util.List;

import lu.itrust.business.view.model.Role;
import lu.itrust.business.view.model.User;

/**
 * @author oensuifudine
 *
 */
public interface DAORole {
	
	Role get(long id) throws Exception;
	
	List<Role> getFromUser(String login) throws Exception;
	
	List<Role> getFromUser(User user) throws Exception;
		
	List<Role> loadAll() throws Exception;
	
	void save(Role role)throws Exception;
	
	void saveOrUpdate(Role role)throws Exception;
	
	void delete(Role role)throws Exception;
	
	void delete(long id)throws Exception;
	
	void delete(User user)throws Exception;
	
	void delete(String login)throws Exception;

	Role findByName(String name) throws Exception;

}
