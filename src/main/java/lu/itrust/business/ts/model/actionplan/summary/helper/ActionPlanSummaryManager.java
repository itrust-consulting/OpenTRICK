/**
 *
 */
package lu.itrust.business.ts.model.actionplan.summary.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lu.itrust.business.ts.model.actionplan.ActionPlanType;
import lu.itrust.business.ts.model.actionplan.summary.SummaryStage;
import lu.itrust.business.ts.model.actionplan.summary.SummaryStandardConformance;
import lu.itrust.business.ts.model.general.Phase;


/**
 * The ActionPlanSummaryManager class is responsible for managing the action plan summary data.
 * It provides methods to extract phase rows, build phase tables, split summaries by action plan type,
 * and build tables and charts based on the summary stages and phases.
 */
public class ActionPlanSummaryManager {

	public static final String LABEL_RESOURCE_PLANNING_TOTAL_PHASE_COST = "label.resource.planning.total.phase.cost";
	public static final String LABEL_RESOURCE_PLANNING_RECURRENT_COST = "label.resource.planning.total.recurrent.cost";
	public static final String LABEL_RESOURCE_PLANNING_IMPLEMENT_PHASE_COST = "label.resource.planning.total.implement.phase.cost";
	public static final String LABEL_RESOURCE_PLANNING_RECURRENT_INVESTMENT = "label.resource.planning.recurrent.investment";
	public static final String LABEL_RESOURCE_PLANNING_EXTERNAL_MAINTENANCE = "label.resource.planning.external.maintenance";
	public static final String LABEL_RESOURCE_PLANNING_INTERNAL_MAINTENANCE = "label.resource.planning.internal.maintenance";
	public static final String LABEL_RESOURCE_PLANNING_INVESTMENT = "label.resource.planning.investment";
	public static final String LABEL_RESOURCE_PLANNING_EXTERNAL_WORKLOAD = "label.resource.planning.external.workload";
	public static final String LABEL_RESOURCE_PLANNING_INTERNAL_WORKLOAD = "label.resource.planning.internal.workload";
	public static final String LABEL_RESOURCE_PLANNING = "label.resource.planning";
	public static final String LABEL_PROFITABILITY_ROSI_RELATIF = "label.profitability.rosi.relatif";
	public static final String LABEL_PROFITABILITY_ROSI = "label.profitability.rosi";
	public static final String LABEL_PROFITABILITY_AVERAGE_YEARLY_COST_OF_PHASE = "label.profitability.average_yearly_cost_of_phase";
	public static final String LABEL_PROFITABILITY_RISK_REDUCTION = "label.profitability.risk.reduction";
	public static final String LABEL_PROFITABILITY_ALE_UNTIL_END = "label.profitability.ale.until.end";
	public static final String LABEL_PROFITABILITY = "label.profitability";
	public static final String LABEL_CHARACTERISTIC_COUNT_MEASURE_IMPLEMENTED = "label.characteristic.count.measure.implemented";
	public static final String LABEL_CHARACTERISTIC_COUNT_MEASURE_PHASE = "label.characteristic.count.measure.phase";
	public static final String LABEL_CHARACTERISTIC_COUNT_NOT_COMPLIANT_MEASURE = "label.characteristic.count.not_compliant_measure_";
	public static final String LABEL_CHARACTERISTIC_COMPLIANCE = "label.characteristic.compliance";
	public static final String LABEL_PHASE_END_DATE = "label.phase.end.date";
	public static final String LABEL_PHASE_BEGIN_DATE = "label.phase.begin.date";
	public static final String LABEL_CHARACTERISTIC = "label.characteristic";

	/**
	 * Extracts the phase row from a list of summary stages.
	 *
	 * @param summaryStages the list of summary stages
	 * @return a list of unique phase names extracted from the summary stages
	 */
	public static List<String> extractPhaseRow(List<SummaryStage> summaryStages) {
		return summaryStages.stream().map(SummaryStage::getStage).distinct().collect(Collectors.toList());
	}

	/**
	 * Builds a map of phases based on the provided list of phases and extracted phases.
	 *
	 * @param phases          the list of all phases
	 * @param extractedPhases the list of extracted phases
	 * @return a map of phases with their corresponding stage names
	 */
	public static Map<String, Phase> buildPhase(List<Phase> phases, List<String> extractedPhases) {
		Map<String, Phase> phaseStages = new LinkedHashMap<>();

		Phase tmpphase = new Phase(0);

		phaseStages.put("Start(P0)", tmpphase);

		for (Phase phase : phases) {
			String stage = "Phase " + phase.getNumber();
			if (extractedPhases.contains(stage))
				phaseStages.put(stage, phase);
		}
		return phaseStages;
	}

	/**
	 * Splits the given list of summary stages by action plan type.
	 *
	 * @param summaryStages the list of summary stages to be split
	 * @return a map where the keys are action plan types and the values are lists of summary stages
	 */
	private static Map<ActionPlanType, List<SummaryStage>> splitByActionPlanType(List<SummaryStage> summaryStages) {
		return summaryStages.stream().collect(Collectors.groupingBy(SummaryStage::getActionPlanType));
	}

	/**
	 * Builds tables for action plan summaries based on the provided summary stages and phases.
	 *
	 * @param summaryStages The list of summary stages.
	 * @param phases The list of phases.
	 * @return A map containing action plan types as keys and their corresponding tables as values.
	 */
	public static Map<ActionPlanType, Map<String, List<String>>> buildTables(List<SummaryStage> summaryStages,
			List<Phase> phases) {
		Map<ActionPlanType, List<SummaryStage>> summariesByActionPlanType = splitByActionPlanType(summaryStages);
		Map<ActionPlanType, Map<String, List<String>>> summaries = new LinkedHashMap<>(
				summariesByActionPlanType.size());

		summariesByActionPlanType.forEach((k, v) -> summaries.put(k, buildTable(v, phases)));

		return summaries;
	}

	/**
	 * Builds a table summarizing the action plan based on the provided summary stages and phases.
	 *
	 * @param summaryStages The list of summary stages.
	 * @param phases The list of phases.
	 * @return A map representing the built table, where the keys are the table rows and the values are the corresponding row data.
	 */
	public static Map<String, List<String>> buildTable(List<SummaryStage> summaryStages, List<Phase> phases) {
		if (summaryStages.isEmpty())
			return Collections.emptyMap();
		List<String> firstRows = generateHeader(summaryStages.get(0).getConformances());

		Map<String, List<String>> summaries = new LinkedHashMap<>(firstRows.size());

		List<String> rowHeaders = extractPhaseRow(summaryStages);

		Map<String, Phase> phaseStages = buildPhase(phases, rowHeaders);

		for (String string : firstRows) {
			List<String> rows = summaries.get(string);
			if (rows == null) {
				if (!(string.equals(LABEL_PROFITABILITY) || string.equals(LABEL_RESOURCE_PLANNING)))
					summaries.put(string, rows = new ArrayList<>(rowHeaders.size()));
				else
					summaries.put(string, rows = new ArrayList<>());
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
				summary = summaries.get(
						LABEL_CHARACTERISTIC_COMPLIANCE + conformance.getAnalysisStandard().getStandard().getName());
				summary.add(index, (int) (conformance.getConformance() * 100) + "");
			}

			summary = summaries.get(LABEL_CHARACTERISTIC_COUNT_MEASURE_PHASE);
			summary.add(index, summaryStage.getMeasureCount() + "");

			summary = summaries.get(LABEL_CHARACTERISTIC_COUNT_MEASURE_IMPLEMENTED);
			summary.add(index, summaryStage.getImplementedMeasuresCount() + "");

			for (SummaryStandardConformance conformance : summaryStage.getConformances()) {
				summary = summaries.get(LABEL_CHARACTERISTIC_COUNT_NOT_COMPLIANT_MEASURE
						+ conformance.getAnalysisStandard().getStandard().getName());
				summary.add(index, conformance.getNotCompliantMeasureCount() + "");
			}

			summary = summaries.get(LABEL_PROFITABILITY_ALE_UNTIL_END);
			summary.add(index, Math.floor(summaryStage.getTotalALE() * 0.001) + "");

			summary = summaries.get(LABEL_PROFITABILITY_RISK_REDUCTION);
			summary.add(index, Math.floor(summaryStage.getDeltaALE() * 0.001) + "");

			summary = summaries.get(LABEL_PROFITABILITY_AVERAGE_YEARLY_COST_OF_PHASE);
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

			summary = summaries.get(LABEL_RESOURCE_PLANNING_IMPLEMENT_PHASE_COST);
			summary.add(index, Math.floor(summaryStage.getImplementCostOfPhase() * 0.001) + "");

			summary = summaries.get(LABEL_RESOURCE_PLANNING_RECURRENT_COST);
			summary.add(index, Math.floor(summaryStage.getRecurrentCost() * 0.001) + "");

			summary = summaries.get(LABEL_RESOURCE_PLANNING_TOTAL_PHASE_COST);
			summary.add(index, Math.floor(summaryStage.getTotalCostofStage() * 0.001) + "");
		}
		return summaries;
	}

	/**
	 * Builds the chart data based on the provided summary stages and phases.
	 *
	 * @param summaryStages The list of summary stages.
	 * @param phases The list of phases.
	 * @return A map containing the chart data, where the keys are the row headers and the values are the corresponding data rows.
	 */
	public static Map<String, List<Object>> buildChartData(List<SummaryStage> summaryStages, List<Phase> phases) {
		if (summaryStages.isEmpty())
			return Collections.emptyMap();
		List<String> firstRows = generateHeader(summaryStages.get(0).getConformances());

		Map<String, List<Object>> summaries = new LinkedHashMap<>(firstRows.size());

		List<String> rowHeaders = extractPhaseRow(summaryStages);

		Map<String, Phase> phaseStages = buildPhase(phases, rowHeaders);

		for (String string : firstRows) {
			List<Object> rows = summaries.get(string);
			if (rows == null) {
				if (!(string.equals(LABEL_PROFITABILITY) || string.equals(LABEL_RESOURCE_PLANNING)))
					summaries.put(string, rows = new ArrayList<>(rowHeaders.size()));
				else
					summaries.put(string, rows = new ArrayList<>());
			}
			if (rows.isEmpty() && LABEL_CHARACTERISTIC.equals(string))
				rows.addAll(rowHeaders);

		}

		List<Object> summary = null;

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
				summary = summaries.get(
						LABEL_CHARACTERISTIC_COMPLIANCE + conformance.getAnalysisStandard().getStandard().getName());
				summary.add(index, conformance.getConformance());
			}

			summary = summaries.get(LABEL_CHARACTERISTIC_COUNT_MEASURE_PHASE);
			summary.add(index, summaryStage.getMeasureCount());

			summary = summaries.get(LABEL_CHARACTERISTIC_COUNT_MEASURE_IMPLEMENTED);
			summary.add(index, summaryStage.getImplementedMeasuresCount());

			for (SummaryStandardConformance conformance : summaryStage.getConformances()) {
				summary = summaries.get(LABEL_CHARACTERISTIC_COUNT_NOT_COMPLIANT_MEASURE
						+ conformance.getAnalysisStandard().getStandard().getName());
				summary.add(index, conformance.getNotCompliantMeasureCount());
			}

			summary = summaries.get(LABEL_PROFITABILITY_ALE_UNTIL_END);
			summary.add(index, Math.floor(summaryStage.getTotalALE() * 0.001));

			summary = summaries.get(LABEL_PROFITABILITY_RISK_REDUCTION);
			summary.add(index, Math.floor(summaryStage.getDeltaALE() * 0.001));

			summary = summaries.get(LABEL_PROFITABILITY_AVERAGE_YEARLY_COST_OF_PHASE);
			summary.add(index, Math.floor(summaryStage.getCostOfMeasures() * 0.001));

			summary = summaries.get(LABEL_PROFITABILITY_ROSI);
			summary.add(index, Math.floor(summaryStage.getROSI() * 0.001));

			summary = summaries.get(LABEL_PROFITABILITY_ROSI_RELATIF);
			summary.add(index, Math.floor(summaryStage.getRelativeROSI()));

			summary = summaries.get(LABEL_RESOURCE_PLANNING_INTERNAL_WORKLOAD);
			summary.add(index, summaryStage.getInternalWorkload());

			summary = summaries.get(LABEL_RESOURCE_PLANNING_EXTERNAL_WORKLOAD);
			summary.add(index, summaryStage.getExternalWorkload());

			summary = summaries.get(LABEL_RESOURCE_PLANNING_INVESTMENT);
			summary.add(index, Math.floor(summaryStage.getInvestment() * 0.001));

			summary = summaries.get(LABEL_RESOURCE_PLANNING_INTERNAL_MAINTENANCE);
			summary.add(index, Math.floor(summaryStage.getInternalMaintenance()));

			summary = summaries.get(LABEL_RESOURCE_PLANNING_EXTERNAL_MAINTENANCE);
			summary.add(index, Math.floor(summaryStage.getExternalMaintenance()));

			summary = summaries.get(LABEL_RESOURCE_PLANNING_RECURRENT_INVESTMENT);
			summary.add(index, Math.floor(summaryStage.getRecurrentInvestment() * 0.001));

			summary = summaries.get(LABEL_RESOURCE_PLANNING_IMPLEMENT_PHASE_COST);
			summary.add(index, Math.floor(summaryStage.getImplementCostOfPhase() * 0.001));

			summary = summaries.get(LABEL_RESOURCE_PLANNING_RECURRENT_COST);
			summary.add(index, Math.floor(summaryStage.getRecurrentCost() * 0.001));

			summary = summaries.get(LABEL_RESOURCE_PLANNING_TOTAL_PHASE_COST);
			summary.add(index, Math.floor(summaryStage.getTotalCostofStage() * 0.001));
		}
		return summaries;
	}

	/**
	 * Builds a map of raw data based on the provided summary stages and phases.
	 *
	 * @param summaryStages The list of summary stages.
	 * @param phases The list of phases.
	 * @return A map containing the raw data.
	 */
	public static Map<String, List<Object>> buildRawData(List<SummaryStage> summaryStages, List<Phase> phases) {
		if (summaryStages.isEmpty())
			return Collections.emptyMap();
		List<String> firstRows = generateHeader(summaryStages.get(0).getConformances());

		Map<String, List<Object>> summaries = new LinkedHashMap<>(firstRows.size());

		List<String> rowHeaders = extractPhaseRow(summaryStages);

		Map<String, Phase> phaseStages = buildPhase(phases, rowHeaders);

		for (String string : firstRows) {
			List<Object> rows = summaries.get(string);
			if (rows == null) {
				if (!(string.equals(LABEL_PROFITABILITY) || string.equals(LABEL_RESOURCE_PLANNING)))
					summaries.put(string, rows = new ArrayList<>(rowHeaders.size()));
				else
					summaries.put(string, rows = new ArrayList<>());
			}
			if (rows.isEmpty() && LABEL_CHARACTERISTIC.equals(string))
				rows.addAll(rowHeaders);

		}

		List<Object> summary = null;

		for (SummaryStage summaryStage : summaryStages) {
			int index = rowHeaders.indexOf(summaryStage.getStage());
			if (index == -1)
				throw new IllegalArgumentException("Bad index....");

			Phase phase = phaseStages.get(summaryStage.getStage());

			if (phase != null) {
				if (phase.getNumber() == 0) {
					summary = summaries.get(LABEL_PHASE_BEGIN_DATE);
					summary.add(index, null);

					summary = summaries.get(LABEL_PHASE_END_DATE);
					summary.add(index, null);
				} else {
					summary = summaries.get(LABEL_PHASE_BEGIN_DATE);
					summary.add(index, phase.getBeginDate());

					summary = summaries.get(LABEL_PHASE_END_DATE);
					summary.add(index, phase.getEndDate());
				}
			}

			for (SummaryStandardConformance conformance : summaryStage.getConformances()) {
				summary = summaries.get(
						LABEL_CHARACTERISTIC_COMPLIANCE + conformance.getAnalysisStandard().getStandard().getName());
				summary.add(index, (int) (conformance.getConformance() * 100));
			}

			summary = summaries.get(LABEL_CHARACTERISTIC_COUNT_MEASURE_PHASE);
			summary.add(index, summaryStage.getMeasureCount());

			summary = summaries.get(LABEL_CHARACTERISTIC_COUNT_MEASURE_IMPLEMENTED);
			summary.add(index, summaryStage.getImplementedMeasuresCount());

			for (SummaryStandardConformance conformance : summaryStage.getConformances()) {
				summary = summaries.get(LABEL_CHARACTERISTIC_COUNT_NOT_COMPLIANT_MEASURE
						+ conformance.getAnalysisStandard().getStandard().getName());
				summary.add(index, conformance.getNotCompliantMeasureCount());
			}

			summary = summaries.get(LABEL_PROFITABILITY_ALE_UNTIL_END);
			summary.add(index, summaryStage.getTotalALE());

			summary = summaries.get(LABEL_PROFITABILITY_RISK_REDUCTION);
			summary.add(index, summaryStage.getDeltaALE());

			summary = summaries.get(LABEL_PROFITABILITY_AVERAGE_YEARLY_COST_OF_PHASE);
			summary.add(index, summaryStage.getCostOfMeasures());

			summary = summaries.get(LABEL_PROFITABILITY_ROSI);
			summary.add(index, summaryStage.getROSI());

			summary = summaries.get(LABEL_PROFITABILITY_ROSI_RELATIF);
			summary.add(index, summaryStage.getRelativeROSI());

			summary = summaries.get(LABEL_RESOURCE_PLANNING_INTERNAL_WORKLOAD);
			summary.add(index, summaryStage.getInternalWorkload());

			summary = summaries.get(LABEL_RESOURCE_PLANNING_EXTERNAL_WORKLOAD);
			summary.add(index, summaryStage.getExternalWorkload());

			summary = summaries.get(LABEL_RESOURCE_PLANNING_INVESTMENT);
			summary.add(index, summaryStage.getInvestment());

			summary = summaries.get(LABEL_RESOURCE_PLANNING_INTERNAL_MAINTENANCE);
			summary.add(index, summaryStage.getInternalMaintenance());

			summary = summaries.get(LABEL_RESOURCE_PLANNING_EXTERNAL_MAINTENANCE);
			summary.add(index, summaryStage.getExternalMaintenance());

			summary = summaries.get(LABEL_RESOURCE_PLANNING_RECURRENT_INVESTMENT);
			summary.add(index, summaryStage.getRecurrentInvestment());

			summary = summaries.get(LABEL_RESOURCE_PLANNING_IMPLEMENT_PHASE_COST);
			summary.add(index, summaryStage.getImplementCostOfPhase());

			summary = summaries.get(LABEL_RESOURCE_PLANNING_RECURRENT_COST);
			summary.add(index, summaryStage.getRecurrentCost());

			summary = summaries.get(LABEL_RESOURCE_PLANNING_TOTAL_PHASE_COST);
			summary.add(index, summaryStage.getTotalCostofStage());
		}
		return summaries;
	}

	/**
	 * Retrieves a map of rows for each action plan type, based on the provided summary stages and phases.
	 *
	 * @param summaryStages The list of summary stages.
	 * @param phases The list of phases.
	 * @return A map containing rows for each action plan type.
	 */
	public static Map<ActionPlanType, Map<String, Map<Integer, Object>>> getRows(List<SummaryStage> summaryStages,
			List<Phase> phases) {
		Map<ActionPlanType, Map<String, Map<Integer, Object>>> result = new LinkedHashMap<>();
		Map<ActionPlanType, List<SummaryStage>> summariesByActionPlanType = splitByActionPlanType(summaryStages);
		for (ActionPlanType apt : summariesByActionPlanType.keySet())
			result.put(apt, generateRowsForActionPlanType(summariesByActionPlanType.get(apt), phases));
		return result;
	}

	/**
	 * Generates rows for the action plan type based on the provided summary stages and phases.
	 *
	 * @param summaryStages The list of summary stages.
	 * @param phases The list of phases.
	 * @return A map containing the generated rows for the action plan type.
	 */
	private static Map<String, Map<Integer, Object>> generateRowsForActionPlanType(List<SummaryStage> summaryStages,
			List<Phase> phases) {

		Map<String, Map<Integer, Object>> rowdata = new LinkedHashMap<>();

		SummaryStage stage = getStageFromPhase(0, summaryStages);

		List<String> datas = generateHeader(stage.getConformances());

		int colnumber = 0;

		for (String data : datas)
			setValue(data, rowdata, stage, null, colnumber);

		colnumber++;

		for (Phase phase : phases) {

			stage = getStageFromPhase(phase.getNumber(), summaryStages);

			for (String data : datas) {

				if (stage != null)
					setValue(data, rowdata, stage, phase, colnumber);
			}

			colnumber++;

		}

		return rowdata;
	}

	/**
	 * Generates the header for the action plan summary.
	 * 
	 * @param conformances the list of summary standard conformances
	 * @return the list of header rows
	 */
	public static List<String> generateHeader(List<SummaryStandardConformance> conformances) {
		List<String> rows = new ArrayList<>();
		rows.add(LABEL_CHARACTERISTIC);
		rows.add(LABEL_PHASE_BEGIN_DATE);
		rows.add(LABEL_PHASE_END_DATE);
		for (SummaryStandardConformance conformance : conformances)
			rows.add(LABEL_CHARACTERISTIC_COMPLIANCE + conformance.getAnalysisStandard().getStandard().getName());
		rows.add(LABEL_CHARACTERISTIC_COUNT_MEASURE_PHASE);
		rows.add(LABEL_CHARACTERISTIC_COUNT_MEASURE_IMPLEMENTED);
		for (SummaryStandardConformance conformance : conformances)
			rows.add(LABEL_CHARACTERISTIC_COUNT_NOT_COMPLIANT_MEASURE
					+ conformance.getAnalysisStandard().getStandard().getName());
		rows.add(LABEL_PROFITABILITY);
		rows.add(LABEL_PROFITABILITY_ALE_UNTIL_END);
		rows.add(LABEL_PROFITABILITY_RISK_REDUCTION);
		rows.add(LABEL_PROFITABILITY_AVERAGE_YEARLY_COST_OF_PHASE);
		rows.add(LABEL_PROFITABILITY_ROSI);
		rows.add(LABEL_PROFITABILITY_ROSI_RELATIF);
		rows.add(LABEL_RESOURCE_PLANNING);
		rows.add(LABEL_RESOURCE_PLANNING_INTERNAL_WORKLOAD);
		rows.add(LABEL_RESOURCE_PLANNING_EXTERNAL_WORKLOAD);
		rows.add(LABEL_RESOURCE_PLANNING_INVESTMENT);
		rows.add(LABEL_RESOURCE_PLANNING_INTERNAL_MAINTENANCE);
		rows.add(LABEL_RESOURCE_PLANNING_EXTERNAL_MAINTENANCE);
		rows.add(LABEL_RESOURCE_PLANNING_RECURRENT_INVESTMENT);
		rows.add(LABEL_RESOURCE_PLANNING_IMPLEMENT_PHASE_COST);
		rows.add(LABEL_RESOURCE_PLANNING_RECURRENT_COST);
		rows.add(LABEL_RESOURCE_PLANNING_TOTAL_PHASE_COST);
		return rows;
	}

	/**
	 * Represents a stage in the summary of an action plan.
	 */
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

	/**
	 * Sets the value for a specific data in the given map of values based on the provided parameters.
	 *
	 * @param data       The data for which the value needs to be set.
	 * @param values     The map of values to update.
	 * @param stage      The summary stage.
	 * @param phase      The phase.
	 * @param colnumber  The column number.
	 */
	private static void setValue(String data, Map<String, Map<Integer, Object>> values, SummaryStage stage, Phase phase,
			Integer colnumber) {

		Map<Integer, Object> value = null;

		if (values.get(data) == null)
			values.put(data, value = new LinkedHashMap<Integer, Object>());
		else
			value = values.get(data);

		if (data.startsWith(LABEL_CHARACTERISTIC_COMPLIANCE)) {

			for (SummaryStandardConformance conformance : stage.getConformances())
				if (data.equals(
						LABEL_CHARACTERISTIC_COMPLIANCE + conformance.getAnalysisStandard().getStandard().getName()))
					value.put(colnumber, conformance.getConformance());
			return;
		}

		if (data.startsWith(LABEL_CHARACTERISTIC_COUNT_NOT_COMPLIANT_MEASURE)) {
			for (SummaryStandardConformance conformance : stage.getConformances())
				if (data.equals(LABEL_CHARACTERISTIC_COUNT_NOT_COMPLIANT_MEASURE
						+ conformance.getAnalysisStandard().getStandard().getName()))
					value.put(colnumber, conformance.getNotCompliantMeasureCount());
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

			case LABEL_CHARACTERISTIC_COUNT_MEASURE_PHASE:
				value.put(colnumber, stage.getMeasureCount());
				break;
			case LABEL_CHARACTERISTIC_COUNT_MEASURE_IMPLEMENTED:
				value.put(colnumber, stage.getImplementedMeasuresCount());
				break;
			case LABEL_PROFITABILITY:
				value.put(colnumber, null);
				break;
			case LABEL_PROFITABILITY_ALE_UNTIL_END:
				value.put(colnumber, stage.getTotalALE());
				break;
			case LABEL_PROFITABILITY_RISK_REDUCTION:
				value.put(colnumber, stage.getDeltaALE());
				break;
			case LABEL_PROFITABILITY_AVERAGE_YEARLY_COST_OF_PHASE:
				value.put(colnumber, stage.getCostOfMeasures());
				break;
			case LABEL_PROFITABILITY_ROSI:
				value.put(colnumber, stage.getROSI());
				break;
			case LABEL_PROFITABILITY_ROSI_RELATIF:
				value.put(colnumber, stage.getRelativeROSI());
				break;
			case LABEL_RESOURCE_PLANNING:
				value.put(colnumber, null);
				break;
			case LABEL_RESOURCE_PLANNING_INTERNAL_WORKLOAD:
				value.put(colnumber, stage.getInternalWorkload());
				break;
			case LABEL_RESOURCE_PLANNING_EXTERNAL_WORKLOAD:
				value.put(colnumber, stage.getExternalWorkload());
				break;
			case LABEL_RESOURCE_PLANNING_INVESTMENT:
				value.put(colnumber, stage.getInvestment());
				break;
			case LABEL_RESOURCE_PLANNING_INTERNAL_MAINTENANCE:
				value.put(colnumber, stage.getInternalMaintenance());
				break;
			case LABEL_RESOURCE_PLANNING_EXTERNAL_MAINTENANCE:
				value.put(colnumber, stage.getExternalMaintenance());
				break;
			case LABEL_RESOURCE_PLANNING_RECURRENT_INVESTMENT:
				value.put(colnumber, stage.getRecurrentInvestment());
				break;
			case LABEL_RESOURCE_PLANNING_IMPLEMENT_PHASE_COST:
				value.put(colnumber, stage.getImplementCostOfPhase());
				break;
			case LABEL_RESOURCE_PLANNING_RECURRENT_COST:
				value.put(colnumber, stage.getRecurrentCost());
				break;
			case LABEL_RESOURCE_PLANNING_TOTAL_PHASE_COST:
				value.put(colnumber, stage.getTotalCostofStage());
				break;
		}

	}

}
