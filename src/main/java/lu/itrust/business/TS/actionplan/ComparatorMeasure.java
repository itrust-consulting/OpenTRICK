/**
 * 
 */
package lu.itrust.business.TS.actionplan;

import java.util.Comparator;

import lu.itrust.business.TS.Measure;

/**
 * @author oensuifudine
 *
 */
public class ComparatorMeasure implements Comparator<Measure> {

	@Override
	public int compare(Measure arg0, Measure arg1) {
		return arg0.getMeasureDescription().getReference().compareToIgnoreCase(arg1.getMeasureDescription().getReference());
	}
}
