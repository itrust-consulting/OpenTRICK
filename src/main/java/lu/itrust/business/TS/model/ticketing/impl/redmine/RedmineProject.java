/**
 * 
 */
package lu.itrust.business.TS.model.ticketing.impl.redmine;

import java.util.LinkedList;
import java.util.List;

import lu.itrust.business.TS.model.ticketing.TicketingTask;
import lu.itrust.business.TS.model.ticketing.impl.AbstractProject;

/**
 * @author eomar
 *
 */
public class RedmineProject extends AbstractProject {
	
	private String description;
	
	private List<RedmineTask> tasks = new LinkedList<>();

	/**
	 * 
	 */
	public RedmineProject() {
	}

	/**
	 * @param id
	 * @param name
	 * @param description 
	 */
	public RedmineProject(String id, String name, String description) {
		super(id, name);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.model.ticketing.TicketingProject#getTasks()
	 */
	@Override
	public List<? extends TicketingTask> getTasks() {
		return tasks;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.model.ticketing.TicketingProject#setTasks(java.util.List)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void setTasks(List<? extends TicketingTask> tasks) {
		this.tasks = (List<RedmineTask>) tasks; 
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
