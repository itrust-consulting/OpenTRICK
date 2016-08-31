/**
 * 
 */
package lu.itrust.business.TS.model.assessment.helper;

import java.util.Comparator;

import lu.itrust.business.TS.model.assessment.Assessment;

/**
 * @author eomar
 *
 */
public class AssessmentAssetComparator implements Comparator<Assessment> {

	@Override
	public int compare(Assessment o1, Assessment o2) {
		return Double.compare(o1.getAsset().getValue(), o2.getAsset().getValue());
	}

}
