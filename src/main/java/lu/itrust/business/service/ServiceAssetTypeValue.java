package lu.itrust.business.service;

import java.util.List;

import lu.itrust.business.TS.AssetType;
import lu.itrust.business.TS.AssetTypeValue;

public interface ServiceAssetTypeValue {
	
	AssetTypeValue findOne(int id);
	
	AssetTypeValue findByIdAndAnalysis(int id, int analysis);
	
	AssetTypeValue findByIdAndScenario(int id, int scenario);
	
	AssetTypeValue findByIdAndMeasure(int id, int measure);
	
	List<AssetTypeValue> findByMeasure(int measure);
	
	List<AssetTypeValue> findByScenario(int scenario);
	
	List<AssetTypeValue> findByAndAnalysis(int analysis);
	
	List<AssetTypeValue> findAll();
	
	List<AssetTypeValue> findByAssetType(AssetType assetType);
	
	List<AssetTypeValue> findByAssetTypeAndAnalysis(AssetType assetType, int analysis);
	
	List<AssetTypeValue> findByAssetTypeAndAnalysis(String assetType, int analysis);
	
	AssetTypeValue save(AssetTypeValue assetTypeValue);
	
	AssetTypeValue saveOrUpdate(AssetTypeValue assetTypeValue);
	
	AssetTypeValue merge(AssetTypeValue assetTypeValue);
	
	void delete(AssetTypeValue assetTypeValue);
	
	void delete(int id);
	
	void delete(List<AssetTypeValue> assetTypeValues);
	
}
