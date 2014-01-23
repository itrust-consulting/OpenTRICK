/**
 * 
 */
package lu.itrust.business.component;

import java.util.ArrayList;
import java.util.List;

import lu.itrust.business.TS.actionplan.SummaryStage;

/**
 * @author eomar
 *
 */
public class ActionPanSummaryManager {
	
	private static List<String> extractPhaseRow(List<SummaryStage> summaryStages){
		List<String> phases = new ArrayList<String>();
		for (SummaryStage summaryStage : summaryStages)
			if(!phases.contains(summaryStage.getStage()))
				phases.add(summaryStage.getStage());
		return phases;
	}
	
	public static List<String> buildFisrtRow(){
		List<String> rows = new ArrayList<String>();
		rows.add("label.phase.begin.date");
		rows.add("label.phase.end.date");
		rows.add("label.compliance.27001");
		rows.add("label.compliance.27002");
		rows.add("label.count.measure.phase");
		rows.add("label.count.measure.implemented");
		rows.add("label.count.measure.implemented");

		return null;
	}

	public static Map<String, List<String>> buildTable(List<SummaryStage> summaryStages){
		
		
	}
	

}
