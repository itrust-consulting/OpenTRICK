package lu.itrust.business.dao;

import java.util.List;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.AnalysisNorm;
import lu.itrust.business.TS.Norm;

/** 
 * DAOAnalysisNorm.java: <br>
 * Detailed description...
 *
 * @author itrust consulting s.à.rl. :
 * @version 
 * @since 24 janv. 2013
 */
public interface DAOAnalysisNorm {
	
	public AnalysisNorm get(int id) throws Exception;
	
	public List<AnalysisNorm> loadAll() throws Exception;
	
	public List<AnalysisNorm> loadAllFromAnalysis(Analysis analysis) throws Exception;
	
	public List<AnalysisNorm> loadAllFromNorm(Norm norm) throws Exception;
	
	public void save(AnalysisNorm analysisNorm) throws Exception;
	
	public void saveOrUpdate(AnalysisNorm analysisNorm) throws Exception;
	
	public void remove(AnalysisNorm analysisNorm)throws Exception;
}