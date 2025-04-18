package lu.itrust.business.ts.database.service;

import java.sql.Date;
import java.util.List;

import lu.itrust.business.ts.model.general.Phase;

/**
 * ServicePhase.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.a.rl.
 * @version
 * @since Jan 16, 2013
 */
public interface ServicePhase {
	public Phase get(Integer id);

	public Phase getFromAnalysisByPhaseNumber(Integer IdAnalysis, Integer number);

	public Phase getFromAnalysisById(Integer idAnalysis, Integer idPhase);

	public boolean belongsToAnalysis(Integer analysisId, Integer phaseId);

	public boolean canBeDeleted(Integer idPhase);

	public List<Phase> getAll();

	public List<Phase> getAllFromAnalysis(Integer idAnalysis);

	public List<Phase> getAllFromAnalysisByBeginDate(Integer idAnalysis, Date beginDate);

	public List<Phase> getAllFromAnalysisByEndDate(Integer idAnalysis, Date beginDate);

	public void save(Phase phase);

	public void saveOrUpdate(Phase phase);

	public void delete(Phase phase);

	public Phase findAllByIdAnalysis(Integer idAnalysis);
}