/**
 * 
 */
package lu.itrust.business.TS.model.ticketing.impl.jira;

import java.util.List;
import java.util.Map;

import lu.itrust.business.TS.model.ticketing.TicketingField;
import lu.itrust.business.TS.model.ticketing.TicketingTask;
import lu.itrust.business.TS.model.ticketing.impl.TaskImpl;

/**
 * @author eomar
 *
 */
public class JiraTask extends TaskImpl {

	private List<JiraTask> subTasks;

	private Map<String, JiraCustomField> customFields;

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.model.ticketing.TicketingTask#getSubTickets()
	 */
	@Override
	public List<JiraTask> getSubTasks() {
		return this.subTasks;
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

	@SuppressWarnings("unchecked")
	@Override
	public void setSubTask(List<? extends TicketingTask> subTasks) {
		this.subTasks = (List<JiraTask>) subTasks;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setCustomFields(Map<String, ? extends TicketingField> customFields) {
		this.customFields = (Map<String, JiraCustomField>) customFields;
	}

}
