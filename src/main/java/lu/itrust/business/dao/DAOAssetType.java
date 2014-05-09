package lu.itrust.business.dao;

import java.util.List;

import lu.itrust.business.TS.AssetType;

public interface DAOAssetType {

	public AssetType get(int id) throws Exception;

	public AssetType getByName(String assetTypeName) throws Exception;

	public List<AssetType> getAll() throws Exception;

	public List<AssetType> getAllFromAnalysis(int idAnalysis) throws Exception;

	public void save(AssetType assetType) throws Exception;

	public void saveOrUpdate(AssetType assetType) throws Exception;

	public void delete(AssetType assetType) throws Exception;
}