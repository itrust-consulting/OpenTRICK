package lu.itrust.business.TS.database.dao;

import java.util.List;

import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.standard.AnalysisStandard;
import lu.itrust.business.TS.model.standard.Standard;

/**
 * DAOAnalysisStandard.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.�.rl. :
 * @version
 * @since 24 janv. 2013
 */
public interface DAOAnalysisStandard {
	
	public AnalysisStandard get(Integer id);

	public List<AnalysisStandard> getAll();

	public List<AnalysisStandard> getAllFromAnalysis(Integer analysisId);

	public List<AnalysisStandard> getAllComputableFromAnalysis(Integer analysisId);
	
	public List<AnalysisStandard> getAllFromAnalysis(Analysis analysis);

	public List<AnalysisStandard> getAllFromStandard(Standard standard);
	
	public Integer getAnalysisIDFromAnalysisStandard(Integer analysisStandard);

	public void deleteAllFromAnalysis(Integer analysisId);
	
	public void save(AnalysisStandard analysisStandard);

	public void saveOrUpdate(AnalysisStandard analysisStandard);

	public void delete(AnalysisStandard analysisStandard);

	public AnalysisStandard getFromAnalysisIdAndStandardId(Integer idAnalysis, int idStandard);

	public boolean belongsToAnalysis(Integer idAnalysis, int id);

	public Standard getStandardById(int idAnalysisStandard);

	public String getStandardNameById(int idAnalysisStandard);

	public AnalysisStandard getFromAnalysisIdAndStandardName(Integer idAnalysis, String name);

	public Boolean hasStandard(Integer idAnalysis, String standard);

	public List<AnalysisStandard> findBySOAEnabledAndAnalysisId(boolean state, Integer idAnalysis);
	
	public List<AnalysisStandard> findByAndAnalysisIdAndTypeIn(Integer analysisId, String ... classes);

	public AnalysisStandard findOne(int id, int analysisId);
}