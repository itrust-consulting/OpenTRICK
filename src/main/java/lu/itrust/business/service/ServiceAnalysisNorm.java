/**
 * 
 */
package lu.itrust.business.service;

import java.util.List;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.AnalysisNorm;
import lu.itrust.business.TS.Norm;
import lu.itrust.business.dao.DAOAnalysisNorm;

/**
 * @author oensuifudine
 *
 */
public interface ServiceAnalysisNorm {
	
public AnalysisNorm get(int id) throws Exception;
	
	public List<AnalysisNorm> loadAll() throws Exception;
	
	public List<AnalysisNorm> loadAllFromAnalysis(Analysis analysis) throws Exception;
	
	public List<AnalysisNorm> loadAllFromNorm(Norm norm) throws Exception;
	
	public void save(AnalysisNorm analysisNorm) throws Exception;
	
	public void saveOrUpdate(AnalysisNorm analysisNorm) throws Exception;
	
	public void remove(AnalysisNorm analysisNorm)throws Exception;
	
	public DAOAnalysisNorm getDaoAnalysisNorm();


}
