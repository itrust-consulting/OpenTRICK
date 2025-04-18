package lu.itrust.business.ts.model.general.helper;

import java.util.regex.Pattern;

/**
 * This class represents a filter for invitations. It extends the FilterControl class.
 */
public class InvitationFilter extends FilterControl {

	/**
	 * The serial version UID for serialization.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The regular expression for sorting options.
	 */
	private static final String REG_SORT = "analysis\\.identifier|analysis\\.label|analysis\\.version|host\\.email|host\\.firstName|host\\.lastName";

	/**
	 * The pattern for matching the sorting options.
	 */
	private static final Pattern PATTERN = Pattern.compile(REG_SORT);

	/**
	 * Constructs a new InvitationFilter object with the default sort option.
	 */
	public InvitationFilter() {
		setSort("analysis.identifier");
	}

	/**
	 * Constructs a new InvitationFilter object with the specified sort, direction, and size.
	 *
	 * @param sort      the sort option
	 * @param direction the sort direction
	 * @param size      the number of results to return
	 */
	public InvitationFilter(String sort, String direction, int size) {
		super(sort, direction, size, null);
	}

	/**
	 * Checks if the given sort option is valid.
	 *
	 * @param sort the sort option to check
	 * @return true if the sort option is valid, false otherwise
	 */
	@Override
	protected boolean checkSort(String sort) {
		return sort != null && PATTERN.matcher(sort).matches();
	}

	/**
	 * Returns an array of valid sort options.
	 *
	 * @return an array of valid sort options
	 */
	public static String[] SORTS() {
		return REG_SORT.replaceAll("\\\\", "").split("\\|");
	}

}
