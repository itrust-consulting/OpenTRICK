/**
 * 
 */
package lu.itrust.business.dao;

import java.util.List;

import lu.itrust.business.TS.User;

/**
 * @author oensuifudine
 *
 */
public interface DAOUser {
	
	User get(long id) throws Exception;
	
	User get(String login) throws Exception;
	
	User get(String login,String password) throws Exception;
	
	List<User> loadAll()throws Exception;
	
	List<User> loadByName(String name)throws Exception;
	
	List<User> loadByCountry(String name)throws Exception;
	
	void save(User user)throws Exception;
	
	void saveOrUpdate(User user)throws Exception;
	
	void delete(User user)throws Exception;
	
	void delete(long id)throws Exception;

	boolean isEmpty()throws Exception;
}
