/**
 * 
 */
package lu.itrust.business.TS.model.ticketing.impl.jira;

import java.util.List;

import lu.itrust.business.TS.model.ticketing.TicketingTask;
import lu.itrust.business.TS.model.ticketing.impl.ProjectImpl;

/**
 * @author eomar
 *
 */
public class JiraProject extends ProjectImpl {

	private List<JiraTask> tasks;
	
	/**
	 * 
	 */
	public JiraProject() {
	}

	/**
	 * @param id
	 * @param name
	 * @param description
	 * @param progress
	 */
	public JiraProject(String id, String name) {
		super(id, name);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.model.ticketing.TicketingProject#getTasks()
	 */
	@Override
	public List<JiraTask> getTasks() {
		return tasks;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.model.ticketing.TicketingProject#setTasks(java.util.List)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void setTasks(List<? extends TicketingTask> tasks) {
		this.tasks = (List<JiraTask>) tasks;
	}

}
