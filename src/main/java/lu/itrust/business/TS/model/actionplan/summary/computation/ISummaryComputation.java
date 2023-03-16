/**
 * 
 */
package lu.itrust.business.TS.model.actionplan.summary.computation;

import java.util.List;
import java.util.Map;

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.model.actionplan.ActionPlanEntry;
import lu.itrust.business.TS.model.actionplan.ActionPlanMode;
import lu.itrust.business.TS.model.actionplan.ActionPlanType;
import lu.itrust.business.TS.model.actionplan.summary.SummaryStage;
import lu.itrust.business.TS.model.actionplan.summary.helper.MaintenanceRecurrentInvestment;
import lu.itrust.business.TS.model.actionplan.summary.helper.SummaryValues;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.general.Phase;

/**
 * @author eomar
 *
 */
public interface ISummaryComputation {
	
	final String START_P0 = "Start(P0)";
	
	void compute(ActionPlanMode mode);
	
	List<ActionPlanEntry> getActionPlans();
	
	ActionPlanType getActionPlanType();
	
	Analysis getAnalysis();
	
	SummaryValues getCurrentValues();
	
	double getExternalSetupRate();
	
	double getInternalSetupRate();

	boolean isFullCostRelated();
	
	Map<Integer, MaintenanceRecurrentInvestment> getMaintenances();
	
	List<Phase> getPhases();
	
	/**
	 * Retrieve maintenance of measure were already implemented
	 * @return the preMaintenance
	 */
	MaintenanceRecurrentInvestment getPreMaintenance();
	
	double getSoa();
	
	List<SummaryStage> getSummaryStages();
	
	static String findChapter(String chapter) {
		if ((chapter.toUpperCase().startsWith("A.")) || (chapter.toUpperCase().startsWith("M."))) {
			String[] chapters = chapter.split("[.]", 3);
			return chapters[0] + "." + chapters[1];
		}
		return chapter.split(Constant.REGEX_SPLIT_REFERENCE, 2)[0];
	}
}
