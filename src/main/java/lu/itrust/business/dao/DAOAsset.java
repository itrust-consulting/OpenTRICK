package lu.itrust.business.dao;

import java.util.List;

import lu.itrust.business.TS.Asset;

/**
 * DAOAsset.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.à.rl. :
 * @version
 * @since 16 janv. 2013
 */
public interface DAOAsset {
	
	Asset get(int id);

	List<Asset> find();

	List<Asset> find(int pageIndex, int pageSize);

	List<Asset> findByAnalysis(int analysisId);
	
	List<Asset> findByAnalysisAndSelected(int idAnalysis);

	List<Asset> findByAnalysis(int pageIndex, int pageSize, int analysisId);

	Asset save(Asset asset);

	void saveOrUpdate(Asset asset);

	Asset merge(Asset asset);

	void delete(Asset asset);

	void delete(int id);
}
