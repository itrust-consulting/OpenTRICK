/**
 * 
 */
package lu.itrust.business.TS.model.ticketing;

import java.util.List;

/**
 * @author eomar
 *
 */
public interface TicketingPageable<E> extends List<E> {

	List<E> getContent();

	int getMaxSize();

	int getOffset();

	default int getNextOffset() {
		return getOffset() + getMaxSize();
	}
	
	int moveNext();
}
