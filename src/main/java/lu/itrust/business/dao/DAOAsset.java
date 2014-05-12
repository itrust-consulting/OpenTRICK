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

	public Asset get(int id) throws Exception;

	public boolean belongsToAnalysis(int assetId, int analysisId) throws Exception;

	public List<Asset> getAll() throws Exception;

	public List<Asset> getByPageAndSize(int pageIndex, int pageSize) throws Exception;

	public List<Asset> getFromAnalysisByPageAndSize(int pageIndex, int pageSize, int analysisId) throws Exception;

	public List<Asset> getAllFromAnalysis(int analysisId) throws Exception;

	public List<Asset> getAllFromAnalysisIdAndSelected(int idAnalysis) throws Exception;

	public List<Asset> getSelectedFromAnalysisAndOrderByALE(int idAnalysis) throws Exception;

	public Asset save(Asset asset) throws Exception;

	public void saveOrUpdate(Asset asset) throws Exception;

	public Asset merge(Asset asset) throws Exception;

	public void delete(int id) throws Exception;

	public void delete(Asset asset) throws Exception;
}