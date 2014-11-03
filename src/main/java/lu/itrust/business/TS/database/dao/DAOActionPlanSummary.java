package lu.itrust.business.TS.database.dao;

import java.util.List;

import lu.itrust.business.TS.data.actionplan.ActionPlanType;
import lu.itrust.business.TS.data.actionplan.SummaryStage;
import lu.itrust.business.TS.data.basic.Analysis;

/**
 * DAOActionPlanSummary.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl. :
 * @version
 * @since 16 janv. 2013
 */
public interface DAOActionPlanSummary {
	public SummaryStage get(Integer idSummaryStage) throws Exception;

	public SummaryStage getFromAnalysisById(Integer idAnalysis, Integer idSummaryStage) throws Exception;

	public boolean belongsToAnalysis(Integer analysisId, Integer actionPlanSummaryId) throws Exception;

	public List<SummaryStage> getAll() throws Exception;

	public List<SummaryStage> getAllFromAnalysis(Integer idAnalysis) throws Exception;

	public List<SummaryStage> getAllFromAnalysis(Analysis analysis) throws Exception;

	public List<SummaryStage> getAllFromAnalysisAndActionPlanType(Integer idAnalysis, String actionPlanType) throws Exception;

	public List<SummaryStage> getAllFromAnalysisAndActionPlanType(Analysis analysis, ActionPlanType actionPlanType) throws Exception;

	public void save(SummaryStage summaryStage) throws Exception;

	public void saveOrUpdate(SummaryStage summaryStage) throws Exception;

	public void delete(SummaryStage summaryStage) throws Exception;
	
	public void deleteAllFromAnalysis(Integer analysisID) throws Exception;
	
}