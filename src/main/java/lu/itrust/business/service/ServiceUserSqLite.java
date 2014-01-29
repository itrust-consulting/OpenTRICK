/**
 * 
 */
package lu.itrust.business.service;

import java.util.List;

import lu.itrust.business.TS.usermanagement.UserSqLite;

/**
 * @author eomar
 * 
 */
public interface ServiceUserSqLite {

	UserSqLite findOne(long id);

	UserSqLite findByFileName(String fileName);
	
	UserSqLite findByIdAndUser(long idFile, String username);

	List<UserSqLite> findByUser(String username);

	List<UserSqLite> findByUser(String username, int pageIndex, int pageSize);

	UserSqLite save(UserSqLite userSqLite);

	void saveOrUpdate(UserSqLite userSqLite);

	UserSqLite merge(UserSqLite userSqLite);

	void delete(UserSqLite userSqLite);

	void delete(long idUserSqLite);

	void delete(String fileName);

	

}
