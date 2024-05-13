package lu.itrust.business.ts.model.history.helper;

import lu.itrust.business.ts.helper.NaturalOrderComparator;
import lu.itrust.business.ts.model.history.History;

/**
 * A comparator for comparing two instances of the History class based on their version numbers.
 */
public class ComparatorHistoryVersion implements NaturalOrderComparator<History> {

	/**
	 * Compares two History objects based on their version numbers.
	 *
	 * @param o1 the first History object to compare
	 * @param o2 the second History object to compare
	 * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second
	 */
	@Override
	public int compare(History o1, History o2) {
		return NaturalOrderComparator.compareTo(o1.getVersion(), o2.getVersion());
	}

}
