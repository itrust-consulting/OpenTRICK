package lu.itrust.business.TS.database.service;

import java.util.List;

import lu.itrust.business.TS.data.asset.AssetType;

/**
 * ServiceAssetType.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.à.rl.
 * @version
 * @since May 13, 2014
 */
public interface ServiceAssetType {
	public AssetType get(Integer id) throws Exception;

	public AssetType getByName(String assetTypeName) throws Exception;

	public List<AssetType> getAll() throws Exception;

	public List<AssetType> getAllFromAnalysis(Integer idAnalysis) throws Exception;

	public void save(AssetType assetType) throws Exception;

	public void saveOrUpdate(AssetType assetType) throws Exception;

	public void delete(AssetType assetType) throws Exception;
}