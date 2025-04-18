/**
 * 
 */
package lu.itrust.business.ts.model.standard.measure.helper;

import lu.itrust.business.ts.helper.NaturalOrderComparator;
import lu.itrust.business.ts.model.standard.measure.Measure;


/**
 * This class implements the NaturalOrderComparator interface and provides a comparison method for Measure objects.
 * It compares the Measure objects based on the standard name and reference of their MeasureDescription.
 */
public class MeasureComparator implements NaturalOrderComparator<Measure> {

	/**
	 * Compares two Measure objects based on the standard name and reference of their MeasureDescription.
	 * If the standard names are equal, it compares the references.
	 *
	 * @param o1 the first Measure object to compare
	 * @param o2 the second Measure object to compare
	 * @return a negative integer, zero, or a positive integer as the first Measure is less than, equal to, or greater than the second Measure
	 */
	@Override
	public int compare(Measure o1, Measure o2) {
		int standard = NaturalOrderComparator.compareTo(o1.getMeasureDescription().getStandard().getName(), o2.getMeasureDescription().getStandard().getName());
		return standard == 0 ? NaturalOrderComparator.compareTo(o1.getMeasureDescription().getReference(), o2.getMeasureDescription().getReference()) : standard;
	}

}
