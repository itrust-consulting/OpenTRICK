/**
 * 
 */
package lu.itrust.business.ts.model.general.helper;

/**
 * This class represents a filter control used for filtering and sorting data.
 * It extends the TrickFilter class.
 */
public class FilterControl extends TrickFilter {

	/**
	 * The serial version UID for serialization.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The regular expression pattern for valid sort values.
	 */
	private static final String REG_SORT = "identifier|label|name|length|version|created";

	/**
	 * The default sort value.
	 */
	private String sort = "identifier";

	/**
	 * The filter value.
	 */
	private String filter = "ALL";

	/**
	 * Default constructor for FilterControl.
	 */
	public FilterControl() {
	}

	/**
	 * Constructor for FilterControl with sort, direction, size, and filter parameters.
	 *
	 * @param sort      The sort value.
	 * @param direction The direction value.
	 * @param size      The size value.
	 * @param filter    The filter value.
	 */
	public FilterControl(String sort, String direction, int size, String filter) {
		super(direction, size);
		setSort(sort);
		setFilter(filter);
	}

	/**
	 * Get the current sort value.
	 *
	 * @return The sort value.
	 */
	public String getSort() {
		return sort;
	}

	/**
	 * Set the sort value.
	 *
	 * @param sort The sort value to set.
	 * @throws IllegalArgumentException if the sort value is invalid.
	 */
	public void setSort(String sort) {
		if (!checkSort(sort))
			throw new IllegalArgumentException("Invalid sort value");
		this.sort = sort;
	}

	/**
	 * Get the current filter value.
	 *
	 * @return The filter value.
	 */
	public String getFilter() {
		return filter;
	}

	/**
	 * Set the filter value.
	 *
	 * @param filter The filter value to set.
	 */
	public void setFilter(String filter) {
		if (filter == null)
			filter = "ALL";
		this.filter = filter;
	}

	/**
	 * Check if the current sort value is valid.
	 *
	 * @return true if the sort value is valid, false otherwise.
	 */
	protected boolean checkSort() {
		return checkSort(sort);
	}

	/**
	 * Check if the given sort value is valid.
	 *
	 * @param sort The sort value to check.
	 * @return true if the sort value is valid, false otherwise.
	 */
	protected boolean checkSort(String sort) {
		return sort != null && sort.matches(REG_SORT);
	}

	/**
	 * Validate the filter control.
	 *
	 * @return true if the filter control is valid, false otherwise.
	 */
	public boolean validate() {
		return checkSort() && checkDirection();
	}
}
