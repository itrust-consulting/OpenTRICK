/**
 * 
 */
package lu.itrust.business.component;

import java.util.Comparator;

import lu.itrust.business.TS.Measure;

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
		if(!o1.getAnalysisNorm().getNorm().equals(o2.getAnalysisNorm().getNorm()))
			return o1.getAnalysisNorm().getNorm().getLabel().compareToIgnoreCase(o2.getAnalysisNorm().getNorm().getLabel());
		return ComparatorMeasureDescription.Compare(o1.getMeasureDescription().getReference(), o2.getMeasureDescription().getReference());
	}

}
