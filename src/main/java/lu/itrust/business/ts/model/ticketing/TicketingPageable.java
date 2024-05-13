/**
 * 
 */
package lu.itrust.business.ts.model.ticketing;

import java.util.List;


/**
 * This interface represents a pageable list of elements for ticketing purposes.
 * It extends the List interface and provides additional methods for pagination.
 *
 * @param <E> the type of elements in the pageable list
 */
public interface TicketingPageable<E> extends List<E> {

	/**
	 * Returns the content of the pageable list.
	 *
	 * @return the content of the pageable list
	 */
	List<E> getContent();

	/**
	 * Returns the maximum size of each page in the pageable list.
	 *
	 * @return the maximum size of each page
	 */
	int getMaxSize();

	/**
	 * Returns the offset of the current page in the pageable list.
	 *
	 * @return the offset of the current page
	 */
	int getOffset();

	/**
	 * Returns the offset of the next page in the pageable list.
	 *
	 * @return the offset of the next page
	 */
	default int getNextOffset() {
		return getOffset() + getMaxSize();
	}

	/**
	 * Increases the offset by the specified amount.
	 *
	 * @param offset the amount to increase the offset by
	 * @return the new offset value
	 */
	int increase(int offset);

	/**
	 * Moves to the next page in the pageable list.
	 *
	 * @return the new offset value after moving to the next page
	 */
	int moveNext();
}
