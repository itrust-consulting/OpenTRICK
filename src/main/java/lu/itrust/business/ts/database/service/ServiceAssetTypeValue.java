package lu.itrust.business.ts.database.service;

import java.util.List;

import lu.itrust.business.ts.model.general.AssetTypeValue;

/**
 * ServiceAssetTypeValue.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.a.rl.
 * @version
 * @since Jan 16, 2013
 */
public interface ServiceAssetTypeValue {
	public AssetTypeValue get(Integer id);

	public AssetTypeValue save(AssetTypeValue assetTypeValue);

	public AssetTypeValue saveOrUpdate(AssetTypeValue assetTypeValue);

	public AssetTypeValue merge(AssetTypeValue assetTypeValue);

	public void delete(Integer id);

	public void delete(AssetTypeValue assetTypeValue);

	public void delete(List<AssetTypeValue> assetTypeValues);
}