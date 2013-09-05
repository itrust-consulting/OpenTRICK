package lu.itrust.business.dao;

import java.util.List;

import lu.itrust.business.TS.History;

import lu.itrust.business.TS.Analysis;

/** 
 * DAOHistory.java: <br>
 * Detailed description...
 *
 * @author itrust consulting s.à.rl. :
 * @version 
 * @since Feb 1, 2013
 */
public interface DAOHistory {
	public History get(int id) throws Exception;
	
	public List<History> loadAllFromAnalysis(Analysis analysis) throws Exception;
	
	public List<History> loadAllByAuthorAnalysis(Analysis analysis, String author) throws Exception;
	
	public List<History> loadAllByVersionAnalysis(Analysis analysis, String version) throws Exception;
	
	public List<History> loadAll() throws Exception;
	
	public void save(History history) throws Exception;
	
	public void saveOrUpdate(History history) throws Exception;
	
	public void remove(History history)throws Exception;
}
