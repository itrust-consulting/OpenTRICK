/**
 * 
 */
package lu.itrust.business.TS.model.standard.measuredescription.helper;

import java.util.Comparator;

import lu.itrust.business.TS.model.standard.measure.Measure;

/**
 * @author eomar
 *
 */
public class ComparatorMeasureReferance implements Comparator<Measure> {

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Measure o1, Measure o2) {
		if(!o1.getAnalysisStandard().getStandard().equals(o2.getAnalysisStandard().getStandard()))
			return o1.getAnalysisStandard().getStandard().getLabel().compareToIgnoreCase(o2.getAnalysisStandard().getStandard().getLabel());
		return ComparatorMeasureDescription.Compare(o1.getMeasureDescription().getReference(), o2.getMeasureDescription().getReference());
	}

}
