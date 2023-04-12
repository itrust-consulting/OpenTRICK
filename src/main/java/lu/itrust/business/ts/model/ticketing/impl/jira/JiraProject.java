/**
 * 
 */
package lu.itrust.business.ts.model.ticketing.impl.jira;

import java.util.List;

import lu.itrust.business.ts.model.ticketing.TicketingTask;
import lu.itrust.business.ts.model.ticketing.impl.AbstractProject;

/**
 * @author eomar
 *
 */
public class JiraProject extends AbstractProject {

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
	 * @see lu.itrust.business.ts.model.ticketing.TicketingProject#getTasks()
	 */
	@Override
	public List<JiraTask> getTasks() {
		return tasks;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.model.ticketing.TicketingProject#setTasks(java.util.List)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void setTasks(List<? extends TicketingTask> tasks) {
		this.tasks = (List<JiraTask>) tasks;
	}

}
