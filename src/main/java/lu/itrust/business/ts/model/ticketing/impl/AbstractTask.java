/**
 * 
 */
package lu.itrust.business.ts.model.ticketing.impl;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import lu.itrust.business.ts.model.ticketing.TickectingComment;
import lu.itrust.business.ts.model.ticketing.TicketingTask;


/**
 * The AbstractTask class is an abstract implementation of the TicketingTask interface.
 * It provides common properties and methods for all types of tasks in the ticketing system.
 */
public abstract class AbstractTask implements TicketingTask {

	private String url;

	private String assignee;

	private Date created;

	private Date due;

	private String description;

	private String id;

	private String name;

	private int progress;

	private String reporter;

	private Date updated;

	private List<Comment> comments = new LinkedList<>();

	/**
	 * 
	 */
	public AbstractTask() {
	}

	/**
	 * @param id
	 * @param name
	 * @param type
	 * @param status
	 * @param description
	 * @param progress
	 */
	public AbstractTask(String id, String name, String type, String status, String description, int progress) {
		this(id, name, type, status, description, null, progress);
	}

	/**
	 * @param id
	 * @param name
	 * @param type
	 * @param status
	 * @param description
	 * @param progress
	 */
	public AbstractTask(String id, String name, String type, String status, String description, String url,
			int progress) {
		this.setId(id);
		this.name = name;
		this.description = description;
		this.progress = progress;
		setType(type);
		setStatus(status);
		setUrl(url);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.model.ticketing.TicketingTask#getAssignee()
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
	 * @see lu.itrust.business.ts.model.ticketing.TicketingObject#getDescription()
	 */
	@Override
	public String getDescription() {
		return this.description;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.model.ticketing.TicketingBase#getId()
	 */
	@Override
	public String getId() {
		return this.id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.model.ticketing.TicketingBase#getName()
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.model.ticketing.TicketingObject#getProgess()
	 */
	@Override
	public int getProgress() {
		return this.progress;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.model.ticketing.TicketingTask#getReporter()
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.model.ticketing.TicketingTask#setAssignee(java.lang
	 * .String)
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
	 * lu.itrust.business.ts.model.ticketing.TicketingObject#setDescription(java
	 * .lang.String)
	 */
	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.model.ticketing.TicketingBase#setId(java.lang.
	 * String)
	 */
	@Override
	public void setId(String id) {
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.model.ticketing.TicketingBase#setName(java.lang.
	 * String)
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.model.ticketing.TicketingObject#setProgress(int)
	 */
	@Override
	public void setProgress(int progress) {
		this.progress = progress;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.model.ticketing.TicketingTask#setReporter(java.lang
	 * .String)
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.model.ticketing.TicketingTask#getComments()
	 */
	@Override
	public List<Comment> getComments() {
		return this.comments;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.model.ticketing.TicketingTask#setComments(java.util.
	 * List)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void setComments(List<? extends TickectingComment> comments) {
		this.comments = (List<Comment>) comments;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.model.ticketing.TicketingTask#getUrl()
	 */
	@Override
	public String getUrl() {
		return url;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.model.ticketing.TicketingTask#setUrl(java.lang.String)
	 */
	@Override
	public void setUrl(String url) {
		this.url = url;
	}

}
