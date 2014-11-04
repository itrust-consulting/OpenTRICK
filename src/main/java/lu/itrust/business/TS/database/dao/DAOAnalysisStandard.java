package lu.itrust.business.TS.database.dao;

import java.util.List;

import lu.itrust.business.TS.data.analysis.Analysis;
import lu.itrust.business.TS.data.standard.AnalysisStandard;
import lu.itrust.business.TS.data.standard.Standard;

/**
 * DAOAnalysisStandard.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.�.rl. :
 * @version
 * @since 24 janv. 2013
 */
public interface DAOAnalysisStandard {
	public AnalysisStandard get(Integer id) throws Exception;

	public List<AnalysisStandard> getAll() throws Exception;

	public List<AnalysisStandard> getAllFromAnalysis(Integer analysisId) throws Exception;

	public List<AnalysisStandard> getAllComputableFromAnalysis(Integer analysisId) throws Exception;
	
	public List<AnalysisStandard> getAllFromAnalysis(Analysis analysis) throws Exception;

	public List<AnalysisStandard> getAllFromStandard(Standard standard) throws Exception;
	
	public Integer getAnalysisIDFromAnalysisStandard(Integer analysisStandard) throws Exception;

	public void deleteAllFromAnalysis(Integer analysisId) throws Exception;
	
	public void save(AnalysisStandard analysisStandard) throws Exception;

	public void saveOrUpdate(AnalysisStandard analysisStandard) throws Exception;

	public void delete(AnalysisStandard analysisStandard) throws Exception;
	
	public AnalysisStandard getFromAnalysisIdAndStandardId(Integer idAnalysis, int idStandard);
}