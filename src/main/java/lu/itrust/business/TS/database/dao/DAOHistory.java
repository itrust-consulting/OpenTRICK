package lu.itrust.business.TS.database.dao;

import java.util.List;

import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.history.History;

/**
 * DAOHistory.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.a.rl. :
 * @version
 * @since Feb 1, 2013
 */
public interface DAOHistory {
	public History get(Integer id) throws Exception;

	public History getFromAnalysisById(Integer idAnalysis, Integer idHistory) throws Exception;
	
	public boolean belongsToAnalysis(Integer analysisId, Integer historyId) throws Exception;

	public boolean versionExistsInAnalysis(Integer analysisId, String version) throws Exception;

	public boolean versionExistsInAnalysis(Analysis analysis, String version) throws Exception;

	public List<String> getVersionsFromAnalysis(Integer analysisId) throws Exception;

	public List<History> getAll() throws Exception;

	public List<History> getAllFromAnalysis(Integer analysisid) throws Exception;

	public List<History> getAllFromAnalysis(Analysis analysis) throws Exception;

	public List<History> getAllFromAnalysisByAuthor(Analysis analysis, String author) throws Exception;

	public List<History> getAllFromAnalysisByVersion(Analysis analysis, String version) throws Exception;

	public void save(Integer analysisId, History history) throws Exception;

	public void save(History history) throws Exception;

	public void saveOrUpdate(History history) throws Exception;

	public void delete(History history) throws Exception;
	
}