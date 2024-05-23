package lu.itrust.business.ts.model.general.helper;

import java.io.Serializable;

/**
 * The TrickFilter class represents a filter used for sorting and pagination in the TrickService.
 * It allows specifying the sorting direction and the number of results to be returned.
 */
public class TrickFilter implements Serializable{

	private static final long serialVersionUID = 1L;

	private static final String REG_SORT_DIRCTION = "asc|desc";
	
	private String direction = "asc";
	
	private int size = 30;
	
	/**
	 * 
	 */
	public TrickFilter() {
	}
	

	/**
	 * @param direction
	 * @param size
	 */
	public TrickFilter(String direction, int size) {
		setSize(size);
		setDirection(direction);
	}

	/**
	 * Returns the size of the TrickFilter.
	 *
	 * @return the size of the TrickFilter
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Sets the size of the TrickFilter.
	 *
	 * @param size the size to set
	 */
	public void setSize(int size) {
		this.size = size;
	}

	/**
	 * Returns the direction of the trick.
	 *
	 * @return the direction of the trick as a String.
	 */
	public String getDirection() {
		return direction;
	}

	/**
	 * Sets the direction for the trick filter.
	 * 
	 * @param direction the direction to set
	 * @throws IllegalArgumentException if the direction is invalid
	 */
	public void setDirection(String direction) {
		if (!CheckDirection(direction))
			throw new IllegalArgumentException();
		this.direction = direction;
	}

	/**
	 * Checks the direction of the trick.
	 *
	 * @return true if the direction is valid, false otherwise.
	 */
	protected boolean checkDirection() {
		return CheckDirection(direction);
	}

	/**
	 * Checks if the given direction is valid.
	 *
	 * @param direction the direction to be checked
	 * @return true if the direction is valid, false otherwise
	 */
	protected static boolean CheckDirection(String direction) {
		return direction != null && direction.matches(REG_SORT_DIRCTION);
	}

}