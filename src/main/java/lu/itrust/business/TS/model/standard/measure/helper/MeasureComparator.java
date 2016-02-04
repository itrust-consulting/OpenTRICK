/**
 * 
 */
package lu.itrust.business.TS.model.standard.measure.helper;

import java.util.Comparator;

import lu.itrust.business.TS.model.standard.measure.Measure;

/**
 * @author eomar
 *
 */
public class MeasureComparator implements Comparator<Measure> {

	@Override
	public int compare(Measure o1, Measure o2) {
		if (o1.getSortIndex() == null)
			o1.generateSortId();
		if (o2.getSortIndex() == null)
			o2.generateSortId();
		return o1.compareTo(o2);
	}

}
