package lu.itrust.business.dao;

import java.util.List;

import lu.itrust.business.TS.Asset;

/**
 * DAOAsset.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.�.rl. :
 * @version
 * @since 16 janv. 2013
 */
public interface DAOAsset {
	public Asset get(Integer id) throws Exception;

	public boolean belongsToAnalysis(Integer analysisId, Integer assetId) throws Exception;

	public List<Asset> getAll() throws Exception;

	public List<Asset> getByPageAndSize(Integer pageIndex, Integer pageSize) throws Exception;

	public List<Asset> getFromAnalysisByPageAndSize(Integer analysisId, Integer pageIndex, Integer pageSize) throws Exception;

	public List<Asset> getAllFromAnalysis(Integer analysisId) throws Exception;

	public List<Asset> getAllFromAnalysisIdAndSelected(Integer idAnalysis) throws Exception;

	public List<Asset> getSelectedFromAnalysisAndOrderByALE(Integer idAnalysis) throws Exception;

	public Asset save(Asset asset) throws Exception;

	public void saveOrUpdate(Asset asset) throws Exception;

	public Asset merge(Asset asset) throws Exception;

	public void delete(Integer id) throws Exception;

	public void delete(Asset asset) throws Exception;

	public boolean exist(Integer idAnalysis, String name);
}