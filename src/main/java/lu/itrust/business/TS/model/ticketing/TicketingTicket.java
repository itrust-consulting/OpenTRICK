/**
 * 
 */
package lu.itrust.business.TS.model.ticketing;

/**
 * @author eomar
 *
 */
public interface TicketingTicket extends TicketingBase {

	String getDescription();

	int getProgress();

	void setDescription(String description);

	void setProgress(int progress);
}
