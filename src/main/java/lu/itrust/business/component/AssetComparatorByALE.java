package lu.itrust.business.component;

import java.util.Comparator;

public class AssetComparatorByALE implements Comparator<ALE> {

	@Override
	public int compare(ALE o1, ALE o2) {
		int comp = Double.compare(o2.getValue(), o1.getValue());
		return comp == 0 ? o1.getAssetName().toLowerCase().compareTo(o2.getAssetName().toLowerCase())
				: comp;
	}
}
