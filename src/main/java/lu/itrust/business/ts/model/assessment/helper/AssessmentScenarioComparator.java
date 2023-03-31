/**
 * 
 */
package lu.itrust.business.ts.model.assessment.helper;

import java.util.Comparator;

import lu.itrust.business.ts.helper.NaturalOrderComparator;
import lu.itrust.business.ts.model.assessment.Assessment;

/**
 * @author eomar
 *
 */
public class AssessmentScenarioComparator implements Comparator<Assessment> {

	@Override
	public int compare(Assessment o1, Assessment o2) {
		int compare = NaturalOrderComparator.compareTo(o1.getScenario().getType().getName(), o2.getScenario().getType().getName());
		return compare == 0 ? NaturalOrderComparator.compareTo(o1.getScenario().getName(),o2.getScenario().getName()) : compare;
	}

}
