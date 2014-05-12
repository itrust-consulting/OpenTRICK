package lu.itrust.business.dao;

import java.sql.Date;
import java.util.List;

import lu.itrust.business.TS.Phase;

/**
 * DAOPhase.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.�.rl. :
 * @version
 * @since 16 janv. 2013
 */
public interface DAOPhase {

	public Phase get(int id) throws Exception;

	public Phase getPhaseFromAnalysisByPhaseNumber(int number, int IdAnalysis) throws Exception;

	public Phase getPhaseFromAnalysisIdByPhaseId(int idPhase, Integer idAnalysis) throws Exception;

	public boolean belongsToAnalysis(Integer phaseId, Integer analysisId) throws Exception;

	public boolean canBeDeleted(int idPhase) throws Exception;

	public List<Phase> getAllPhases() throws Exception;

	public List<Phase> getAllFromAnalysis(int idAnalysis) throws Exception;

	public List<Phase> getAllPhasesFromAnalysisByBeginDate(int idAnalysis, Date beginDate) throws Exception;

	public List<Phase> getAllPhasesFromAnalysisByEndDate(int idAnalysis, Date beginDate) throws Exception;

	public void save(Phase phase) throws Exception;

	public void saveOrUpdate(Phase phase) throws Exception;

	public void delete(Phase phase) throws Exception;
}