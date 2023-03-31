package lu.itrust.business.ts.database.service;

import java.util.List;

import lu.itrust.business.ts.model.asset.Asset;

/**
 * ServiceAsset.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.à.rl.
 * @version
 * @since Jan 16, 2013
 */
public interface ServiceAsset {
	public Asset get(Integer id);

	public boolean belongsToAnalysis(Integer analysisId, Integer assetId);

	public Asset getFromAnalysisByName(Integer analysisId, String name);
	
	public List<Asset> getAll();

	public List<Asset> getByPageAndSize(Integer pageIndex, Integer pageSize);

	public List<Asset> getFromAnalysisByPageAndSize(Integer analysisId, Integer pageIndex, Integer pageSize);

	public List<Asset> getAllFromAnalysis(Integer analysisId);

	public List<Asset> getAllFromAnalysisIdAndSelected(Integer idAnalysis);

	public List<Asset> getSelectedFromAnalysisAndOrderByALE(Integer idAnalysis);

	public Asset save(Asset asset);

	public void saveOrUpdate(Asset asset);

	public Asset merge(Asset asset);

	public void delete(Integer id);

	public void delete(Asset asset);

	public boolean exist(Integer idAnalysis, String name);

	public Asset getFromAnalysisById(Integer idAnalysis, int idAsset);

	public Asset getByNameAndAnlysisId(String name, int idAnalysis);

	public boolean belongsToAnalysis(Integer integer, List<Integer> assetIds);
}