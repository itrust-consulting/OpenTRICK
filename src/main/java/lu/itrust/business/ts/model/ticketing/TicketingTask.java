/**
 * 
 */
package lu.itrust.business.ts.model.ticketing;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * The TicketingTask interface represents a task in a ticketing system.
 * It extends the TicketingBase interface and provides methods to get and set various properties of the task.
 */
public interface TicketingTask extends TicketingBase {
	/**
	 * Gets the URL of the task.
	 *
	 * @return The URL of the task.
	 */
	String getUrl();

	/**
	 * Gets the status of the task.
	 *
	 * @return The status of the task.
	 */
	String getStatus();

	/**
	 * Gets the type of the task.
	 *
	 * @return The type of the task.
	 */
	String getType();

	/**
	 * Gets the priority of the task.
	 *
	 * @return The priority of the task.
	 */
	String getPriority();

	/**
	 * Gets the reporter of the task.
	 *
	 * @return The reporter of the task.
	 */
	String getReporter();

	/**
	 * Gets the assignee of the task.
	 *
	 * @return The assignee of the task.
	 */
	String getAssignee();

	/**
	 * Gets the description of the task.
	 *
	 * @return The description of the task.
	 */
	String getDescription();

	/**
	 * Gets the date when the task was created.
	 *
	 * @return The date when the task was created.
	 */
	Date getCreated();

	/**
	 * Gets the due date of the task.
	 *
	 * @return The due date of the task.
	 */
	Date getDue();

	/**
	 * Gets the date when the task was last updated.
	 *
	 * @return The date when the task was last updated.
	 */
	Date getUpdated();

	/**
	 * Gets the progress of the task.
	 *
	 * @return The progress of the task.
	 */
	int getProgress();

	/**
	 * Gets the issue links associated with the task.
	 *
	 * @return The issue links associated with the task.
	 */
	List<? extends TicketingField> getIssueLinks();

	/**
	 * Gets the sub tasks of the task.
	 *
	 * @return The sub tasks of the task.
	 */
	List<? extends TicketingTask> getSubTasks();

	/**
	 * Gets the custom fields of the task.
	 *
	 * @return The custom fields of the task.
	 */
	Map<String, ? extends TicketingField> getCustomFields();

	/**
	 * Gets the comments of the task.
	 *
	 * @return The comments of the task.
	 */
	List<? extends TickectingComment> getComments();

	/**
	 * Sets the URL of the task.
	 *
	 * @param url The URL of the task.
	 */
	void setUrl(String url);

	/**
	 * Sets the status of the task.
	 *
	 * @param status The status of the task.
	 */
	void setStatus(String status);

	/**
	 * Sets the type of the task.
	 *
	 * @param type The type of the task.
	 */
	void setType(String type);

	/**
	 * Sets the priority of the task.
	 *
	 * @param priority The priority of the task.
	 */
	void setPriority(String priority);

	/**
	 * Sets the reporter of the task.
	 *
	 * @param reporter The reporter of the task.
	 */
	void setReporter(String reporter);

	/**
	 * Sets the assignee of the task.
	 *
	 * @param assignee The assignee of the task.
	 */
	void setAssignee(String assignee);

	/**
	 * Sets the description of the task.
	 *
	 * @param description The description of the task.
	 */
	void setDescription(String description);

	/**
	 * Sets the date when the task was created.
	 *
	 * @param date The date when the task was created.
	 */
	void setCreated(Date date);

	/**
	 * Sets the due date of the task.
	 *
	 * @param date The due date of the task.
	 */
	void setDue(Date date);

	/**
	 * Sets the date when the task was last updated.
	 *
	 * @param date The date when the task was last updated.
	 */
	void setUpdated(Date date);

	/**
	 * Sets the progress of the task.
	 *
	 * @param progress The progress of the task.
	 */
	void setProgress(int progress);

	/**
	 * Sets the issue links associated with the task.
	 *
	 * @param issueLinks The issue links associated with the task.
	 */
	void setIssueLinks(List<? extends TicketingField> issueLinks);

	/**
	 * Sets the sub tasks of the task.
	 *
	 * @param subTasks The sub tasks of the task.
	 */
	void setSubTask(List<? extends TicketingTask> subTasks);

	/**
	 * Sets the custom fields of the task.
	 *
	 * @param customFields The custom fields of the task.
	 */
	void setCustomFields(Map<String, ? extends TicketingField> customFields);

	/**
	 * Sets the comments of the task.
	 *
	 * @param comments The comments of the task.
	 */
	void setComments(List<? extends TickectingComment> comments);
}
