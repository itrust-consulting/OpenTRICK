package lu.itrust.business.TS.model.standard.helper;

import java.util.Comparator;

import lu.itrust.business.TS.model.standard.Standard;

public class StandardComparator implements Comparator<Standard> {

	@Override
	public int compare(Standard o1, Standard o2) {
		return o1.getLabel().compareToIgnoreCase(o2.getLabel());
	}
}
