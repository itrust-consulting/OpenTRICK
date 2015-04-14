package lu.itrust.business.TS.database.dao;

import java.util.List;

import lu.itrust.business.TS.model.asset.AssetType;

/**
 * DAOAssetType.java: <br>
 * Detailed description...
 *
 * @author smenghi, itrust consulting s.à.rl.
 * @version 
 * @since 16 janv. 2013
 */
public interface DAOAssetType {
	public AssetType get(Integer id) throws Exception;

	public AssetType getByName(String assetTypeName) throws Exception;

	public List<AssetType> getAll() throws Exception;

	public List<AssetType> getAllFromAnalysis(Integer idAnalysis) throws Exception;

	public void save(AssetType assetType) throws Exception;

	public void saveOrUpdate(AssetType assetType) throws Exception;

	public void delete(AssetType assetType) throws Exception;
}