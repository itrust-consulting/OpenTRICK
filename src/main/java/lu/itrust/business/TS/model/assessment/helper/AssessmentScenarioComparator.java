/**
 * 
 */
package lu.itrust.business.TS.model.assessment.helper;

import java.util.Comparator;

import lu.itrust.business.TS.component.NaturalOrderComparator;
import lu.itrust.business.TS.model.assessment.Assessment;

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
