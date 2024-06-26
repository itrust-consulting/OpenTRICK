/**
 *
 */
package lu.itrust.business.ts.model.actionplan.summary.computation.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import lu.itrust.business.ts.constants.Constant;
import lu.itrust.business.ts.model.actionplan.ActionPlanEntry;
import lu.itrust.business.ts.model.actionplan.ActionPlanMode;
import lu.itrust.business.ts.model.actionplan.summary.SummaryStage;
import lu.itrust.business.ts.model.actionplan.summary.computation.SummaryComputation;
import lu.itrust.business.ts.model.actionplan.summary.helper.MaintenanceRecurrentInvestment;
import lu.itrust.business.ts.model.actionplan.summary.helper.SummaryStandardHelper;
import lu.itrust.business.ts.model.actionplan.summary.helper.SummaryValues;
import lu.itrust.business.ts.model.analysis.Analysis;
import lu.itrust.business.ts.model.parameter.helper.ValueFactory;
import lu.itrust.business.ts.model.standard.AnalysisStandard;
import lu.itrust.business.ts.model.standard.measure.AbstractNormalMeasure;
import lu.itrust.business.ts.model.standard.measure.Measure;


/**
 * This class represents the qualitative summary computation for an analysis. It extends the base class SummaryComputation.
 * It performs the computation of summary stages based on qualitative measures and analysis standards.
 */
public class SummaryComputationQualitative extends SummaryComputation {

	private Map<String, AnalysisStandard> analysisStandards;

	/**
	 *
	 */
	public SummaryComputationQualitative(Analysis analysis, List<AnalysisStandard> analysisStandards,
			ValueFactory valueFactory) {
		setAnalysis(analysis);
		setPhases(new ArrayList<>());
		setValueFactory(valueFactory);
		setSummaryStages(new ArrayList<>());
		setActionPlans(analysis.findActionPlan(ActionPlanMode.APQ));
		setMaintenances(new HashMap<>());
		setCurrentValues(new SummaryValues(analysisStandards));
		setPreMaintenance(new MaintenanceRecurrentInvestment());
		setAnalysisStandards(
				analysisStandards.stream().map(analysisStandard -> loadStandard(analysis, analysisStandard))
						.filter(Objects::nonNull)
						.collect(Collectors.toMap(analysisStandard -> analysisStandard.getStandard().getName(),
								Function.identity())));
		generatePreMaintenance(analysisStandards);

		setInternalSetupRate(analysis.findParameter(Constant.PARAMETER_INTERNAL_SETUP_RATE));

		setExternalSetupRate(analysis.findParameter(Constant.PARAMETER_EXTERNAL_SETUP_RATE));

		setSoa(analysis.findParameter(Constant.SOA_THRESHOLD, 100));

		if (!getActionPlans().isEmpty())
			setActionPlanType(getActionPlans().get(0).getActionPlanType());

	}

	/**
	 * Represents an analysis standard used in the computation of a summary in the qualitative analysis.
	 */
	private AnalysisStandard loadStandard(Analysis analysis, AnalysisStandard analysisStandard) {
		AnalysisStandard std = analysis.getAnalysisStandards().get(analysisStandard.getStandard().getName());
		if (std == null)
			std = analysis.getAnalysisStandards().values().stream()
					.filter(e -> e.getStandard().hasSameName(analysisStandard.getStandard())).findAny().orElse(null);
		return std;

	}

	/**
	 * Generates the pre-maintenance based on the provided list of analysis standards.
	 * 
	 * @param analysisStandards the list of analysis standards
	 */
	private void generatePreMaintenance(List<AnalysisStandard> analysisStandards) {
		final Map<String, Boolean> selectedMeasures = analysisStandards.stream()
				.flatMap(analysisStandard -> analysisStandard.getMeasures().stream())
				.collect(Collectors.toMap(Measure::getKey, measure -> true, (e1, e2) -> e1));
		getAnalysisStandards().values().stream().flatMap(standard -> standard.getMeasures().stream())
				.forEach(measure -> {
					if (!(measure.getStatus().equals(Constant.MEASURE_STATUS_NOT_APPLICABLE)
							|| measure.getStatus().equals(Constant.MEASURE_STATUS_EXCLUDE))) {
						if (measure.getImplementationRateValue((ValueFactory) null) >= 100)
							getPreMaintenance().add(measure.getInternalMaintenance(), measure.getExternalMaintenance(),
									measure.getRecurrentInvestment());
						if (selectedMeasures.containsKey(measure.getKey())
								&& !this.getPhases().contains(measure.getPhase()))
							this.getPhases().add(measure.getPhase());
					}
				});
		getPhases().sort((o1, o2) -> Integer.compare(o1.getNumber(), o2.getNumber()));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see lu.itrust.business.ts.model.actionplan.summary.computation.
	 * ISummaryComputation#compute(lu.itrust.business.ts.model.actionplan.
	 * ActionPlanMode)
	 */
	@Override
	public void compute(ActionPlanMode mode) {
		if (getActionPlans().isEmpty() || mode != ActionPlanMode.APQ)
			return;

		final List<Measure> measures;

		if (isFullCostRelated())
			measures = getActionPlans().stream().map(ActionPlanEntry::getMeasure)
					.collect(Collectors.toList());
		else
			measures = Collections.emptyList();

		generateStage(START_P0, true, 0, measures);

		int phase = getActionPlans().get(0).getMeasure().getPhase().getNumber();

		for (ActionPlanEntry actionPlanEntry : getActionPlans()) {
			int measurePhase = actionPlanEntry.getMeasure().getPhase().getNumber();
			if (measurePhase > phase) {
				for (int i = phase; i < measurePhase; i++) {
					generateStage("Phase " + i, false, i, measures);
					resetCurrentData();
				}
				phase = measurePhase;
			}
			nextActionEntry(actionPlanEntry);
		}

		getCurrentValues().conformanceHelper.values().parallelStream().forEach(helper -> helper.conformance = 0);

		generateStage("Phase " + phase, false, phase, measures);

		getSummaryStages().forEach(summary -> {
			summary.getConformances().forEach(conformity -> conformity.setAnalysisStandard(
					getAnalysisStandards().get(conformity.getAnalysisStandard().getStandard().getName())));
			getAnalysis().getSummaries().add(summary);
		});

		// getAnalysis().addSummaryEntries(getSummaryStages());
	}

	private void resetCurrentData() {
		getCurrentValues().conformanceHelper.values().parallelStream().forEach(helper -> helper.conformance = 0);
		getCurrentValues().externalWorkload = 0;
		getCurrentValues().internalWorkload = 0;
		getCurrentValues().implementCostOfPhase = 0;
		getCurrentValues().investment = 0;
		getCurrentValues().measureCost = 0;
		getCurrentValues().measureCount = 0;
		getCurrentValues().totalCost = 0;
	}

	/**
	 * Adds the given action plan entry to the current summary computation.
	 * Updates various values related to measures, workload, investment, and maintenance.
	 *
	 * @param actionPlanEntry The action plan entry to be added.
	 */
	private void nextActionEntry(ActionPlanEntry actionPlanEntry) {
		Measure measure = actionPlanEntry.getMeasure();
		SummaryStandardHelper helper = getCurrentValues().conformanceHelper
				.get(measure.getMeasureDescription().getStandard().getName());
		helper.measures.add(measure);
		getCurrentValues().measureCount++;
		getCurrentValues().implementedCount++;
		getCurrentValues().measureCost += measure.getCost();

		// ****************************************************************
		// * update resource planning values
		// ****************************************************************
		// update internal workload
		getCurrentValues().internalWorkload += measure.getInternalWL();
		// update external workload
		getCurrentValues().externalWorkload += measure.getExternalWL();
		// update investment
		getCurrentValues().investment += measure.getInvestment();
		// in case of a phase calculation multiply internal maintenance with
		// phasetime
		if (!isFullCostRelated()) {
			getCurrentValues().internalMaintenance += measure.getInternalMaintenance();
			// in case of a phase calculation multiply external maintenance with
			// phasetime
			getCurrentValues().externalMaintenance += measure.getExternalMaintenance();
			// update recurrent investment
			getCurrentValues().recurrentInvestment += measure.getRecurrentInvestment();
		}
	}

	/**
	 * Generates a stage for the summary computation.
	 *
	 * @param name     The name of the stage.
	 * @param isFirst  Indicates if it is the first stage.
	 * @param number   The number of the stage.
	 * @param measures The list of measures.
	 */
	private void generateStage(String name, boolean isFirst, int number, final List<Measure> measures) {
		double phaseTime = 0;
		boolean isFirstValidPhase = false;

		if (number > 0)
			phaseTime = getPhases().stream().filter(phase -> phase.getNumber() == number).map(phase -> phase.getTime())
					.findAny().orElse(0d);
		if (isFirst)
			getCurrentValues().implementedCount = 0;
		if (getCurrentValues().previousStage == null)
			getCurrentValues().measureCount = 0;
		else {
			getCurrentValues().measureCount = getCurrentValues().previousStage.getMeasureCount();
			isFirstValidPhase = START_P0.equals(getCurrentValues().previousStage.getStage());
		}

		for (SummaryStandardHelper helper : getCurrentValues().conformanceHelper.values()) {
			int denominator = 0;
			double numerator = 0;
			helper.conformance = 0;
			helper.notCompliantMeasureCount = 0;
			final AnalysisStandard analysisStandard = getAnalysisStandards()
					.get(helper.standard.getStandard().getName());
			for (Measure measure : analysisStandard.getMeasures()) {
				final double imprate = measure.getImplementationRateValue(getValueFactory());
				if (measure.getMeasureDescription().isComputable()
						&& !(measure.getStatus().equals(Constant.MEASURE_STATUS_NOT_APPLICABLE)
								|| measure.getStatus().equals(Constant.MEASURE_STATUS_EXCLUDE))) {
					denominator++;
					numerator += imprate * 0.01;// imprate / 100.0
					if (isFirst && imprate >= Constant.MEASURE_IMPLEMENTATIONRATE_COMPLETE)
						getCurrentValues().implementedCount++;
					else {
						boolean isSelected = helper.measures.contains(measure);
						if (isSelected) {
							numerator += (1.0 - imprate * 0.01);
							getCurrentValues().measureCount++;
						}

						if (imprate < getSoa() && measure instanceof AbstractNormalMeasure
								&& (!isSelected || measure.getPhase().getNumber() > number))
							helper.notCompliantMeasureCount++;
					}

				}
			}

			if (denominator == 0)
				helper.conformance = 0;
			else
				helper.conformance += (numerator / denominator);
		}

		final MaintenanceRecurrentInvestment maintenanceRecurrentInvestment = getMaintenances().computeIfAbsent(
				number - 1,
				k -> new MaintenanceRecurrentInvestment());

		if (isFullCostRelated()) {
			if (number > 0) {
				measures.stream().forEach(m -> {
					final double implR = (m.getPhase().getNumber() < number ? 100
							: m.getImplementationRateValue(getValueFactory())) * 0.01;
					maintenanceRecurrentInvestment.add(m.getInternalMaintenance() * implR,
							m.getExternalMaintenance() * implR, m.getRecurrentInvestment() * implR);

				});
			}
		} else {
			if (isFirstValidPhase) {
				getCurrentValues().internalMaintenance += getPreMaintenance().getInternalMaintenance();
				getCurrentValues().externalMaintenance += getPreMaintenance().getExternalMaintenance();
				getCurrentValues().recurrentInvestment += getPreMaintenance().getRecurrentInvestment();
			}
			getMaintenances().computeIfAbsent(number, k -> new MaintenanceRecurrentInvestment()).update(
					getCurrentValues().internalMaintenance,
					getCurrentValues().externalMaintenance, getCurrentValues().recurrentInvestment);
		}

		// ****************************************************************
		// * create summary stage object
		// ****************************************************************
		SummaryStage summaryStage = new SummaryStage();

		// add values to summary stage object
		summaryStage.setStage(name);
		summaryStage.setActionPlanType(getActionPlanType());

		for (String key : getCurrentValues().conformanceHelper.keySet()) {
			final SummaryStandardHelper standardHelper = getCurrentValues().conformanceHelper.get(key);
			summaryStage.addConformance(standardHelper.standard, standardHelper.conformance,
					standardHelper.notCompliantMeasureCount);
		}

		if (getCurrentValues().previousStage != null)
			summaryStage.setMeasureCount(getCurrentValues().implementedCount
					- getCurrentValues().previousStage.getImplementedMeasuresCount());
		else
			summaryStage.setMeasureCount(getCurrentValues().measureCount);

		summaryStage.setImplementedMeasuresCount(getCurrentValues().implementedCount);
		summaryStage.setCostOfMeasures(getCurrentValues().measureCost);
		summaryStage.setInternalWorkload(getCurrentValues().internalWorkload);
		summaryStage.setExternalWorkload(getCurrentValues().externalWorkload);
		summaryStage.setInvestment(getCurrentValues().investment);

		if (isFirstValidPhase && !isFullCostRelated()) {
			summaryStage.setInternalMaintenance((getPreMaintenance().getInternalMaintenance()
					+ maintenanceRecurrentInvestment.getInternalMaintenance()) * phaseTime);
			summaryStage.setExternalMaintenance((getPreMaintenance().getExternalMaintenance()
					+ maintenanceRecurrentInvestment.getExternalMaintenance()) * phaseTime);
			summaryStage.setRecurrentInvestment((getPreMaintenance().getRecurrentInvestment()
					+ maintenanceRecurrentInvestment.getRecurrentInvestment()) * phaseTime);
		} else {
			summaryStage.setInternalMaintenance(maintenanceRecurrentInvestment.getInternalMaintenance() * phaseTime);
			summaryStage.setExternalMaintenance(maintenanceRecurrentInvestment.getExternalMaintenance() * phaseTime);
			summaryStage.setRecurrentInvestment(maintenanceRecurrentInvestment.getRecurrentInvestment() * phaseTime);
		}

		summaryStage.setRecurrentCost(
				getCurrentValues().recurrentCost = summaryStage.getInternalMaintenance() * getInternalSetupRate()
						+ summaryStage.getExternalMaintenance() * getExternalSetupRate()
						+ summaryStage.getRecurrentInvestment());

		// update total cost
		summaryStage.setImplementCostOfPhase(
				getCurrentValues().implementCostOfPhase = (getCurrentValues().internalWorkload * getInternalSetupRate())
						+ (getCurrentValues().externalWorkload * getExternalSetupRate())
						+ getCurrentValues().investment);

		// in case of a phase calculation multiply external maintenance,
		// internal maintenance with
		// phasetime and with internal and external setup as well as investment
		// with phasetime

		summaryStage.setTotalCostofStage(getCurrentValues().totalCost += (summaryStage.getRecurrentCost()
				+ summaryStage.getImplementCostOfPhase()));

		// ****************************************************************
		// * add summary stage to list of summary stages
		// ****************************************************************
		getSummaryStages().add(summaryStage);

		getCurrentValues().previousStage = summaryStage;

	}

	/**
	 * @return the analysisStandards
	 */
	public Map<String, AnalysisStandard> getAnalysisStandards() {
		return analysisStandards;
	}

	/**
	 * @param analysisStandards the analysisStandards to set
	 */
	public void setAnalysisStandards(Map<String, AnalysisStandard> analysisStandards) {
		this.analysisStandards = analysisStandards;
	}
}
