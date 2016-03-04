package lu.itrust.business.TS.database.service;

import java.util.List;

import lu.itrust.business.TS.model.asset.AssetType;

/**
 * ServiceAssetType.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.à.rl.
 * @version
 * @since May 13, 2014
 */
public interface ServiceAssetType {
	public AssetType get(Integer id) ;

	public AssetType getByName(String assetTypeName) ;

	public List<AssetType> getAll() ;

	public List<AssetType> getAllFromAnalysis(Integer idAnalysis) ;

	public void save(AssetType assetType) ;

	public void saveOrUpdate(AssetType assetType) ;

	public void delete(AssetType assetType) ;
}