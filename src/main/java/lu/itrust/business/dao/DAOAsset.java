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

	Asset get(int id) throws Exception;

	List<Asset> getAll() throws Exception;

	List<Asset> getByPageAndSize(int pageIndex, int pageSize) throws Exception;

	List<Asset> getFromAnalysisByPageAndSize(int pageIndex, int pageSize, int analysisId) throws Exception;

	boolean belongsToAnalysis(int assetId, int analysisId) throws Exception;

	List<Asset> getAllFromAnalysis(int analysisId) throws Exception;

	List<Asset> getSelectedFromAnalysis(int idAnalysis) throws Exception;

	List<Asset> getSelectedFromAnalysisAndOrderByALE(int idAnalysis) throws Exception;

	Asset save(Asset asset) throws Exception;

	void saveOrUpdate(Asset asset) throws Exception;

	Asset merge(Asset asset) throws Exception;

	void delete(int id) throws Exception;

	void delete(Asset asset) throws Exception;
}