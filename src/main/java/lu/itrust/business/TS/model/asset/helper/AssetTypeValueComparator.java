package lu.itrust.business.TS.model.asset.helper;

import java.util.Comparator;

import lu.itrust.business.TS.model.general.AssetTypeValue;

public class AssetTypeValueComparator implements Comparator<AssetTypeValue> {

	@Override
	public int compare(AssetTypeValue o1, AssetTypeValue o2) {
		return o1.getAssetType().getName().compareTo(o2.getAssetType().getName());
	}
}
