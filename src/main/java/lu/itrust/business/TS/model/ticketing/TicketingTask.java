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
public interface TicketingTask extends TicketingBase {
	
	String getStatus();
	
	String getType();
	
	String getPriority();
	
	String getReporter();
	
	String getAssignee();
	
	String getDescription();

	Date getCreated();

	Date getDue();

	Date getUpdated();
	
	int getProgress();
	
	List<? extends TicketingField> getIssueLinks();
	
	List<? extends TicketingTask> getSubTasks();

	Map<String, ? extends TicketingField> getCustomFields();
	
	List<? extends TickectingComment> getComments();
	
	void setStatus(String status);
	
	void setType(String type);
	
	void setPriority(String priority);
	
	void setReporter(String reporter);
	
	void setAssignee(String assignee);
	
	void setDescription(String description);

	void setCreated(Date date);

	void setDue(Date date);

	void setUpdated(Date date);
	
	void setProgress(int progress);
	
	void setIssueLinks(List<?extends TicketingField> issueLinks);
	
	void setSubTask(List<? extends TicketingTask> subTasks);

	void setCustomFields(Map<String, ? extends TicketingField> customFields);
	
	void setComments(List<? extends TickectingComment> comments);
}
