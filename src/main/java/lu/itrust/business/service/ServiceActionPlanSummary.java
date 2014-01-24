/**
 * 
 */
package lu.itrust.business.service;

import java.util.List;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.actionplan.ActionPlanType;
import lu.itrust.business.TS.actionplan.SummaryStage;

/**
 * @author oensuifudine
 * 
 */
public interface ServiceActionPlanSummary {

	
	public SummaryStage get(int idSummaryStage) throws Exception;
	
	public SummaryStage findByIdAndAnalysis(int id, Integer idAnalysis);

	public List<SummaryStage> loadAllFromType(ActionPlanType actionPlanType, Analysis analysis) throws Exception;

	public List<SummaryStage> findByAnalysis(Integer idAnalysis);
	
	public List<SummaryStage> findByAnalysisAndActionPlanType(Integer idAnalysis, String actionPlanType);

	public List<SummaryStage> loadAllFromAnalysis(Analysis analysis) throws Exception;

	public List<SummaryStage> loadAllFromAnalysisIdentifierVersionCreationDate(int identifier, String version, String creationDate) throws Exception;

	public List<SummaryStage> loadAll() throws Exception;

	public void save(SummaryStage summaryStage) throws Exception;

	public void saveOrUpdate(SummaryStage summaryStage) throws Exception;

	public void remove(SummaryStage summaryStage) throws Exception;

}
