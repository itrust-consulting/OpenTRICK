package lu.itrust.business.dao;

import java.sql.Date;
import java.util.List;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.Phase;

/** 
 * DAOPhase.java: <br>
 * Detailed description...
 *
 * @author itrust consulting s.à.rl. :
 * @version 
 * @since 16 janv. 2013
 */
public interface DAOPhase {
	
	public Phase get(int id) throws Exception;
	
	public List<Phase> loadFromPhaseNumberAnalysis(int number, Analysis analysis) throws Exception;
	
	public List<Phase> loadByBeginDate(Date beginDate, Analysis analysis) throws Exception;
	
	public List<Phase> loadByEndDate(Date beginDate, Analysis analysis) throws Exception;
	
	public List<Phase> loadAllFromAnalysis(Analysis analysis) throws Exception;
	
	public List<Phase> loadAllFromAnalysisIdentifierVersionCreationDate(int identifier, int version, String creationDate) throws Exception;
	
	public List<Phase> loadAll() throws Exception;
	
	public void save(Phase phase) throws Exception;
	
	public void saveOrUpdate(Phase phase) throws Exception;
	
	public void remove(Phase phase)throws Exception;

}
