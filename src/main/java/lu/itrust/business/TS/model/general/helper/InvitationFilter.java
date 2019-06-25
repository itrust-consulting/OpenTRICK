/**
 * 
 */
package lu.itrust.business.TS.model.general.helper;

import java.util.regex.Pattern;

/**
 * @author eomar
 *
 */
public class InvitationFilter extends FilterControl {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String REG_SORT = "analysis\\.identifier|analysis\\.label|analysis\\.version|host\\.email|host\\.firstName|host\\.lastName";

	private static final Pattern PATTERN = Pattern.compile(REG_SORT);

	/**
	 * 
	 */
	public InvitationFilter() {
		setSort("analysis.identifier");
	}

	/**
	 * @param sort
	 * @param direction
	 * @param size
	 * @param filter
	 */
	public InvitationFilter(String sort, String direction, int size) {
		super(sort, direction, size, null);
	}

	@Override
	protected boolean ckeckSort(String sort) {
		return sort != null && PATTERN.matcher(sort).matches();
	}

	public static String[] SORTS() {
		return REG_SORT.replaceAll("\\\\", "").split("\\|");
	}

}
