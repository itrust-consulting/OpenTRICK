/**
 * 
 */
package lu.itrust.business.ts.model.history.helper;

import lu.itrust.business.ts.helper.NaturalOrderComparator;
import lu.itrust.business.ts.model.history.History;

/**
 * @author eomar
 *
 */
public class ComparatorHistoryVersion implements NaturalOrderComparator<History> {

	@Override
	public int compare(History o1, History o2) {
		return NaturalOrderComparator.compareTo(o1.getVersion(), o2.getVersion());
	}

}
