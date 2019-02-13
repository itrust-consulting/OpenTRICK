/**
 * 
 */
package lu.itrust.business.TS.model.ticketing.impl.redmine;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import lu.itrust.business.TS.model.ticketing.TicketingField;
import lu.itrust.business.TS.model.ticketing.TicketingTask;
import lu.itrust.business.TS.model.ticketing.impl.AbstractTask;

/**
 * @author eomar
 *
 */
public class RedmineTask extends AbstractTask {
	
	private String type;
	
	private String status;
	
	private String priority;
	
	private List<RedmineTask> subTasks = new LinkedList<>();
	
	private List<RedmineIssueLink> issueLinks = new LinkedList<>();
	
	private Map<String, RedmineCustomField> customFields = new LinkedHashMap<>();
	

	/**
	 * 
	 */
	public RedmineTask() {
	}

	/**
	 * @param id
	 * @param name
	 * @param type
	 * @param status
	 * @param description
	 * @param progress
	 */
	public RedmineTask(String id, String name, String type, String status, String description, int progress) {
		super(id, name, type, status, description, progress);
	}
	
	

	public RedmineTask(String id, String name, String type, String status, String description, String url,
			int progress) {
		super(id, name, type, status, description, url, progress);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.model.ticketing.TicketingTask#getStatus()
	 */
	@Override
	public String getStatus() {
		return status;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.model.ticketing.TicketingTask#getType()
	 */
	@Override
	public String getType() {
		return type;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.model.ticketing.TicketingTask#getPriority()
	 */
	@Override
	public String getPriority() {
		return priority;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.model.ticketing.TicketingTask#getIssueLinks()
	 */
	@Override
	public List<? extends TicketingField> getIssueLinks() {
		return issueLinks;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.model.ticketing.TicketingTask#getSubTasks()
	 */
	@Override
	public List<? extends TicketingTask> getSubTasks() {
		return subTasks;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.model.ticketing.TicketingTask#getCustomFields()
	 */
	@Override
	public Map<String, ? extends TicketingField> getCustomFields() {
		return customFields;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.model.ticketing.TicketingTask#setStatus(java.lang.String)
	 */
	@Override
	public void setStatus(String status) {
		this.status = status;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.model.ticketing.TicketingTask#setType(java.lang.String)
	 */
	@Override
	public void setType(String type) {
		this.type = type;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.model.ticketing.TicketingTask#setPriority(java.lang.String)
	 */
	@Override
	public void setPriority(String priority) {
		this.priority = priority;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.model.ticketing.TicketingTask#setIssueLinks(java.util.List)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void setIssueLinks(List<? extends TicketingField> issueLinks) {
		this.issueLinks = (List<RedmineIssueLink>) issueLinks;

	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.model.ticketing.TicketingTask#setSubTask(java.util.List)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void setSubTask(List<? extends TicketingTask> subTasks) {
		this.subTasks = (List<RedmineTask>) subTasks;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.model.ticketing.TicketingTask#setCustomFields(java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void setCustomFields(Map<String, ? extends TicketingField> customFields) {
		this.customFields = (Map<String, RedmineCustomField>) customFields;
	}

}
