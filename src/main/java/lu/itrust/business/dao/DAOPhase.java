package lu.itrust.business.dao;

import java.sql.Date;
import java.util.List;

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

	public List<Phase> loadByBeginDate(Date beginDate, int idAnalysis) throws Exception;
	
	public List<Phase> loadByEndDate(Date beginDate, int idAnalysis) throws Exception;
	
	public List<Phase> loadAllFromAnalysis(int idAnalysis) throws Exception;
	
	public List<Phase> loadAll() throws Exception;
	
	public void save(Phase phase) throws Exception;
	
	public void saveOrUpdate(Phase phase) throws Exception;
	
	public void remove(Phase phase)throws Exception;

	Phase loadFromPhaseNumberAnalysis(int number, int IdAnalysis)
			throws Exception;
	
	Phase loadByIdAndIdAnalysis(int idPhase, Integer idAnalysis);

	public boolean canBeDeleted(int idPhase);

}
