package lu.itrust.business.TS.component;

import java.util.Comparator;

import lu.itrust.business.TS.data.basic.Assessment;

public class AssessmentComparator implements Comparator<Assessment> {

	@Override
	public int compare(Assessment o1, Assessment o2) {
		int comp = Double.compare(o2.getALE(), o1.getALE());
		return comp == 0 ? o1.getAsset().getName().toLowerCase()
				.compareTo(o2.getAsset().getName().toLowerCase()) : comp;
	}

}
