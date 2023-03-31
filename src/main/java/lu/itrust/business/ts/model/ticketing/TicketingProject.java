/**
 * 
 */
package lu.itrust.business.ts.model.ticketing;

import java.util.List;

/**
 * @author eomar
 *
 */
public interface TicketingProject extends TicketingBase {

	List<? extends TicketingTask> getTasks();

	void setTasks(List<? extends TicketingTask> tasks);
}
