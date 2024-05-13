/**
 * 
 */
package lu.itrust.business.ts.model.assessment.helper;

import java.util.Comparator;

import lu.itrust.business.ts.model.assessment.Assessment;


/**
 * A comparator for comparing Assessment objects based on their asset values.
 */
public class AssessmentAssetComparator implements Comparator<Assessment> {

	/**
	 * Compares two Assessment objects based on their asset values.
	 *
	 * @param o1 the first Assessment object to compare
	 * @param o2 the second Assessment object to compare
	 * @return a negative integer, zero, or a positive integer as the first Assessment's asset value is less than, equal to, or greater than the second Assessment's asset value
	 */
	@Override
	public int compare(Assessment o1, Assessment o2) {
		return Double.compare(o1.getAsset().getValue(), o2.getAsset().getValue());
	}

}
