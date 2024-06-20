package lu.itrust.business.ts.database.dao;

import java.util.List;

import lu.itrust.business.ts.model.general.AssetTypeValue;

/**
 * DAOAssetTypeValue.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.a.rl. :
 * @version
 * @since 16 janv. 2013
 */
public interface DAOAssetTypeValue {
	public AssetTypeValue get(Integer id);

	public AssetTypeValue save(AssetTypeValue assetTypeValue);

	public AssetTypeValue saveOrUpdate(AssetTypeValue assetTypeValue);

	public AssetTypeValue merge(AssetTypeValue assetTypeValue);

	public void delete(Integer id);

	public void delete(AssetTypeValue assetTypeValue);

	public void delete(List<AssetTypeValue> assetTypeValues);
	
}