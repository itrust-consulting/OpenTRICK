/**
 * 
 */
package lu.itrust.business.ts.model.ticketing.impl;

import lu.itrust.business.ts.model.ticketing.TicketingProject;

/**
 * @author eomar
 *
 */
public abstract class AbstractProject implements TicketingProject {
	
	private String id;
	
	private String name;
	
	/**
	 * 
	 */
	public AbstractProject() {
	}
	
	/**
	 * @param id
	 * @param name
	 * @param description
	 * @param progress
	 */
	public AbstractProject(String id, String name) {
		this.id = id;
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.model.ticketing.TicketingBase#getId()
	 */
	@Override
	public String getId() {
		return this.id;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.model.ticketing.TicketingBase#getName()
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.model.ticketing.TicketingBase#setId(java.lang.String)
	 */
	@Override
	public void setId(String id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.model.ticketing.TicketingBase#setName(java.lang.String)
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}
}
