/**
 * 
 */
package lu.itrust.business.ts.model.ticketing.impl.redmine;

import java.util.LinkedList;
import java.util.List;

import lu.itrust.business.ts.model.ticketing.TicketingTask;
import lu.itrust.business.ts.model.ticketing.impl.AbstractProject;

/**
 * Represents a Redmine project.
 */
public class RedmineProject extends AbstractProject {
	
	private String description;
	
	private List<RedmineTask> tasks = new LinkedList<>();

	/**
	 * Constructs a new RedmineProject object.
	 */
	public RedmineProject() {
	}

	/**
	 * Constructs a new RedmineProject object with the specified id, name, and description.
	 * 
	 * @param id the project id
	 * @param name the project name
	 * @param description the project description
	 */
	public RedmineProject(String id, String name, String description) {
		super(id, name);
	}

	/**
	 * Returns the list of tasks associated with this project.
	 * 
	 * @return the list of tasks
	 */
	@Override
	public List<? extends TicketingTask> getTasks() {
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
		this.tasks = (List<RedmineTask>) tasks; 
	}

	/**
	 * Returns the description of this project.
	 * 
	 * @return the project description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description of this project.
	 * 
	 * @param description the project description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

}
