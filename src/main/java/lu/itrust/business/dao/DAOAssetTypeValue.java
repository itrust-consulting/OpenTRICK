package lu.itrust.business.dao;

import java.util.List;

import lu.itrust.business.TS.AssetTypeValue;

/**
 * DAOAssetTypeValue.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.�.rl. :
 * @version
 * @since 16 janv. 2013
 */
public interface DAOAssetTypeValue {

	public AssetTypeValue get(int id) throws Exception;

	public AssetTypeValue save(AssetTypeValue assetTypeValue) throws Exception;

	public AssetTypeValue saveOrUpdate(AssetTypeValue assetTypeValue) throws Exception;

	public AssetTypeValue merge(AssetTypeValue assetTypeValue) throws Exception;

	public void delete(int id) throws Exception;

	public void delete(AssetTypeValue assetTypeValue) throws Exception;

	public void delete(List<AssetTypeValue> assetTypeValues) throws Exception;
}