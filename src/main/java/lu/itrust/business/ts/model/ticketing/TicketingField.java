/**
 * 
 */
package lu.itrust.business.ts.model.ticketing;


/**
 * The TicketingField interface represents a field in a ticketing system.
 * It extends the TicketingBase interface.
 */
public interface TicketingField extends TicketingBase {

	/**
	 * Gets the value of the field.
	 *
	 * @return the value of the field
	 */
	Object getValue();

	/**
	 * Sets the value of the field.
	 *
	 * @param value the value to be set
	 */
	void setValue(Object value);

}
