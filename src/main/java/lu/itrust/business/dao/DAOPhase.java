package lu.itrust.business.dao;

import java.sql.Date;
import java.util.List;

import lu.itrust.business.TS.Phase;

/**
 * DAOPhase.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl. :
 * @version
 * @since 16 janv. 2013
 */
public interface DAOPhase {
	public Phase get(Integer id) throws Exception;

	public Phase getFromAnalysisByPhaseNumber(Integer IdAnalysis, Integer number) throws Exception;

	public Phase getFromAnalysisByPhaseId(Integer idAnalysis, Integer idPhase) throws Exception;

	public boolean belongsToAnalysis(Integer analysisId, Integer phaseId) throws Exception;

	public boolean canBeDeleted(Integer idPhase) throws Exception;

	public List<Phase> getAll() throws Exception;

	public List<Phase> getAllFromAnalysis(Integer idAnalysis) throws Exception;

	public List<Phase> getAllFromAnalysisByBeginDate(Integer idAnalysis, Date beginDate) throws Exception;

	public List<Phase> getAllFromAnalysisByEndDate(Integer idAnalysis, Date beginDate) throws Exception;

	public void save(Phase phase) throws Exception;

	public void saveOrUpdate(Phase phase) throws Exception;

	public void delete(Phase phase) throws Exception;
}