/**
 * 
 */
package lu.itrust.business.ts.database.template;

import java.util.List;

import lu.itrust.business.ts.database.TemplateDAOService;
import lu.itrust.business.ts.usermanagement.UserCredential;

/**
 * @author eomar
 *
 */
public interface TemplateUserCredential extends TemplateDAOService<UserCredential, Long> {
	
	UserCredential findByIdAndUsername(long id, String name);
	
	List<UserCredential> findByUsername(String username);
}
