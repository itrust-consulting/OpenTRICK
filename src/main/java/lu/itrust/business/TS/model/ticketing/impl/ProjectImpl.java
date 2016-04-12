/**
 * 
 */
package lu.itrust.business.TS.model.ticketing.impl;

import lu.itrust.business.TS.model.ticketing.TicketingProject;

/**
 * @author eomar
 *
 */
public abstract class ProjectImpl implements TicketingProject {
	
	private String id;
	
	private String name;
	
	/**
	 * 
	 */
	public ProjectImpl() {
	}
	
	/**
	 * @param id
	 * @param name
	 * @param description
	 * @param progress
	 */
	public ProjectImpl(String id, String name) {
		this.id = id;
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.model.ticketing.TicketingBase#getId()
	 */
	@Override
	public String getId() {
		return this.id;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.model.ticketing.TicketingBase#getName()
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.model.ticketing.TicketingBase#setId(java.lang.String)
	 */
	@Override
	public void setId(String id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.model.ticketing.TicketingBase#setName(java.lang.String)
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}
}
