package lu.itrust.business.service;

import java.util.List;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.History;
import lu.itrust.business.dao.DAOHistory;

/** 
 * ServiceHistory.java: <br>
 * Detailed description...
 *
 * @author itrust consulting s.à.rl. :
 * @version 
 * @since Oct 22, 2013
 */
public interface ServiceHistory {
	public History get(Integer id) throws Exception;
	
	public List<History> getAllFromAnalysis(Integer id) throws Exception;
	
	public List<History> getAllFromAnalysis(Analysis analysis) throws Exception;
	
	public boolean versionExists(Analysis analysis, String version) throws Exception;
	
	public boolean versionExists(Integer analysisId, String version) throws Exception;
	
	public void save(History history) throws Exception;
	
	public void save(Integer analysisId, History history) throws Exception;

	public void saveOrUpdate(History history) throws Exception;

	public void delete(History history) throws Exception;
	
	public DAOHistory getDAOHistory() throws Exception;

	public List<String> findVersionByAnalysis(int analysisId);
	
}