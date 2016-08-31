/**
 * 
 */
package lu.itrust.business.TS.model.ticketing;

import java.util.Date;

/**
 * @author eomar
 *
 */
public interface TickectingComment extends TicketingObject {
	
	Date getCreated();
	
	String getAuthor();
	
	void setCreated(Date created);
	
	void setAuthor(String author);
	
	String getDescription();
	
	void setDescription(String description);

}
