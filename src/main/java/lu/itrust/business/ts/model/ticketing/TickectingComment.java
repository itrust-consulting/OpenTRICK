/**
 * 
 */
package lu.itrust.business.ts.model.ticketing;

import java.util.Date;


/**
 * This interface represents a ticketing comment.
 * It extends the TicketingObject interface.
 */
public interface TickectingComment extends TicketingObject {
	
	/**
	 * Gets the creation date of the comment.
	 *
	 * @return the creation date of the comment
	 */
	Date getCreated();
	
	/**
	 * Gets the author of the comment.
	 *
	 * @return the author of the comment
	 */
	String getAuthor();
	
	/**
	 * Sets the creation date of the comment.
	 *
	 * @param created the creation date of the comment
	 */
	void setCreated(Date created);
	
	/**
	 * Sets the author of the comment.
	 *
	 * @param author the author of the comment
	 */
	void setAuthor(String author);
	
	/**
	 * Gets the description of the comment.
	 *
	 * @return the description of the comment
	 */
	String getDescription();
	
	/**
	 * Sets the description of the comment.
	 *
	 * @param description the description of the comment
	 */
	void setDescription(String description);

}
