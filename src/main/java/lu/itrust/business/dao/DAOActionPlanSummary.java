package lu.itrust.business.dao;

import java.util.List;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.actionplan.ActionPlanType;
import lu.itrust.business.TS.actionplan.SummaryStage;

/** 
 * DAOActionPlanSummary.java: <br>
 * Detailed description...
 *
 * @author itrust consulting s.�.rl. :
 * @version 
 * @since 16 janv. 2013
 */
public interface DAOActionPlanSummary {
	
	public SummaryStage get(int idSummaryStage) throws Exception;

	public SummaryStage getFromAnalysisById(Integer idAnalysis, Integer idSummaryStage) throws Exception;
	
	public boolean belongsToAnalysis(int actionPlanSummaryId, int analysisId) throws Exception;
	
	public List<SummaryStage> getAll() throws Exception;

	public List<SummaryStage> getAllFromAnalysis(Integer idAnalysis) throws Exception;

	public List<SummaryStage> getAllFromAnalysis(Analysis analysis) throws Exception;
	
	public List<SummaryStage> getFromAnalysisAndActionPlanType(Integer idAnalysis, String actionPlanType) throws Exception;
	
	public List<SummaryStage> getFromAnalysisAndActionPlanType(Analysis analysis, ActionPlanType actionPlanType) throws Exception;
	
	public void save(SummaryStage summaryStage) throws Exception;
	
	public void saveOrUpdate(SummaryStage summaryStage) throws Exception;
	
	public void delete(SummaryStage summaryStage) throws Exception;

}