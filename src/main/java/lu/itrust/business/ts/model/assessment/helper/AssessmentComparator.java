package lu.itrust.business.ts.model.assessment.helper;

import java.util.Comparator;

import lu.itrust.business.ts.helper.NaturalOrderComparator;
import lu.itrust.business.ts.model.assessment.Assessment;

/**
 * A comparator for sorting Assessment objects based on their ALE (Annualized Loss Expectancy) values.
 * The comparator compares two Assessment objects and returns a negative integer, zero, or a positive integer
 * as the first Assessment is less than, equal to, or greater than the second Assessment, respectively.
 */
public class AssessmentComparator implements Comparator<Assessment> {

	@Override
	public int compare(Assessment o1, Assessment o2) {
		int comp = Double.compare(o2.getALE(), o1.getALE());
		return comp == 0 ? NaturalOrderComparator.compareTo(o1.getAsset().getName(), o2.getAsset().getName()) : comp;
	}

}
