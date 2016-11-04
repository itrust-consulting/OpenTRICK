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
public class AssessmentScenarioComparator implements Comparator<Assessment> {

	@Override
	public int compare(Assessment o1, Assessment o2) {
		in
		int compare = o1.getScenario().getType().getName().compareTo(o2.getScenario().getType().getName());
		return compare == 0 ? o1.getScenario().getName().compareToIgnoreCase(o2.getScenario().getName()) : compare;
	}

}
