/**
 * 
 */
package lu.itrust.business.ts.database.service;

import java.util.List;

import lu.itrust.business.ts.usermanagement.ResetPassword;
import lu.itrust.business.ts.usermanagement.User;

/**
 * @author eomar
 *
 */
public interface ServiceResetPassword {

	ResetPassword get(long id);
	
	ResetPassword get(User user);
	
	ResetPassword get(String keyControl);
	
	List<ResetPassword> getAll();
	
	List<ResetPassword> getAll(int page, int size);
	
	void saveOrUpdate(ResetPassword resetPassword);
	
	ResetPassword save(ResetPassword resetPassword);
	
	ResetPassword merge(ResetPassword resetPassword);
	
	void delete(ResetPassword resetPassword);
}
