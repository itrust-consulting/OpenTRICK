/**
 * 
 */
package lu.itrust.business.component;

import java.util.Comparator;

import lu.itrust.business.TS.History;

/**
 * @author eomar
 *
 */
public class ComparatorHistoryVersion implements Comparator<History> {

	@Override
	public int compare(History o1, History o2) {
		return ComparatorMeasureDescription.Compare(o1.getVersion(), o2.getVersion());
	}

}
