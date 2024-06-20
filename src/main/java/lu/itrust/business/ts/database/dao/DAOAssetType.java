package lu.itrust.business.ts.database.dao;

import java.util.List;

import lu.itrust.business.ts.model.asset.AssetType;

/**
 * DAOAssetType.java: <br>
 * Detailed description...
 *
 * @author itrust consulting s.à.rl.
 * @version
 * @since 16 janv. 2013
 */
public interface DAOAssetType {
	
	public AssetType get(Integer id);

	public AssetType getByName(String assetTypeName);

	public List<AssetType> getAll();

	public List<AssetType> getAllFromAnalysis(Integer idAnalysis);

	public void save(AssetType assetType);

	public void saveOrUpdate(AssetType assetType);

	public void delete(AssetType assetType);
}