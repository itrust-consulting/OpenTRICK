package lu.itrust.business.ts.model.assessment.helper;

import java.util.Comparator;

import lu.itrust.business.ts.helper.NaturalOrderComparator;

/**
 * A comparator for sorting ALE objects based on their ALE value in descending order.
 */
public class AssetComparatorByALE implements Comparator<ALE> {

	/**
	 * Compares two ALE objects based on their ALE value and asset name.
	 *
	 * @param o1 the first ALE object to compare
	 * @param o2 the second ALE object to compare
	 * @return a negative integer, zero, or a positive integer as the first ALE object is less than, equal to, or greater than the second ALE object
	 */
	@Override
	public int compare(ALE o1, ALE o2) {
		int comp = Double.compare(o2.getValue(), o1.getValue());
		return comp == 0 ? NaturalOrderComparator.compareTo(o2.getAssetName(), o1.getAssetName()) : comp;
	}
}
