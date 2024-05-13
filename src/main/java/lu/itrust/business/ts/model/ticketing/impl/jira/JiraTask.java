/**
 * 
 */
package lu.itrust.business.ts.model.ticketing.impl.jira;

import java.util.List;
import java.util.Map;

import lu.itrust.business.ts.model.ticketing.TicketingField;
import lu.itrust.business.ts.model.ticketing.TicketingTask;
import lu.itrust.business.ts.model.ticketing.impl.AbstractTask;

/**
 * Represents a Jira task.
 */
public class JiraTask extends AbstractTask {

	private List<JiraIssueLink> issueLinks;
	private Map<String, JiraCustomField> customFields;
	private List<JiraTask> subTasks;
	private String type;
	private String status;
	private String priority;

	/**
	 * Default constructor.
	 */
	public JiraTask() {
	}

	/**
	 * Constructs a Jira task with the specified parameters.
	 *
	 * @param id          the ID of the task
	 * @param name        the name of the task
	 * @param type        the type of the task
	 * @param status      the status of the task
	 * @param description the description of the task
	 * @param progress    the progress of the task
	 */
	public JiraTask(String id, String name, String type, String status, String description, int progress) {
		super(id, name, type, status, description, progress);
		setPriority("Normal");
	}

	/**
	 * Constructs a Jira task with the specified parameters.
	 *
	 * @param id     the ID of the task
	 * @param name   the name of the task
	 * @param type   the type of the task
	 * @param status the status of the task
	 */
	public JiraTask(String id, String name, String type, String status) {
		super(id, name, type, status, null, 0);
		setPriority("Normal");
	}

	/**
	 * Gets the issue links associated with the task.
	 *
	 * @return the issue links
	 */
	@Override
	public List<JiraIssueLink> getIssueLinks() {
		return issueLinks;
	}

	/**
	 * Gets the sub tasks of the task.
	 *
	 * @return the sub tasks
	 * @see lu.itrust.business.ts.model.ticketing.TicketingTask#getSubTickets()
	 */
	@Override
	public List<JiraTask> getSubTasks() {
		return this.subTasks;
	}

	/**
	 * Gets the type of the task.
	 *
	 * @return the type
	 */
	@Override
	public String getType() {
		return type;
	}

	/**
	 * Sets the issue links for the task.
	 *
	 * @param issueLinks the issue links to set
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void setIssueLinks(List<? extends TicketingField> issueLinks) {
		this.issueLinks = (List<JiraIssueLink>) issueLinks;
	}

	/**
	 * Sets the sub tasks for the task.
	 *
	 * @param subTasks the sub tasks to set
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void setSubTask(List<? extends TicketingTask> subTasks) {
		this.subTasks = (List<JiraTask>) subTasks;
	}

	/**
	 * Sets the type of the task.
	 *
	 * @param type the type to set
	 */
	@Override
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Gets the status of the task.
	 *
	 * @return the status
	 */
	@Override
	public String getStatus() {
		return status;
	}

	/**
	 * Sets the status of the task.
	 *
	 * @param status the status to set
	 */
	@Override
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * Gets the priority of the task.
	 *
	 * @return the priority
	 */
	@Override
	public String getPriority() {
		return this.priority;
	}

	/**
	 * Sets the priority of the task.
	 *
	 * @param priority the priority to set
	 */
	@Override
	public void setPriority(String priority) {
		this.priority = priority;
	}

	/**
	 * Gets the custom fields of the task.
	 *
	 * @return the custom fields
	 * @see
	 * lu.itrust.business.ts.model.ticketing.TicketingTask#getCustomFields()
	 */
	@Override
	public Map<String, JiraCustomField> getCustomFields() {
		return this.customFields;
	}

	/**
	 * Sets the custom fields for the task.
	 *
	 * @param customFields the custom fields to set
	 * @see
	 * lu.itrust.business.ts.model.ticketing.TicketingTask#setCustomFields(java.
	 * util.Map)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void setCustomFields(Map<String, ? extends TicketingField> customFields) {
		this.customFields = (Map<String, JiraCustomField>) customFields;
	}
}
