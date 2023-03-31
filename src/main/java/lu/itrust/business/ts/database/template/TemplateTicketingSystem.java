/**
 * 
 */
package lu.itrust.business.ts.database.template;

import lu.itrust.business.ts.database.TemplateDAOService;
import lu.itrust.business.ts.model.general.TicketingSystem;

/**
 * @author eomar
 *
 */
public interface TemplateTicketingSystem extends TemplateDAOService<TicketingSystem, Long> {
	
	TicketingSystem findByCustomerId(Integer customerId);
}
