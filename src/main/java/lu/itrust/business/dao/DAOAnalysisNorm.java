package lu.itrust.business.dao;

import java.util.List;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.AnalysisNorm;
import lu.itrust.business.TS.Norm;

/**
 * DAOAnalysisNorm.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.�.rl. :
 * @version
 * @since 24 janv. 2013
 */
public interface DAOAnalysisNorm {
	public AnalysisNorm get(Integer id) throws Exception;

	public List<AnalysisNorm> getAll() throws Exception;

	public List<AnalysisNorm> getAllFromAnalysis(Integer analysisId) throws Exception;

	public List<AnalysisNorm> getAllFromAnalysis(Analysis analysis) throws Exception;

	public List<AnalysisNorm> getAllFromNorm(Norm norm) throws Exception;

	public void save(AnalysisNorm analysisNorm) throws Exception;

	public void saveOrUpdate(AnalysisNorm analysisNorm) throws Exception;

	public void delete(AnalysisNorm analysisNorm) throws Exception;
}