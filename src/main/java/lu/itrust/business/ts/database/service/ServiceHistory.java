package lu.itrust.business.ts.database.service;

import java.util.List;

import lu.itrust.business.ts.model.analysis.Analysis;
import lu.itrust.business.ts.model.history.History;

/**
 * ServiceHistory.java: <br>
 * Detailed description...
 *
 * @author eomar, itrust consulting s.a.rl.
 * @version 
 * @since Jan 16, 2013
 */
public interface ServiceHistory {
	public History get(Integer id);

	public History getFromAnalysisById(Integer idAnalysis, Integer idHistory);
	
	public boolean belongsToAnalysis(Integer analysisId, Integer historyId);

	public boolean versionExistsInAnalysis(Integer analysisId, String version);

	public boolean versionExistsInAnalysis(Analysis analysis, String version);

	public List<String> getVersionsFromAnalysis(Integer analysisId);

	public List<History> getAll();

	public List<History> getAllFromAnalysis(Integer analysisid);

	public List<History> getAllFromAnalysis(Analysis analysis);

	public List<History> getAllFromAnalysisByAuthor(Analysis analysis, String author);

	public List<History> getAllFromAnalysisByVersion(Analysis analysis, String version);

	public void save(Integer analysisId, History history);

	public void save(History history);

	public void saveOrUpdate(History history);

	public void delete(History history);
}