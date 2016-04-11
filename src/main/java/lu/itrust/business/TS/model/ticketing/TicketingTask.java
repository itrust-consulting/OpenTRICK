/**
 * 
 */
package lu.itrust.business.TS.model.ticketing;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author eomar
 *
 */
public interface TicketingTask extends TicketingTicket {

	Date getCreated();

	Date getDeadline();

	Date getLastUpdated();

	List<? extends TicketingTask> getSubTasks();

	Map<String, ? extends TicketingField> getCustomFields();

	void setCreated(Date date);

	void setDeadline(Date date);

	void setLastUpdated(Date date);

	void setSubTask(List<? extends TicketingTask> subTasks);

	void setCustomFields(Map<String, ? extends TicketingField> customFields);
}
