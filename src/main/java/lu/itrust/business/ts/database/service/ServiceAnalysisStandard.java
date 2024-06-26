package lu.itrust.business.ts.database.service;

import java.util.List;

import lu.itrust.business.ts.model.analysis.Analysis;
import lu.itrust.business.ts.model.standard.AnalysisStandard;
import lu.itrust.business.ts.model.standard.Standard;
import lu.itrust.business.ts.model.standard.StandardType;

/**
 * ServiceAnalysisStandard.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.à.rl.
 * @version
 * @since Jan 24, 2013
 */
public interface ServiceAnalysisStandard {
	public AnalysisStandard get(Integer id);

	public List<AnalysisStandard> getAll();

	public List<AnalysisStandard> getAllFromAnalysis(Integer analysisId);

	public List<AnalysisStandard> getAllComputableFromAnalysis(Integer analysisId);

	public List<AnalysisStandard> getAllFromAnalysis(Analysis analysis);

	public List<AnalysisStandard> getAllFromStandard(Standard standard);

	public void deleteAllFromAnalysis(Integer analysisId);

	public void save(AnalysisStandard analysisStandard);

	public void saveOrUpdate(AnalysisStandard analysisStandard);

	public void delete(AnalysisStandard analysisStandard);

	public AnalysisStandard getFromAnalysisIdAndStandardId(Integer analysisId, int standardId);

	public boolean belongsToAnalysis(Integer idAnalysis, int id);

	public Standard getStandardById(int idAnalysisStandard);

	public String getStandardNameById(int idAnalysisStandard);

	public AnalysisStandard getFromAnalysisIdAndStandardName(Integer idAnalysis, String standard);

	public Boolean hasStandard(Integer idAnalysis, String standard);

	public List<AnalysisStandard> findBySOAEnabledAndAnalysisId(boolean b, Integer idAnalysis);

	public List<AnalysisStandard> findByAndAnalysisIdAndTypeIn(Integer analysisId, Class<?>... classes);

	public AnalysisStandard findOne(int id, int analysisId);

	public List<AnalysisStandard> findByComputableAndAnalysisIdAndTypeIn(boolean computable, Integer idAnalysis,
			Class<?>... classes);

	public List<Standard> findStandardByComputableAndAnalysisIdAndTypeIn(boolean computable, Integer idAnalysis,
			Class<?>... classes);

	public List<Standard> findStandardByAnalysisIdAndTypeIn(Integer idAnalysis, Class<?>... classes);

	public List<String> findByAnalysisAndNameLikeAndTypeAndCustom(Integer idAnalysis, String name, StandardType type,
			boolean custom);
}