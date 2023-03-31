package lu.itrust.business.ts.model.general.helper;

import java.io.Serializable;

public class TrickFilter implements Serializable{

	/**
	 * 
	 */
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

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		if (!CheckDirection(direction))
			throw new IllegalArgumentException();
		this.direction = direction;
	}

	protected boolean checkDirection() {
		return CheckDirection(direction);
	}

	protected static boolean CheckDirection(String direction) {
		return direction != null && direction.matches(REG_SORT_DIRCTION);
	}

}