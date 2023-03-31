/**
 * 
 */
package lu.itrust.business.ts.model.ticketing.impl;

import java.util.Date;

import lu.itrust.business.ts.model.ticketing.TickectingComment;

/**
 * @author eomar
 *
 */
public class Comment implements TickectingComment {
	
	private String author;
	
	private Date created;
	
	private String description;
	
	private String id;
	

	/**
	 * 
	 */
	public Comment() {
	}

	/**
	 * @param id
	 * @param author
	 * @param created
	 * @param description
	 */
	public Comment(String id, String author, Date created, String description) {
		this.id = id;
		this.author = author;
		this.created = created;
		this.description = description;
	}



	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.model.ticketing.TickectingComment#getAuthor()
	 */
	@Override
	public String getAuthor() {
		return this.author;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.model.ticketing.TickectingComment#getCreated()
	 */
	@Override
	public Date getCreated() {
		return created;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.model.ticketing.TickectingComment#getDescription()
	 */
	@Override
	public String getDescription() {
		return this.description;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.model.ticketing.TicketingObject#getId()
	 */
	@Override
	public String getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.model.ticketing.TickectingComment#setAuthor(java.lang.String)
	 */
	@Override
	public void setAuthor(String author) {
		this.author = author;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.model.ticketing.TickectingComment#setCreated(java.util.Date)
	 */
	@Override
	public void setCreated(Date created) {
		this.created = created;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.model.ticketing.TickectingComment#setDescription(java.lang.String)
	 */
	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.model.ticketing.TicketingObject#setId(java.lang.String)
	 */
	@Override
	public void setId(String id) {
		this.id = id;
	}

}
