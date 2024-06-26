package lu.itrust.business.ts.model.asset.helper;

import java.util.Comparator;

import lu.itrust.business.ts.helper.NaturalOrderComparator;
import lu.itrust.business.ts.model.general.AssetTypeValue;

/**
 * A comparator for comparing instances of the AssetTypeValue class based on the name of their asset type.
 */
public class AssetTypeValueComparator implements Comparator<AssetTypeValue> {

	@Override
	public int compare(AssetTypeValue o1, AssetTypeValue o2) {
		return NaturalOrderComparator.compareTo(o1.getAssetType().getName(), o2.getAssetType().getName());
	}
}
