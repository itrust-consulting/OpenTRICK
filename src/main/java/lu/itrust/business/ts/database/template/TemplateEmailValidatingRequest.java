/**
 * 
 */
package lu.itrust.business.ts.database.template;

import lu.itrust.business.ts.database.TemplateDAOService;
import lu.itrust.business.ts.usermanagement.EmailValidatingRequest;
import lu.itrust.business.ts.usermanagement.User;

/**
 * @author eomar
 *
 */
public interface TemplateEmailValidatingRequest extends TemplateDAOService<EmailValidatingRequest, Long> {
	
	EmailValidatingRequest findByToken(String token);
	
	EmailValidatingRequest findByUsername(String username);
	
	EmailValidatingRequest findByEmail(String email);
	
	boolean existsByEmail(String email);
	
	boolean existsByToken(String token);
	
	boolean existsByUsername(String username);
	
	void deleteByUser(User user);
	
}
