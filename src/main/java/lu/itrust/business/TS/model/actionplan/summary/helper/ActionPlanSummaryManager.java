/**
 * 
 */
package lu.itrust.business.TS.model.actionplan.summary.helper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.hibernate.Hibernate;

import lu.itrust.business.TS.model.actionplan.ActionPlanType;
import lu.itrust.business.TS.model.actionplan.summary.SummaryStage;
import lu.itrust.business.TS.model.actionplan.summary.SummaryStandardConformance;
import lu.itrust.business.TS.model.general.Phase;

/**
 * @author eomar
 * 
 */
public class ActionPlanSummaryManager {

	public static final String LABEL_RESOURCE_PLANNING_TOTAL_PHASE_COST = "label.resource.planning.total.phase.cost";
	public static final String LABEL_RESOURCE_PLANNING_RECURRENT_COST = "label.resource.planning.recurrent.cost";
	public static final String LABEL_RESOURCE_PLANNING_RECURRENT_INVESTMENT = "label.resource.planning.recurrent.investment";
	public static final String LABEL_RESOURCE_PLANNING_EXTERNAL_MAINTENANCE = "label.resource.planning.external.maintenance";
	public static final String LABEL_RESOURCE_PLANNING_INTERNAL_MAINTENANCE = "label.resource.planning.internal.maintenance";
	public static final String LABEL_RESOURCE_PLANNING_INVESTMENT = "label.resource.planning.investment";
	public static final String LABEL_RESOURCE_PLANNING_EXTERNAL_WORKLOAD = "label.resource.planning.external.workload";
	public static final String LABEL_RESOURCE_PLANNING_INTERNAL_WORKLOAD = "label.resource.planning.internal.workload";
	public static final String LABEL_RESOURCE_PLANNING = "label.resource.planning";
	public static final String LABEL_PROFITABILITY_ROSI_RELATIF = "label.profitability.rosi.relatif";
	public static final String LABEL_PROFITABILITY_ROSI = "label.profitability.rosi";
	public static final String LABEL_PROFITABILITY_RISK_REDUCTION = "label.profitability.risk.reduction";
	public static final String LABEL_PROFITABILITY_ALE_UNTIL_END = "label.profitability.ale.until.end";
	public static final String LABEL_PROFITABILITY = "label.profitability";
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
		for (SummaryStandardConformance conformance : conformances)
			rows.add(LABEL_CHARACTERISTIC_COMPLIANCE + conformance.getAnalysisStandard().getStandard().getLabel());
		rows.add(LABEL_CHARACTERISTIC_COUNT_MEASURE_PHASE);
		rows.add(LABEL_CHARACTERISTIC_COUNT_MEASURE_IMPLEMENTED);
		rows.add(LABEL_PROFITABILITY);
		rows.add(LABEL_PROFITABILITY_ALE_UNTIL_END);
		rows.add(LABEL_PROFITABILITY_RISK_REDUCTION);
		rows.add(LABEL_PROFITABILITY_ROSI);
		rows.add(LABEL_PROFITABILITY_ROSI_RELATIF);
		rows.add(LABEL_RESOURCE_PLANNING);
		rows.add(LABEL_RESOURCE_PLANNING_INTERNAL_WORKLOAD);
		rows.add(LABEL_RESOURCE_PLANNING_EXTERNAL_WORKLOAD);
		rows.add(LABEL_RESOURCE_PLANNING_INVESTMENT);
		rows.add(LABEL_RESOURCE_PLANNING_INTERNAL_MAINTENANCE);
		rows.add(LABEL_RESOURCE_PLANNING_EXTERNAL_MAINTENANCE);
		rows.add(LABEL_RESOURCE_PLANNING_RECURRENT_INVESTMENT);
		rows.add(LABEL_RESOURCE_PLANNING_RECURRENT_COST);
		rows.add(LABEL_RESOURCE_PLANNING_TOTAL_PHASE_COST);
		return rows;
	}

	public static Map<String, Phase> buildPhase(List<Phase> phases, List<String> extractedPhases) {
		Map<String, Phase> phaseStages = new LinkedHashMap<String, Phase>();

		Phase tmpphase = new Phase(0);

		phaseStages.put("Start(P0)", tmpphase);

		for (Phase phase : phases) {
			Hibernate.initialize(phase);
			String stage = "Phase " + phase.getNumber();
			if (extractedPhases.contains(stage))
				phaseStages.put(stage, phase);
		}
		return phaseStages;
	}

	private static Map<ActionPlanType, List<SummaryStage>> SplitByActionPlanType(List<SummaryStage> summaryStages) {
		Map<ActionPlanType, List<SummaryStage>> actionPlanTypes = new LinkedHashMap<ActionPlanType, List<SummaryStage>>();
		for (SummaryStage summaryStage : summaryStages) {
			List<SummaryStage> stages = actionPlanTypes.get(summaryStage.getActionPlanType());
			if (!actionPlanTypes.containsKey(summaryStage.getActionPlanType()))
				actionPlanTypes.put(summaryStage.getActionPlanType(), stages = new LinkedList<SummaryStage>());
			stages.add(summaryStage);
		}
		return actionPlanTypes;
	}

	public static Map<ActionPlanType, Map<String, List<String>>> buildTables(List<SummaryStage> summaryStages, List<Phase> phases) {
		Map<ActionPlanType, List<SummaryStage>> summariesByActionPlanType = SplitByActionPlanType(summaryStages);
		Map<ActionPlanType, Map<String, List<String>>> summaries = new LinkedHashMap<ActionPlanType, Map<String, List<String>>>(summariesByActionPlanType.size());
		for (ActionPlanType actionPlanType : summariesByActionPlanType.keySet())
			summaries.put(actionPlanType, buildTable(summariesByActionPlanType.get(actionPlanType), phases));
		return summaries;
	}

	public static Map<String, List<String>> buildTable(List<SummaryStage> summaryStages, List<Phase> phases) {
		if (summaryStages.isEmpty())
			return null;
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

			for (SummaryStandardConformance conformance : summaryStage.getConformances()) {
				summary = summaries.get(LABEL_CHARACTERISTIC_COMPLIANCE + conformance.getAnalysisStandard().getStandard().getLabel());
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

			summary = summaries.get(LABEL_RESOURCE_PLANNING_RECURRENT_COST);
			summary.add(index, Math.floor(summaryStage.getRecurrentCost() * 0.001) + "");

			summary = summaries.get(LABEL_RESOURCE_PLANNING_TOTAL_PHASE_COST);
			summary.add(index, Math.floor(summaryStage.getTotalCostofStage() * 0.001) + "");
		}
		return summaries;
	}

	public static Map<ActionPlanType, Map<String, Map<Integer, Object>>> getRows(List<SummaryStage> summaryStages, List<Phase> phases) {

		Map<ActionPlanType, Map<String, Map<Integer, Object>>> result = new LinkedHashMap<ActionPlanType, Map<String, Map<Integer, Object>>>();

		Map<ActionPlanType, List<SummaryStage>> summariesByActionPlanType = SplitByActionPlanType(summaryStages);

		for (ActionPlanType apt : summariesByActionPlanType.keySet()) {

			result.put(apt, generateRowsForActionPlanType(summariesByActionPlanType.get(apt), phases));

		}

		return result;
	}

	private static Map<String, Map<Integer, Object>> generateRowsForActionPlanType(List<SummaryStage> summaryStages, List<Phase> phases) {

		Map<String, Map<Integer, Object>> rowdata = new LinkedHashMap<String, Map<Integer, Object>>();

		SummaryStage stage = getStageFromPhase(0, summaryStages);

		List<String> datas = generateDataList(stage.getConformances());

		int colnumber = 0;
		
		for (String data : datas)
			setValue(data, rowdata, stage, null, colnumber);

		colnumber++;
		
		for (Phase phase : phases) {
			
			stage = getStageFromPhase(phase.getNumber(), summaryStages);
			
			for (String data : datas){
				
				if(stage != null)
					setValue(data, rowdata, stage, phase, colnumber);
			}
			
			colnumber++;

		}
		
		return rowdata;
	}

	public static List<String> generateDataList(List<SummaryStandardConformance> conformances) {
		List<String> result = new ArrayList<String>();

		result.add(LABEL_CHARACTERISTIC);
		result.add(LABEL_PHASE_BEGIN_DATE);
		result.add(LABEL_PHASE_END_DATE);
		for (SummaryStandardConformance conformance : conformances)
			result.add(LABEL_CHARACTERISTIC_COMPLIANCE + conformance.getAnalysisStandard().getStandard().getLabel());
		result.add(LABEL_CHARACTERISTIC_COUNT_MEASURE_PHASE);
		result.add(LABEL_CHARACTERISTIC_COUNT_MEASURE_IMPLEMENTED);
		result.add(LABEL_PROFITABILITY);
		result.add(LABEL_PROFITABILITY_ALE_UNTIL_END);
		result.add(LABEL_PROFITABILITY_RISK_REDUCTION);
		result.add(LABEL_PROFITABILITY_ROSI);
		result.add(LABEL_PROFITABILITY_ROSI_RELATIF);
		result.add(LABEL_RESOURCE_PLANNING);
		result.add(LABEL_RESOURCE_PLANNING_INTERNAL_WORKLOAD);
		result.add(LABEL_RESOURCE_PLANNING_EXTERNAL_WORKLOAD);
		result.add(LABEL_RESOURCE_PLANNING_INVESTMENT);
		result.add(LABEL_RESOURCE_PLANNING_INTERNAL_MAINTENANCE);
		result.add(LABEL_RESOURCE_PLANNING_EXTERNAL_MAINTENANCE);
		result.add(LABEL_RESOURCE_PLANNING_RECURRENT_INVESTMENT);
		result.add(LABEL_RESOURCE_PLANNING_RECURRENT_COST);
		result.add(LABEL_RESOURCE_PLANNING_TOTAL_PHASE_COST);
		return result;
	}

	private static SummaryStage getStageFromPhase(Integer phasenumber, List<SummaryStage> stages) {

		for (SummaryStage stage : stages) {
			if (phasenumber == 0) {
				if (stage.getStage().equals("Start(P0)"))
					return stage;
			} else {
				if (stage.getStage().equals("Phase " + String.valueOf(phasenumber)))
					return stage;
			}
		}

		return null;
	}

	private static void setValue(String data, Map<String, Map<Integer, Object>> values, SummaryStage stage, Phase phase, Integer colnumber) {

		Map<Integer, Object> value = null;

		if (values.get(data) == null)
			values.put(data, value = new LinkedHashMap<Integer, Object>());
		else
			value = values.get(data);

		if(data.startsWith(LABEL_CHARACTERISTIC_COMPLIANCE)) {

			for (SummaryStandardConformance conformance : stage.getConformances())
				if(data.equals(LABEL_CHARACTERISTIC_COMPLIANCE+conformance.getAnalysisStandard().getStandard().getLabel()))
					value.put(colnumber, conformance.getConformance());
			return;
		}
		
		switch (data) {
			case LABEL_CHARACTERISTIC:
				value.put(colnumber, stage.getStage());
				break;
			case LABEL_PHASE_BEGIN_DATE: {
				if (phase == null)
					value.put(colnumber, null);
				else
					value.put(colnumber, phase.getBeginDate());
				break;
			}
			case LABEL_PHASE_END_DATE: {
				if (phase == null)
					value.put(colnumber, null);
				else
					value.put(colnumber, phase.getEndDate());
				break;
			}

			case LABEL_CHARACTERISTIC_COUNT_MEASURE_PHASE:value.put(colnumber, stage.getMeasureCount());break;
			case LABEL_CHARACTERISTIC_COUNT_MEASURE_IMPLEMENTED:value.put(colnumber, stage.getImplementedMeasuresCount());break;
			case LABEL_PROFITABILITY:value.put(colnumber, null);break;
			case LABEL_PROFITABILITY_ALE_UNTIL_END:value.put(colnumber, stage.getTotalALE());break;
			case LABEL_PROFITABILITY_RISK_REDUCTION:value.put(colnumber, stage.getDeltaALE());break;
			case LABEL_PROFITABILITY_ROSI:value.put(colnumber, stage.getROSI());break;
			case LABEL_PROFITABILITY_ROSI_RELATIF:value.put(colnumber, stage.getRelativeROSI());break;
			case LABEL_RESOURCE_PLANNING:value.put(colnumber, null);break;
			case LABEL_RESOURCE_PLANNING_INTERNAL_WORKLOAD:value.put(colnumber, stage.getInternalWorkload());break;
			case LABEL_RESOURCE_PLANNING_EXTERNAL_WORKLOAD:value.put(colnumber, stage.getExternalWorkload());break;
			case LABEL_RESOURCE_PLANNING_INVESTMENT:value.put(colnumber, stage.getInvestment());break;
			case LABEL_RESOURCE_PLANNING_INTERNAL_MAINTENANCE:value.put(colnumber, stage.getInternalMaintenance());break;
			case LABEL_RESOURCE_PLANNING_EXTERNAL_MAINTENANCE:value.put(colnumber, stage.getExternalMaintenance());break;
			case LABEL_RESOURCE_PLANNING_RECURRENT_INVESTMENT:value.put(colnumber, stage.getRecurrentInvestment());break;
			case LABEL_RESOURCE_PLANNING_RECURRENT_COST:value.put(colnumber, stage.getRecurrentCost());break;
			case LABEL_RESOURCE_PLANNING_TOTAL_PHASE_COST:value.put(colnumber, stage.getTotalCostofStage());break;
		}

		

	}

}
