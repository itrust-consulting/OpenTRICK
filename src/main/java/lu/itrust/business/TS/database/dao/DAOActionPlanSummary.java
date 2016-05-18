package lu.itrust.business.TS.database.dao;

import java.util.List;

import lu.itrust.business.TS.model.actionplan.ActionPlanType;
import lu.itrust.business.TS.model.actionplan.summary.SummaryStage;
import lu.itrust.business.TS.model.analysis.Analysis;

/**
 * DAOActionPlanSummary.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl. :
 * @version
 * @since 16 janv. 2013
 */
public interface DAOActionPlanSummary {
	public SummaryStage get(Integer idSummaryStage) ;

	public SummaryStage getFromAnalysisById(Integer idAnalysis, Integer idSummaryStage) ;

	public boolean belongsToAnalysis(Integer analysisId, Integer actionPlanSummaryId) ;

	public List<SummaryStage> getAll() ;

	public List<SummaryStage> getAllFromAnalysis(Integer idAnalysis) ;

	public List<SummaryStage> getAllFromAnalysis(Analysis analysis) ;

	public List<SummaryStage> getAllFromAnalysisAndActionPlanType(Integer idAnalysis, String actionPlanType) ;

	public List<SummaryStage> getAllFromAnalysisAndActionPlanType(Analysis analysis, ActionPlanType actionPlanType) ;

	public void save(SummaryStage summaryStage) ;

	public void saveOrUpdate(SummaryStage summaryStage) ;

	public void delete(SummaryStage summaryStage) ;
	
	public void deleteAllFromAnalysis(Integer analysisID) ;

}