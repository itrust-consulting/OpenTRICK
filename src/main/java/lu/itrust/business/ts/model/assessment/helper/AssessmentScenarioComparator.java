/**
 * 
 */
package lu.itrust.business.ts.model.assessment.helper;

import java.util.Comparator;

import lu.itrust.business.ts.helper.NaturalOrderComparator;
import lu.itrust.business.ts.model.assessment.Assessment;

/**
 * This class implements the Comparator interface to compare Assessment objects based on their scenario type and name.
 */
public class AssessmentScenarioComparator implements Comparator<Assessment> {

	/**
	 * Compares two Assessment objects based on their scenario type and name.
	 *
	 * @param o1 the first Assessment object to compare
	 * @param o2 the second Assessment object to compare
	 * @return a negative integer, zero, or a positive integer as the first Assessment object is less than, equal to, or greater than the second Assessment object
	 */
	@Override
	public int compare(Assessment o1, Assessment o2) {
		int compare = NaturalOrderComparator.compareTo(o1.getScenario().getType().getName(), o2.getScenario().getType().getName());
		return compare == 0 ? NaturalOrderComparator.compareTo(o1.getScenario().getName(),o2.getScenario().getName()) : compare;
	}

}
