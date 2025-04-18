/**
 * 
 */
package lu.itrust.business.ts.model.ticketing.impl;

import java.util.Date;

import lu.itrust.business.ts.model.ticketing.TickectingComment;

/**
 * Represents a comment in the ticketing system.
 */
public class Comment implements TickectingComment {
	
	private String author;
	private Date created;
	private String description;
	private String id;
	
	/**
	 * Default constructor.
	 */
	public Comment() {
	}

	/**
	 * Constructs a comment with the specified id, author, created date, and description.
	 * 
	 * @param id          the unique identifier of the comment
	 * @param author      the author of the comment
	 * @param created     the date and time when the comment was created
	 * @param description the description of the comment
	 */
	public Comment(String id, String author, Date created, String description) {
		this.id = id;
		this.author = author;
		this.created = created;
		this.description = description;
	}

	/**
	 * Retrieves the author of the comment.
	 * 
	 * @return the author of the comment
	 */
	@Override
	public String getAuthor() {
		return this.author;
	}

	/**
	 * Retrieves the date and time when the comment was created.
	 * 
	 * @return the date and time when the comment was created
	 */
	@Override
	public Date getCreated() {
		return created;
	}

	/**
	 * Retrieves the description of the comment.
	 * 
	 * @return the description of the comment
	 */
	@Override
	public String getDescription() {
		return this.description;
	}

	/**
	 * Retrieves the unique identifier of the comment.
	 * 
	 * @return the unique identifier of the comment
	 */
	@Override
	public String getId() {
		return id;
	}

	/**
	 * Sets the author of the comment.
	 * 
	 * @param author the author of the comment
	 */
	@Override
	public void setAuthor(String author) {
		this.author = author;
	}

	/**
	 * Sets the date and time when the comment was created.
	 * 
	 * @param created the date and time when the comment was created
	 */
	@Override
	public void setCreated(Date created) {
		this.created = created;
	}

	/**
	 * Sets the description of the comment.
	 * 
	 * @param description the description of the comment
	 */
	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Sets the unique identifier of the comment.
	 * 
	 * @param id the unique identifier of the comment
	 */
	@Override
	public void setId(String id) {
		this.id = id;
	}

}
