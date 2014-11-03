package lu.itrust.business.TS.component;

import java.util.Comparator;

import lu.itrust.business.TS.component.helper.ALE;

public class AssetComparatorByALE implements Comparator<ALE> {

	@Override
	public int compare(ALE o1, ALE o2) {
		int comp = Double.compare(o2.getValue(), o1.getValue());
		return comp == 0 ? o2.getAssetName().toLowerCase().compareTo(o1.getAssetName().toLowerCase())
				: comp;
	}
}
