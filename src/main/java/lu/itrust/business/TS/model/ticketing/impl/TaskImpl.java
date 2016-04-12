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

	private String assignee;

	private Date created;
	
	private Date due;
	
	private String description;

	private String id;
	
	private String name;
	
	private int progress;
	
	private String reporter;

	private Date updated;

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

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.model.ticketing.TicketingTask#getAssignee()
	 */
	@Override
	public String getAssignee() {
		return this.assignee;
	}

	/**
	 * @return the created
	 */
	@Override
	public Date getCreated() {
		return created;
	}

	/**
	 * @return the due
	 */
	@Override
	public Date getDue() {
		return due;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.model.ticketing.TicketingObject#getDescription()
	 */
	@Override
	public String getDescription() {
		return this.description;
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
	 * @see lu.itrust.business.TS.model.ticketing.TicketingObject#getProgess()
	 */
	@Override
	public int getProgress() {
		return this.progress;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.model.ticketing.TicketingTask#getReporter()
	 */
	@Override
	public String getReporter() {
		return this.reporter;
	}

	/**
	 * @return the updated
	 */
	@Override
	public Date getUpdated() {
		return updated;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.model.ticketing.TicketingTask#setAssignee(java.lang.String)
	 */
	@Override
	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}

	/**
	 * @param created the created to set
	 */
	@Override
	public void setCreated(Date created) {
		this.created = created;
	}

	/**
	 * @param due the due to set
	 */
	@Override
	public void setDue(Date deadline) {
		this.due = deadline;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.model.ticketing.TicketingObject#setDescription(java
	 * .lang.String)
	 */
	@Override
	public void setDescription(String description) {
		this.description = description;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.model.ticketing.TicketingObject#setProgress(int)
	 */
	@Override
	public void setProgress(int progress) {
		this.progress = progress;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.model.ticketing.TicketingTask#setReporter(java.lang.String)
	 */
	@Override
	public void setReporter(String reporter) {
		this.reporter = reporter;
	}

	/**
	 * @param updated the updated to set
	 */
	@Override
	public void setUpdated(Date lastUpdated) {
		this.updated = lastUpdated;
	}

}
