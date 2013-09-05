/**
 * 
 */
package lu.itrust.business.service;

import java.util.List;

import lu.itrust.business.TS.AssetType;
import lu.itrust.business.dao.DAOAssetType;

/**
 * @author oensuifudine
 *
 */
public interface ServiceAssetType {
	
public AssetType get(int id)throws Exception;
	
	public AssetType get(String assetTypeName)throws Exception;
	
	public List<AssetType> loadAll() throws Exception;
	
	public void save(AssetType assetType) throws Exception;
	
	public void saveOrUpdate(AssetType assetType) throws Exception;
	
	public void delete(AssetType assetType) throws Exception;
	
	public DAOAssetType getDaoAssetType();

}
