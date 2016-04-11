/**
 * 
 */
package lu.itrust.business.TS.model.ticketing.impl;

import java.util.Date;

import lu.itrust.business.TS.model.ticketing.TicketingTask;

/**
 * @author eomar
 *
 */
public abstract class TaskImpl implements TicketingTask {

	private String id;

	private String name;

	private String description;
	
	private Date created;
	
	private Date lastUpdated;
	
	private Date deadline;

	private int progress;

	/**
	 * 
	 */
	public TaskImpl() {
	}

	/**
	 * @param id
	 * @param name
	 * @param description
	 * @param progress
	 */
	public TaskImpl(String id, String name, String description, int progress) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.progress = progress;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.model.ticketing.TicketingTicket#getDescription()
	 */
	@Override
	public String getDescription() {
		return this.description;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.model.ticketing.TicketingTicket#getProgess()
	 */
	@Override
	public int getProgress() {
		return this.progress;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.model.ticketing.TicketingTicket#setDescription(java
	 * .lang.String)
	 */
	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.model.ticketing.TicketingTicket#setProgress(int)
	 */
	@Override
	public void setProgress(int progress) {
		this.progress = progress;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.model.ticketing.TicketingBase#getId()
	 */
	@Override
	public String getId() {
		return this.id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.model.ticketing.TicketingBase#getName()
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.model.ticketing.TicketingBase#setId(java.lang.
	 * String)
	 */
	@Override
	public void setId(String id) {
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.model.ticketing.TicketingBase#setName(java.lang.
	 * String)
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the created
	 */
	@Override
	public Date getCreated() {
		return created;
	}

	/**
	 * @param created the created to set
	 */
	@Override
	public void setCreated(Date created) {
		this.created = created;
	}

	/**
	 * @return the lastUpdated
	 */
	@Override
	public Date getLastUpdated() {
		return lastUpdated;
	}

	/**
	 * @param lastUpdated the lastUpdated to set
	 */
	@Override
	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	/**
	 * @return the deadline
	 */
	@Override
	public Date getDeadline() {
		return deadline;
	}

	/**
	 * @param deadline the deadline to set
	 */
	@Override
	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}

	

}
