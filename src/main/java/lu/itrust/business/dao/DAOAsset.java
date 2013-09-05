package lu.itrust.business.dao;

import java.util.List;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.Asset;
import lu.itrust.business.TS.AssetType;

/** 
 * DAOAsset.java: <br>
 * Detailed description...
 *
 * @author itrust consulting s.à.rl. :
 * @version 
 * @since 16 janv. 2013
 */
public interface DAOAsset {
	public Asset get(int id) throws Exception;
	public Asset loadFromNameAnalysis(String assetName, Analysis analysis) throws Exception;
	public List<Asset> loadAllFromAssetType(AssetType assetType, Analysis analysis) throws Exception;
	public List<Asset> loadAllFromAssetTypeID(int assetTypeID, Analysis analysis) throws Exception;
	public List<Asset> loadAllFromAnalysis(Analysis analysis) throws Exception;
	public List<Asset> loadAllFromAnalysisID(int idAnalysis) throws Exception;
	public List<Asset> loadAllFromAnalysisIdentifierAndVersion(int idAnalysis, int identifier, int version) throws Exception;
	public List<Asset> loadAll() throws Exception;
	public void save(Asset asset) throws Exception;
	public void saveOrUpdate(Asset asset) throws Exception;
	public void remove(Asset asset) throws Exception;
}
