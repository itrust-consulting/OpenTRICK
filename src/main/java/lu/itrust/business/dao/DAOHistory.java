package lu.itrust.business.dao;

import java.util.List;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.History;

/**
 * DAOHistory.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.�.rl. :
 * @version
 * @since Feb 1, 2013
 */
public interface DAOHistory {
	public History get(int id) throws Exception;

	boolean belongsToAnalysis(Integer historyId, Integer analysisId) throws Exception;

	public boolean versionExistsByAnalysisIdAndAnalysisVersion(Integer analysisId, String version) throws Exception;

	public boolean versionExistsForAnalysisByVersion(Analysis analysis, String version) throws Exception;

	public List<String> getVersionsFromAnalysisId(int analysisId) throws Exception;

	public List<History> getAllHistories() throws Exception;

	public List<History> getAllFromAnalysisId(Integer analysisid) throws Exception;

	public List<History> getAllFromAnalysis(Analysis analysis) throws Exception;

	public List<History> getAllHistoriesFromAnalysisByAuthor(Analysis analysis, String author) throws Exception;

	public List<History> getAllHistoriesFromAnalysisByVersion(Analysis analysis, String version) throws Exception;

	public void save(Integer analysisId, History history) throws Exception;

	public void save(History history) throws Exception;

	public void saveOrUpdate(History history) throws Exception;

	public void delete(History history) throws Exception;
}