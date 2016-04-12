/**
 * 
 */
package lu.itrust.business.TS.model.ticketing.impl.jira;

import java.util.List;
import java.util.Map;

import lu.itrust.business.TS.model.ticketing.TickectingComment;
import lu.itrust.business.TS.model.ticketing.TicketingField;
import lu.itrust.business.TS.model.ticketing.TicketingTask;
import lu.itrust.business.TS.model.ticketing.impl.TaskImpl;

/**
 * @author eomar
 *
 */
public class JiraTask extends TaskImpl {
	
	private List<? extends TickectingComment> comments;
	
	private Map<String, JiraCustomField> customFields;
	
	private List<? extends TicketingField> issueLinks;

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
	 * @param description
	 * @param progress
	 */
	public JiraTask(String id, String name, String description, int progress) {
		super(id, name, description, progress);
	}

	@Override
	public List<? extends TickectingComment> getComments() {
		return comments;
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

	@Override
	public List<? extends TicketingField> getIssueLinks() {
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

	@Override
	public void setComments(List<? extends TickectingComment> comments) {
		this.comments = comments;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setCustomFields(Map<String, ? extends TicketingField> customFields) {
		this.customFields = (Map<String, JiraCustomField>) customFields;
	}

	@Override
	public void setIssueLinks(List<? extends TicketingField> issueLinks) {
		this.issueLinks = issueLinks;
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

}
