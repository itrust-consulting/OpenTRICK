/**
 * 
 */
package lu.itrust.business.component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.hibernate.Hibernate;

import lu.itrust.business.TS.Phase;
import lu.itrust.business.TS.actionplan.ActionPlanType;
import lu.itrust.business.TS.actionplan.SummaryStage;
import lu.itrust.business.TS.actionplan.SummaryStandardConformance;

/**
 * @author eomar
 * 
 */
public class ActionPlanSummaryManager {

	public static final String LABEL_RESOURCE_PLANNING_TOTAL_PHASE_COST = "label.resource.planning.total.phase.cost";
	public static final String LABEL_RESOURCE_PLANNING_CURRENT_COST = "label.resource.planning.current.cost";
	public static final String LABEL_RESOURCE_PLANNING_RECURRENT_INVESTMENT = "label.resource.planning.recurrent.investment";
	public static final String LABEL_RESOURCE_PLANNING_EXTERNAL_MAINTENANCE = "label.resource.planning.external.maintenance";
	public static final String LABEL_RESOURCE_PLANNING_INTERNAL_MAINTENANCE = "label.resource.planning.internal.maintenance";
	public static final String LABEL_RESOURCE_PLANNING_INVESTMENT = "label.resource.planning.investment";
	public static final String LABEL_RESOURCE_PLANNING_EXTERNAL_WORKLOAD = "label.resource.planning.external.workload";
	public static final String LABEL_RESOURCE_PLANNING_INTERNAL_WORKLOAD = "label.resource.planning.internal.workload";
	public static final String LABEL_RESOURCE_PLANNING = "label.resource.planning";
	public static final String LABEL_PROFITABILITY_ROSI_RELATIF = "label.profitability.rosi.relatif";
	public static final String LABEL_PROFITABILITY_ROSI = "label.profitability.rosi";
	public static final String LABEL_PROFITABILITY_PHASE_ANNUAL_COST = "label.profitability.phase.annual.cost";
	public static final String LABEL_PROFITABILITY_RISK_REDUCTION = "label.profitability.risk.reduction";
	public static final String LABEL_PROFITABILITY_ALE_UNTIL_END = "label.profitability.ale.until.end";
	public static final String LABEL_PROFITABILITY = "label.profitability";
	public static final String LABEL_COUNT_MEASURE_IMPLEMENTED = "label.count.measure.implemented";
	public static final String LABEL_CHARACTERISTIC_COUNT_MEASURE_IMPLEMENTED = "label.characteristic.count.measure.implemented";
	public static final String LABEL_CHARACTERISTIC_COUNT_MEASURE_PHASE = "label.characteristic.count.measure.phase";
	public static final String LABEL_CHARACTERISTIC_COMPLIANCE = "label.characteristic.compliance";
	public static final String LABEL_PHASE_END_DATE = "label.phase.end.date";
	public static final String LABEL_PHASE_BEGIN_DATE = "label.phase.begin.date";
	public static final String LABEL_CHARACTERISTIC = "label.characteristic";

	public static List<String> extractPhaseRow(List<SummaryStage> summaryStages) {
		List<String> phases = new ArrayList<String>();
		for (SummaryStage summaryStage : summaryStages)
			if (!phases.contains(summaryStage.getStage()))
				phases.add(summaryStage.getStage());
		return phases;
	}

	public static List<String> buildFirstRow(List<SummaryStandardConformance> conformances) {
		List<String> rows = new ArrayList<String>();
		rows.add(LABEL_CHARACTERISTIC);
		rows.add(LABEL_PHASE_BEGIN_DATE);
		rows.add(LABEL_PHASE_END_DATE);
		for(SummaryStandardConformance conformance : conformances)
			rows.add(LABEL_CHARACTERISTIC_COMPLIANCE + conformance.getNorm().getLabel());
		rows.add(LABEL_CHARACTERISTIC_COUNT_MEASURE_PHASE);
		rows.add(LABEL_CHARACTERISTIC_COUNT_MEASURE_IMPLEMENTED);
		rows.add(LABEL_PROFITABILITY);
		rows.add(LABEL_PROFITABILITY_ALE_UNTIL_END);
		rows.add(LABEL_PROFITABILITY_RISK_REDUCTION);
		rows.add(LABEL_PROFITABILITY_PHASE_ANNUAL_COST);
		rows.add(LABEL_PROFITABILITY_ROSI);
		rows.add(LABEL_PROFITABILITY_ROSI_RELATIF);
		rows.add(LABEL_RESOURCE_PLANNING);
		rows.add(LABEL_RESOURCE_PLANNING_INTERNAL_WORKLOAD);
		rows.add(LABEL_RESOURCE_PLANNING_EXTERNAL_WORKLOAD);
		rows.add(LABEL_RESOURCE_PLANNING_INVESTMENT);
		rows.add(LABEL_RESOURCE_PLANNING_INTERNAL_MAINTENANCE);
		rows.add(LABEL_RESOURCE_PLANNING_EXTERNAL_MAINTENANCE);
		rows.add(LABEL_RESOURCE_PLANNING_RECURRENT_INVESTMENT);
		rows.add(LABEL_RESOURCE_PLANNING_CURRENT_COST);
		rows.add(LABEL_RESOURCE_PLANNING_TOTAL_PHASE_COST);
		return rows;
	}

	public static Map<String, Phase> buildPhase(List<Phase> phases,List<String> extractedPhases) {
		Map<String, Phase> phaseStages = new LinkedHashMap<String, Phase>();
		
		Phase tmpphase = new Phase(0);
		
		phaseStages.put("Start(P0)", tmpphase);
		
		for (Phase phase : phases) {
			Hibernate.initialize(phase);
			String stage = "Phase " + phase.getNumber();				
			if(extractedPhases.contains(stage))
				phaseStages.put(stage, phase);
		}
		return phaseStages;
	}

	private static Map<ActionPlanType, List<SummaryStage>> SplitByActionPlanType (List<SummaryStage> summaryStages) {
		Map<ActionPlanType, List<SummaryStage>> actionPlanTypes = new LinkedHashMap<ActionPlanType, List<SummaryStage>>();
		for (SummaryStage summaryStage : summaryStages) {
			List<SummaryStage> stages = actionPlanTypes.get(summaryStage.getActionPlanType());
			if (!actionPlanTypes.containsKey(summaryStage.getActionPlanType()))
				actionPlanTypes.put(summaryStage.getActionPlanType(), stages = new LinkedList<SummaryStage>());
			stages.add(summaryStage);
		}
		return actionPlanTypes;
	}
	
	public static Map<ActionPlanType, Map<String, List<String>>> buildTables(List<SummaryStage> summaryStages, List<Phase> phases){
		Map<ActionPlanType, List<SummaryStage>> summariesByActionPlanType = SplitByActionPlanType(summaryStages);
		Map<ActionPlanType, Map<String, List<String>>> summaries = new LinkedHashMap<ActionPlanType, Map<String,List<String>>>(summariesByActionPlanType.size());
		for (ActionPlanType actionPlanType : summariesByActionPlanType.keySet())
			summaries.put(actionPlanType, buildTable(summariesByActionPlanType.get(actionPlanType), phases));
		return summaries;
	}

	public static Map<String, List<String>> buildTable(List<SummaryStage> summaryStages, List<Phase> phases) {
		List<String> firstRows = buildFirstRow(summaryStages.get(0).getConformances());

		Map<String, List<String>> summaries = new LinkedHashMap<String, List<String>>(firstRows.size());

		List<String> rowHeaders = extractPhaseRow(summaryStages);

		Map<String, Phase> phaseStages = buildPhase(phases, rowHeaders);

		for (String string : firstRows) {
			List<String> rows = summaries.get(string);
			if (rows == null) {
				if (!(string.equals(LABEL_PROFITABILITY) || string.equals(LABEL_RESOURCE_PLANNING)))
					summaries.put(string, rows = new ArrayList<String>(rowHeaders.size()));
				else
					summaries.put(string, rows = new ArrayList<String>());
			}
			if (rows.isEmpty() && LABEL_CHARACTERISTIC.equals(string))
				rows.addAll(rowHeaders);
			
		}

		List<String> summary = null;

		for (SummaryStage summaryStage : summaryStages) {
			int index = rowHeaders.indexOf(summaryStage.getStage());
			if (index == -1)
				throw new IllegalArgumentException("Bad index....");

			Phase phase = phaseStages.get(summaryStage.getStage());

			if (phase != null) {
				if (phase.getNumber() == 0) {
					summary = summaries.get(LABEL_PHASE_BEGIN_DATE);
					summary.add(index, "");

					summary = summaries.get(LABEL_PHASE_END_DATE);
					summary.add(index, "");
				} else {
					summary = summaries.get(LABEL_PHASE_BEGIN_DATE);
					summary.add(index, phase.getBeginDate() + "");

					summary = summaries.get(LABEL_PHASE_END_DATE);
					summary.add(index, phase.getEndDate() + "");
				}
			}

			for(SummaryStandardConformance conformance : summaryStage.getConformances()) {
			
				summary = summaries.get(LABEL_CHARACTERISTIC_COMPLIANCE + conformance.getNorm().getLabel());
				summary.add(index, (int) (conformance.getConformance() * 100) + "");
			
			}

			summary = summaries.get(LABEL_CHARACTERISTIC_COUNT_MEASURE_PHASE);
			summary.add(index, summaryStage.getMeasureCount() + "");

			summary = summaries.get(LABEL_CHARACTERISTIC_COUNT_MEASURE_IMPLEMENTED);
			summary.add(index, summaryStage.getImplementedMeasuresCount() + "");

			summary = summaries.get(LABEL_PROFITABILITY_ALE_UNTIL_END);
			summary.add(index, Math.floor(summaryStage.getTotalALE() * 0.001) + "");

			summary = summaries.get(LABEL_PROFITABILITY_RISK_REDUCTION);
			summary.add(index, Math.floor(summaryStage.getDeltaALE() * 0.001) + "");

			summary = summaries.get(LABEL_PROFITABILITY_PHASE_ANNUAL_COST);
			summary.add(index, Math.floor(summaryStage.getCostOfMeasures() * 0.001) + "");

			summary = summaries.get(LABEL_PROFITABILITY_ROSI);
			summary.add(index, Math.floor(summaryStage.getROSI() * 0.001) + "");

			summary = summaries.get(LABEL_PROFITABILITY_ROSI_RELATIF);
			summary.add(index, Math.floor(summaryStage.getRelativeROSI()) + "");

			summary = summaries.get(LABEL_RESOURCE_PLANNING_INTERNAL_WORKLOAD);
			summary.add(index, summaryStage.getInternalWorkload() + "");

			summary = summaries.get(LABEL_RESOURCE_PLANNING_EXTERNAL_WORKLOAD);
			summary.add(index, summaryStage.getExternalWorkload() + "");

			summary = summaries.get(LABEL_RESOURCE_PLANNING_INVESTMENT);
			summary.add(index, Math.floor(summaryStage.getInvestment() * 0.001) + "");
			
			summary = summaries.get(LABEL_RESOURCE_PLANNING_INTERNAL_MAINTENANCE);
			summary.add(index, Math.floor(summaryStage.getInternalMaintenance()) + "");

			summary = summaries.get(LABEL_RESOURCE_PLANNING_EXTERNAL_MAINTENANCE);
			summary.add(index, Math.floor(summaryStage.getExternalMaintenance()) + "");

			summary = summaries.get(LABEL_RESOURCE_PLANNING_RECURRENT_INVESTMENT);
			summary.add(index, Math.floor(summaryStage.getRecurrentInvestment() * 0.001) + "");
			
			summary = summaries.get(LABEL_RESOURCE_PLANNING_CURRENT_COST);
			summary.add(index, Math.floor(summaryStage.getRecurrentCost() * 0.001) + "");

			summary = summaries.get(LABEL_RESOURCE_PLANNING_TOTAL_PHASE_COST);
			summary.add(index, Math.floor(summaryStage.getTotalCostofStage() * 0.001) + "");
		}
		return summaries;
	}
}
