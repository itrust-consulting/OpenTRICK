/**
 * 
 */
package lu.itrust.business.TS.database.template;

import java.util.List;

import lu.itrust.business.TS.database.TemplateDAOService;
import lu.itrust.business.TS.usermanagement.UserCredential;

/**
 * @author eomar
 *
 */
public interface TemplateUserCredential extends TemplateDAOService<UserCredential, Long> {
	
	UserCredential findByIdAndUsername(long id, String name);
	
	List<UserCredential> findByUsername(String username);
}
