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
	
	private String description;
	
	private int progress;
	
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
	public ProjectImpl(String id, String name, String description, int progress) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.progress = progress;
	}



	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.model.ticketing.TicketingTicket#getDescription()
	 */
	@Override
	public String getDescription() {
		return this.description;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.model.ticketing.TicketingTicket#getProgess()
	 */
	@Override
	public int getProgress() {
		return this.progress;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.model.ticketing.TicketingTicket#setDescription(java.lang.String)
	 */
	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.model.ticketing.TicketingTicket#setProgress(int)
	 */
	@Override
	public void setProgress(int progress) {
		this.progress = progress;
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
