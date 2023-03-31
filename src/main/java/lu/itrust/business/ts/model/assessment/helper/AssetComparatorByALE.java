package lu.itrust.business.ts.model.assessment.helper;

import java.util.Comparator;

import lu.itrust.business.ts.helper.NaturalOrderComparator;

public class AssetComparatorByALE implements Comparator<ALE> {

	@Override
	public int compare(ALE o1, ALE o2) {
		int comp = Double.compare(o2.getValue(), o1.getValue());
		return comp == 0 ? NaturalOrderComparator.compareTo(o2.getAssetName(), o1.getAssetName()) : comp;
	}
}
