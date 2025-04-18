/**
 * 
 */
package lu.itrust.business.ts.model.ticketing.impl.jira;

import java.util.List;

import lu.itrust.business.ts.model.ticketing.TicketingTask;
import lu.itrust.business.ts.model.ticketing.impl.AbstractProject;


/**
 * Represents a Jira project.
 */
public class JiraProject extends AbstractProject {

	private List<JiraTask> tasks;
	
	/**
	 * Constructs a new JiraProject object.
	 */
	public JiraProject() {
	}

	/**
	 * Constructs a new JiraProject object with the specified id and name.
	 * 
	 * @param id   the id of the project
	 * @param name the name of the project
	 */
	public JiraProject(String id, String name) {
		super(id, name);
	}

	/**
	 * Returns the list of tasks associated with this project.
	 * 
	 * @return the list of tasks
	 */
	@Override
	public List<JiraTask> getTasks() {
		return tasks;
	}

	/**
	 * Sets the list of tasks associated with this project.
	 * 
	 * @param tasks the list of tasks
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void setTasks(List<? extends TicketingTask> tasks) {
		this.tasks = (List<JiraTask>) tasks;
	}

}
