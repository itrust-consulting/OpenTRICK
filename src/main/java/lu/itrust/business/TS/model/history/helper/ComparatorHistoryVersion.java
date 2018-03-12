/**
 * 
 */
package lu.itrust.business.TS.model.history.helper;

import lu.itrust.business.TS.helper.NaturalOrderComparator;
import lu.itrust.business.TS.model.history.History;

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
