package lu.itrust.business.dao;

import java.util.List;

import lu.itrust.business.TS.AssetType;

public interface DAOAssetType {
	
	public AssetType get(int id)throws Exception;
	
	public AssetType get(String assetTypeName)throws Exception;
	
	public List<AssetType> loadAll() throws Exception;
	
	public void save(AssetType assetType) throws Exception;
	
	public void saveOrUpdate(AssetType assetType) throws Exception;
	
	public void delete(AssetType assetType) throws Exception;
}
