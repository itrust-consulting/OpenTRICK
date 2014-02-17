/**
 * 
 */
package lu.itrust.business.service;

import java.util.List;

import lu.itrust.business.TS.usermanagement.UserSQLite;

/**
 * @author eomar
 * 
 */
public interface ServiceUserSqLite {

	UserSQLite findOne(long id);

	UserSQLite findByFileName(String fileName);
	
	UserSQLite findByIdAndUser(long idFile, String username);

	List<UserSQLite> findByUser(String username);

	List<UserSQLite> findByUser(String username, int pageIndex, int pageSize);

	UserSQLite save(UserSQLite userSqLite);

	void saveOrUpdate(UserSQLite userSqLite);

	UserSQLite merge(UserSQLite userSqLite);

	void delete(UserSQLite userSqLite);

	void delete(long idUserSqLite);

	void delete(String fileName);

	

}
