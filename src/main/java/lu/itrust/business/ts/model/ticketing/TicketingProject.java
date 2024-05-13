/**
 * 
 */
package lu.itrust.business.ts.model.ticketing;

import java.util.List;

/**
 * Represents a ticketing project.
 * This interface extends the TicketingBase interface.
 */
public interface TicketingProject extends TicketingBase {

	/**
	 * Retrieves the list of tasks associated with this project.
	 *
	 * @return the list of tasks
	 */
	List<? extends TicketingTask> getTasks();

	/**
	 * Sets the list of tasks associated with this project.
	 *
	 * @param tasks the list of tasks to set
	 */
	void setTasks(List<? extends TicketingTask> tasks);
}
