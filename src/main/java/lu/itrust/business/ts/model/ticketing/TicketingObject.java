package lu.itrust.business.ts.model.ticketing;

/**
 * The TicketingObject interface represents an object that can be used in ticketing systems.
 * It provides methods to get and set the object's ID.
 */
public interface TicketingObject {

	/**
	 * Gets the ID of the ticketing object.
	 *
	 * @return the ID of the ticketing object
	 */
	String getId();

	/**
	 * Sets the ID of the ticketing object.
	 *
	 * @param id the ID to set
	 */
	void setId(String id);

}