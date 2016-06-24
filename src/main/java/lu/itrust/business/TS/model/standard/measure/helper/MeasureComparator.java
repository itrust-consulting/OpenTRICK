/**
 * 
 */
package lu.itrust.business.TS.model.standard.measure.helper;

import lu.itrust.business.TS.component.NaturalOrderComparator;
import lu.itrust.business.TS.model.standard.measure.Measure;

/**
 * @author eomar
 *
 */
public class MeasureComparator implements NaturalOrderComparator<Measure> {

	@Override
	public int compare(Measure o1, Measure o2) {
		int standard = o1.getMeasureDescription().getStandard().getLabel().compareToIgnoreCase(o2.getMeasureDescription().getStandard().getLabel());
		return standard == 0? compareTo(o1.getMeasureDescription().getReference(), o2.getMeasureDescription().getReference()) : standard;
	}

}
