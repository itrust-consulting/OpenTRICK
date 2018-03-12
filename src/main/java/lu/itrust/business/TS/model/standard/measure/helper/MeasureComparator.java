/**
 * 
 */
package lu.itrust.business.TS.model.standard.measure.helper;

import lu.itrust.business.TS.helper.NaturalOrderComparator;
import lu.itrust.business.TS.model.standard.measure.Measure;

/**
 * @author eomar
 *
 */
public class MeasureComparator implements NaturalOrderComparator<Measure> {

	@Override
	public int compare(Measure o1, Measure o2) {
		int standard = NaturalOrderComparator.compareTo(o1.getMeasureDescription().getStandard().getLabel(), o2.getMeasureDescription().getStandard().getLabel());
		return standard == 0 ? NaturalOrderComparator.compareTo(o1.getMeasureDescription().getReference(), o2.getMeasureDescription().getReference()) : standard;
	}

}
