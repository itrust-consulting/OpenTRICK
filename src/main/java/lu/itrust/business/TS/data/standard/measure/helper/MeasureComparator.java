/**
 * 
 */
package lu.itrust.business.TS.data.standard.measure.helper;

import java.util.Comparator;

import lu.itrust.business.TS.data.standard.measure.Measure;

/**
 * @author eomar
 *
 */
public class MeasureComparator implements Comparator<Measure> {

	@Override
	public int compare(Measure o1, Measure o2) {
		return Measure.compare(o1.getMeasureDescription().getReference(), o2.getMeasureDescription().getReference());
	}

}
