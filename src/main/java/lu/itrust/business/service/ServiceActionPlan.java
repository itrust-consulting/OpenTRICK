/**
 * 
 */
package lu.itrust.business.service;

import java.util.List;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.Measure;
import lu.itrust.business.TS.actionplan.ActionPlanEntry;
import lu.itrust.business.TS.actionplan.ActionPlanMode;
import lu.itrust.business.TS.actionplan.ActionPlanType;

/**
 * @author oensuifudine
 * 
 */
public interface ServiceActionPlan {

	public ActionPlanEntry get(int id) throws Exception;

	public ActionPlanEntry get(ActionPlanType actionPlanType, Measure measure) throws Exception;

	public List<ActionPlanEntry> findByActionPlanType(ActionPlanType actionPlanType) throws Exception;

	public List<ActionPlanEntry> findByAnalysis(Analysis analysis) throws Exception;
	
	public List<ActionPlanEntry> loadByAnalysisActionPlanType(Analysis analysis, ActionPlanMode mode) throws Exception;

	public List<ActionPlanEntry> loadByAnalysisActionPlanType(int analysisID, ActionPlanMode mode) throws Exception;

	public List<ActionPlanEntry> loadAllFromAnalysis(int identifier, String version, String creationDate) throws Exception;

	public List<ActionPlanEntry> loadAllFromAnalysis(int id) throws Exception;

	public List<ActionPlanEntry> loadAll() throws Exception;

	public void save(ActionPlanEntry actionPlanEntry) throws Exception;

	public void saveOrUpdate(ActionPlanEntry actionPlanEntry) throws Exception;

	public void delete(ActionPlanEntry actionPlanEntry) throws Exception;

}
