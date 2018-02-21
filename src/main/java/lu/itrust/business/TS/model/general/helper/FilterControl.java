/**
 * 
 */
package lu.itrust.business.TS.model.general.helper;

/**
 * @author eomar
 *
 */
public class FilterControl extends TrickFilter {

	private static final String REG_SORT = "identifier|label|size|version|created";

	private String sort = "identifier";

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
		super(direction, size);
		setSort(sort);
		setFilter(filter);
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		if (!ckeckSort(sort))
			throw new IllegalArgumentException();
		this.sort = sort;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		if(filter == null)
			filter = "ALL";
		this.filter = filter;
	}

	protected boolean ckeckSort() {
		return ckeckSort(sort);
	}

	protected boolean ckeckSort(String sort) {
		return sort != null && sort.matches(REG_SORT);
	}

	public boolean validate() {
		return ckeckSort() && checkDirection();
	}
}
