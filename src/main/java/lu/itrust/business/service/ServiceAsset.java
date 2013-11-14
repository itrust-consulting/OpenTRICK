/**
 * 
 */
package lu.itrust.business.service;

import java.util.List;

import lu.itrust.business.TS.Asset;

/**
 * @author eom
 *
 */
public interface ServiceAsset {
	
	Asset get(int id);
	
	List<Asset> find();
	
	List<Asset> find(int pageIndex, int pageSize);
	
	List<Asset> findByAnalysis(int analysisId);
	
	List<Asset> findByAnalysis(int pageIndex, int pageSize ,int analysisId);
	
	Asset save(Asset asset);
	
	void saveOrUpdate(Asset asset);
	
	Asset merge(Asset asset);
	
	void delete(Asset asset);
	
	void delete(int id);
}
