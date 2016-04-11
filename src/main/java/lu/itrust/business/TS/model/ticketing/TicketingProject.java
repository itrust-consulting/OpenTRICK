/**
 * 
 */
package lu.itrust.business.TS.model.ticketing;

import java.util.List;

/**
 * @author eomar
 *
 */
public interface TicketingProject extends TicketingTicket {

	List<? extends TicketingTask> getTasks();

	void setTasks(List<? extends TicketingTask> tasks);
}
