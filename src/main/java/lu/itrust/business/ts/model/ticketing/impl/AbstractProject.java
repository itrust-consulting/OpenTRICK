/**
 * 
 */
package lu.itrust.business.ts.model.ticketing.impl;

import lu.itrust.business.ts.model.ticketing.TicketingProject;


/**
 * This class represents an abstract implementation of a ticketing project.
 * It provides basic functionality for managing project details such as id and name.
 */
public abstract class AbstractProject implements TicketingProject {
	
	private String id;
	
	private String name;
	
	/**
	 * Default constructor for the AbstractProject class.
	 */
	public AbstractProject() {
	}
	
	/**
	 * Constructor for the AbstractProject class with id and name parameters.
	 * 
	 * @param id   the unique identifier of the project
	 * @param name the name of the project
	 */
	public AbstractProject(String id, String name) {
		this.id = id;
		this.name = name;
	}

	/**
	 * Retrieves the id of the project.
	 * 
	 * @return the id of the project
	 */
	@Override
	public String getId() {
		return this.id;
	}

	/**
	 * Retrieves the name of the project.
	 * 
	 * @return the name of the project
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * Sets the id of the project.
	 * 
	 * @param id the id to set for the project
	 */
	@Override
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Sets the name of the project.
	 * 
	 * @param name the name to set for the project
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}
}
