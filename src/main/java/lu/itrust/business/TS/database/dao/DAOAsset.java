package lu.itrust.business.TS.database.dao;

import java.util.List;

import lu.itrust.business.TS.model.asset.Asset;

/**
 * DAOAsset.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.�.rl. :
 * @version
 * @since 16 janv. 2013
 */
public interface DAOAsset {
	public Asset get(Integer id) ;

	public boolean belongsToAnalysis(Integer analysisId, Integer assetId) ;

	public Asset getFromAnalysisByName(Integer analysisId, String name) ;
	
	public List<Asset> getAll() ;

	public List<Asset> getByPageAndSize(Integer pageIndex, Integer pageSize) ;

	public List<Asset> getFromAnalysisByPageAndSize(Integer analysisId, Integer pageIndex, Integer pageSize) ;

	public List<Asset> getAllFromAnalysis(Integer analysisId) ;

	public List<Asset> getAllFromAnalysisIdAndSelected(Integer idAnalysis) ;

	public List<Asset> getSelectedFromAnalysisAndOrderByALE(Integer idAnalysis) ;

	public Integer getAnalysisIdFromAsset(Integer assetId) ;
	
	public Asset save(Asset asset) ;

	public void saveOrUpdate(Asset asset) ;

	public Asset merge(Asset asset) ;

	public void delete(Integer id) ;

	public void delete(Asset asset) ;
	
	public boolean exist(Integer idAnalysis, String name);

	public Asset getFromAnalysisById(Integer idAnalysis, int idAsset);

	public Asset getByNameAndAnlysisId(String name, int idAnalysis);

	public boolean belongsToAnalysis(Integer idAnalysis, List<Integer> assetIds);
}