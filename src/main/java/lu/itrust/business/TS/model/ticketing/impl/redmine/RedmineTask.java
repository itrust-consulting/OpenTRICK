/**
 * 
 */
package lu.itrust.business.TS.model.ticketing.impl.redmine;

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

	/**
	 * 
	 */
	public RedmineTask() {
		// TODO Auto-generated constructor stub
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
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.model.ticketing.TicketingTask#getStatus()
	 */
	@Override
	public String getStatus() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.model.ticketing.TicketingTask#getType()
	 */
	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.model.ticketing.TicketingTask#getPriority()
	 */
	@Override
	public String getPriority() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.model.ticketing.TicketingTask#getIssueLinks()
	 */
	@Override
	public List<? extends TicketingField> getIssueLinks() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.model.ticketing.TicketingTask#getSubTasks()
	 */
	@Override
	public List<? extends TicketingTask> getSubTasks() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.model.ticketing.TicketingTask#getCustomFields()
	 */
	@Override
	public Map<String, ? extends TicketingField> getCustomFields() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.model.ticketing.TicketingTask#setStatus(java.lang.String)
	 */
	@Override
	public void setStatus(String status) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.model.ticketing.TicketingTask#setType(java.lang.String)
	 */
	@Override
	public void setType(String type) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.model.ticketing.TicketingTask#setPriority(java.lang.String)
	 */
	@Override
	public void setPriority(String priority) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.model.ticketing.TicketingTask#setIssueLinks(java.util.List)
	 */
	@Override
	public void setIssueLinks(List<? extends TicketingField> issueLinks) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.model.ticketing.TicketingTask#setSubTask(java.util.List)
	 */
	@Override
	public void setSubTask(List<? extends TicketingTask> subTasks) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.model.ticketing.TicketingTask#setCustomFields(java.util.Map)
	 */
	@Override
	public void setCustomFields(Map<String, ? extends TicketingField> customFields) {
		// TODO Auto-generated method stub

	}

}
