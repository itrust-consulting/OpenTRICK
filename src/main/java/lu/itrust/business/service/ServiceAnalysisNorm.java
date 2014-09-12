package lu.itrust.business.service;

import java.util.List;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.AnalysisNorm;
import lu.itrust.business.TS.Norm;

/**
 * ServiceAnalysisNorm.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.à.rl.
 * @version
 * @since Jan 24, 2013
 */
public interface ServiceAnalysisNorm {
	public AnalysisNorm get(Integer id) throws Exception;

	public List<AnalysisNorm> getAll() throws Exception;

	public List<AnalysisNorm> getAllFromAnalysis(Integer analysisId) throws Exception;

	public List<AnalysisNorm> getAllComputableFromAnalysis(Integer analysisId) throws Exception;
	
	public List<AnalysisNorm> getAllFromAnalysis(Analysis analysis) throws Exception;

	public List<AnalysisNorm> getAllFromNorm(Norm norm) throws Exception;

	public void save(AnalysisNorm analysisNorm) throws Exception;

	public void saveOrUpdate(AnalysisNorm analysisNorm) throws Exception;

	public void delete(AnalysisNorm analysisNorm) throws Exception;
}