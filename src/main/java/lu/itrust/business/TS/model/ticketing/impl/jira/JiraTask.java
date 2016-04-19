/**
 * 
 */
package lu.itrust.business.TS.model.ticketing.impl.jira;

import java.util.List;
import java.util.Map;

import lu.itrust.business.TS.model.ticketing.TicketingField;
import lu.itrust.business.TS.model.ticketing.TicketingTask;
import lu.itrust.business.TS.model.ticketing.impl.AbstractTask;

/**
 * @author eomar
 *
 */
public class JiraTask extends AbstractTask {

	private List<JiraIssueLink> issueLinks;

	private Map<String, JiraCustomField> customFields;

	private List<JiraTask> subTasks;

	private String type;

	private String status;

	private String priority;

	/**
	 * 
	 */
	public JiraTask() {
	}

	/**
	 * @param id
	 * @param name
	 * @param type
	 *            TODO
	 * @param status
	 *            TODO
	 * @param description
	 * @param progress
	 */
	public JiraTask(String id, String name, String type, String status, String description, int progress) {
		super(id, name, type, status, description, progress);
		setPriority("Normal");
	}

	public JiraTask(String id, String name, String type, String status) {
		super(id, name, type, status, null, 0);
		setPriority("Normal");
	}

	@Override
	public List<JiraIssueLink> getIssueLinks() {
		return issueLinks;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.model.ticketing.TicketingTask#getSubTickets()
	 */
	@Override
	public List<JiraTask> getSubTasks() {
		return this.subTasks;
	}

	@Override
	public String getType() {
		return type;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setIssueLinks(List<? extends TicketingField> issueLinks) {
		this.issueLinks = (List<JiraIssueLink>) issueLinks;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setSubTask(List<? extends TicketingTask> subTasks) {
		this.subTasks = (List<JiraTask>) subTasks;
	}

	@Override
	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String getStatus() {
		return status;
	}

	@Override
	public void setStatus(String status) {
		this.status = status;

	}

	@Override
	public String getPriority() {
		return this.priority;
	}

	@Override
	public void setPriority(String priority) {
		this.priority = priority;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.model.ticketing.TicketingTask#getCustomFields()
	 */
	@Override
	public Map<String, JiraCustomField> getCustomFields() {
		return this.customFields;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.model.ticketing.TicketingTask#setCustomFields(java.
	 * util.Map)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void setCustomFields(Map<String, ? extends TicketingField> customFields) {
		this.customFields = (Map<String, JiraCustomField>) customFields;
	}

}
