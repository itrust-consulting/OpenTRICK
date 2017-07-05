package lu.itrust.business.TS.model.assessment.helper;

import java.util.Comparator;

import lu.itrust.business.TS.component.NaturalOrderComparator;
import lu.itrust.business.TS.model.assessment.Assessment;

public class AssessmentComparator implements Comparator<Assessment> {

	@Override
	public int compare(Assessment o1, Assessment o2) {
		int comp = Double.compare(o2.getALE(), o1.getALE());
		return comp == 0 ? NaturalOrderComparator.compareTo(o1.getAsset().getName(), o2.getAsset().getName()) : comp;
	}

}
