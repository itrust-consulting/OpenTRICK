/**
 * 
 */
package lu.itrust.business.ts.model.actionplan.summary.computation;

import java.util.List;
import java.util.Map;

import lu.itrust.business.ts.constants.Constant;
import lu.itrust.business.ts.model.actionplan.ActionPlanEntry;
import lu.itrust.business.ts.model.actionplan.ActionPlanMode;
import lu.itrust.business.ts.model.actionplan.ActionPlanType;
import lu.itrust.business.ts.model.actionplan.summary.SummaryStage;
import lu.itrust.business.ts.model.actionplan.summary.helper.MaintenanceRecurrentInvestment;
import lu.itrust.business.ts.model.actionplan.summary.helper.SummaryValues;
import lu.itrust.business.ts.model.analysis.Analysis;
import lu.itrust.business.ts.model.general.Phase;


/**
 * The ISummaryComputation interface represents a computation for generating action plans and summaries.
 */
public interface ISummaryComputation {
	
	final String START_P0 = "Start(P0)";
	
	/**
	 * Computes the action plans based on the given mode.
	 * 
	 * @param mode The action plan mode.
	 */
	void compute(ActionPlanMode mode);
	
	/**
	 * Retrieves the list of action plans.
	 * 
	 * @return The list of action plans.
	 */
	List<ActionPlanEntry> getActionPlans();
	
	/**
	 * Retrieves the action plan type.
	 * 
	 * @return The action plan type.
	 */
	ActionPlanType getActionPlanType();
	
	/**
	 * Retrieves the analysis.
	 * 
	 * @return The analysis.
	 */
	Analysis getAnalysis();
	
	/**
	 * Retrieves the current summary values.
	 * 
	 * @return The current summary values.
	 */
	SummaryValues getCurrentValues();
	
	/**
	 * Retrieves the external setup rate.
	 * 
	 * @return The external setup rate.
	 */
	double getExternalSetupRate();
	
	/**
	 * Retrieves the internal setup rate.
	 * 
	 * @return The internal setup rate.
	 */
	double getInternalSetupRate();

	/**
	 * Checks if the computation is related to full cost.
	 * 
	 * @return True if the computation is related to full cost, false otherwise.
	 */
	boolean isFullCostRelated();
	
	/**
	 * Retrieves the map of maintenances.
	 * 
	 * @return The map of maintenances.
	 */
	Map<Integer, MaintenanceRecurrentInvestment> getMaintenances();
	
	/**
	 * Retrieves the list of phases.
	 * 
	 * @return The list of phases.
	 */
	List<Phase> getPhases();
	
	/**
	 * Retrieves the pre-maintenance of the measure that were already implemented.
	 * 
	 * @return The pre-maintenance.
	 */
	MaintenanceRecurrentInvestment getPreMaintenance();
	
	/**
	 * Retrieves the SOA (System Operational Availability).
	 * 
	 * @return The SOA.
	 */
	double getSoa();
	
	/**
	 * Retrieves the list of summary stages.
	 * 
	 * @return The list of summary stages.
	 */
	List<SummaryStage> getSummaryStages();
	
	/**
	 * Finds the chapter based on the given chapter string.
	 * 
	 * @param chapter The chapter string.
	 * @return The found chapter.
	 */
	static String findChapter(String chapter) {
		if ((chapter.toUpperCase().startsWith("A.")) || (chapter.toUpperCase().startsWith("M."))) {
			String[] chapters = chapter.split("[.]", 3);
			return chapters[0] + "." + chapters[1];
		}
		return chapter.split(Constant.REGEX_SPLIT_REFERENCE, 2)[0];
	}
}
