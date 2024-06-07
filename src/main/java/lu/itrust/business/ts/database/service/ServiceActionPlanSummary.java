package lu.itrust.business.ts.database.service;

import java.util.List;

import lu.itrust.business.ts.model.actionplan.ActionPlanType;
import lu.itrust.business.ts.model.actionplan.summary.SummaryStage;
import lu.itrust.business.ts.model.analysis.Analysis;

/**
 * ServiceActionPlanSummary.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.à.rl.
 * @version
 * @since 16 janv. 2013
 */
public interface ServiceActionPlanSummary {
	public SummaryStage get(Integer idSummaryStage);

	public SummaryStage getFromAnalysisById(Integer idAnalysis, Integer idSummaryStage);

	public boolean belongsToAnalysis(Integer analysisId, Integer actionPlanSummaryId);

	public List<SummaryStage> getAll();

	public List<SummaryStage> getAllFromAnalysis(Integer idAnalysis);

	public List<SummaryStage> getAllFromAnalysis(Analysis analysis);

	public List<SummaryStage> getAllFromAnalysisAndActionPlanType(Integer idAnalysis, String actionPlanType);

	public List<SummaryStage> getAllFromAnalysisAndActionPlanType(Analysis analysis, ActionPlanType actionPlanType);

	public void save(SummaryStage summaryStage);

	public void saveOrUpdate(SummaryStage summaryStage);

	public void delete(SummaryStage summaryStage);
	
	@Deprecated
	public void deleteAllFromAnalysis(Integer analysisID);
}