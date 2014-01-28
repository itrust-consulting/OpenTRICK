/**
 * 
 */
package lu.itrust.business.dao;

import java.util.List;

import lu.itrust.business.TS.usermanagement.UserSqlLite;

/**
 * @author eomar
 * 
 */
public interface DAOUserSqlLite {

	UserSqlLite findOne(long id);

	UserSqlLite findByFileName(String fileName);

	UserSqlLite findByIdAndUser(long idFile, String username);

	List<UserSqlLite> findByUser(String username);

	List<UserSqlLite> findByUser(String username, int pageIndex, int pageSize);

	UserSqlLite save(UserSqlLite userSqlLite);

	void saveOrUpdate(UserSqlLite userSqlLite);

	UserSqlLite merge(UserSqlLite userSqlLite);

	void delete(UserSqlLite userSqlLite);

	void delete(long idUserSqlLite);

	void delete(String fileName);

}
