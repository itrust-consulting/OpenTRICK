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

	List<Asset> getAll();

	List<Asset> getByPageAndSize(int pageIndex, int pageSize);

	List<Asset> getFromAnalysisByPageAndSize(int pageIndex, int pageSize, int analysisId);
	
	boolean belongsToAnalysis(int assetId, int analysisId);
	
	List<Asset> getAllFromAnalysis(int analysisId);
	
	List<Asset> getSelectedFromAnalysis(int idAnalysis);
	
	List<Asset> getSelectedFromAnalysisAndOrderByALE(int idAnalysis);
	
	Asset save(Asset asset);

	void saveOrUpdate(Asset asset);

	Asset merge(Asset asset);

	void delete(int id);

	void delete(Asset asset);
}
