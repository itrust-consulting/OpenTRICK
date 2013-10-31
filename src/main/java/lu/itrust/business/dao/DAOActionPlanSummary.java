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
	
	public List<SummaryStage> loadAllFromType(ActionPlanType actionPlanType, Analysis analysis) throws Exception;
	
	public List<SummaryStage> loadAllFromAnalysis(Analysis analysis) throws Exception;
	
	public List<SummaryStage> loadAllFromAnalysisIdentifierVersionCreationDate(int identifier, String version, String creationDate) throws Exception;
	
	public List<SummaryStage> loadAll() throws Exception;
	
	public void save(SummaryStage summaryStage) throws Exception;
	
	public void saveOrUpdate(SummaryStage summaryStage) throws Exception;
	
	public void remove(SummaryStage summaryStage) throws Exception;

}