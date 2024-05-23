/**
 * 
 */
package lu.itrust.business.ts.model.ticketing;


/**
 * The TicketingBase interface represents the base functionality for a ticketing object.
 * It extends the TicketingObject interface.
 */
public interface TicketingBase extends TicketingObject {

	/**
	 * Gets the name of the ticketing object.
	 *
	 * @return the name of the ticketing object
	 */
	String getName();

	/**
	 * Sets the name of the ticketing object.
	 *
	 * @param name the name to set
	 */
	void setName(String name);
}
