package lu.itrust.business.TS.database.service;

import java.util.List;

import lu.itrust.business.TS.model.general.AssetTypeValue;

/**
 * ServiceAssetTypeValue.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl.
 * @version
 * @since Jan 16, 2013
 */
public interface ServiceAssetTypeValue {
	public AssetTypeValue get(Integer id) throws Exception;

	public AssetTypeValue save(AssetTypeValue assetTypeValue) throws Exception;

	public AssetTypeValue saveOrUpdate(AssetTypeValue assetTypeValue) throws Exception;

	public AssetTypeValue merge(AssetTypeValue assetTypeValue) throws Exception;

	public void delete(Integer id) throws Exception;

	public void delete(AssetTypeValue assetTypeValue) throws Exception;

	public void delete(List<AssetTypeValue> assetTypeValues) throws Exception;
}