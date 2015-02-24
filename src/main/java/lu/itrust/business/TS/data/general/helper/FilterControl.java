/**
 * 
 */
package lu.itrust.business.TS.data.general.helper;

/**
 * @author eomar
 *
 */
public class FilterControl {

	private static final String REG_SORT_DIRCTION = "asc|desc";

	private static final String REG_SORT = "identifier|label|size|version|created|exportTime";

	private String sort = "identifier";

	private String direction = "asc";

	private int size = 30;

	private String filter = "ALL";

	/**
	 * 
	 */
	public FilterControl() {
	}

	/**
	 * @param sort
	 * @param size
	 * @param filter
	 */
	public FilterControl(String sort, String direction, int size, String filter) {
		setSort(sort);
		setDirection(direction);
		setSize(size);
		setFilter(filter);
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		if (sort == null || !sort.matches(REG_SORT))
			throw new IllegalArgumentException();
		this.sort = sort;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		if (direction == null || !direction.matches(REG_SORT_DIRCTION))
			throw new IllegalArgumentException();
		this.direction = direction;
	}

	public boolean validate() {
		if (sort == null || !sort.matches(REG_SORT))
			return false;
		if (direction == null || !direction.matches(REG_SORT_DIRCTION))
			return false;
		return true;
	}
}
