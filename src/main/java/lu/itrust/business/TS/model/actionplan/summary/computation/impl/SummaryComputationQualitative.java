/**
 * 
 */
package lu.itrust.business.TS.model.actionplan.summary.computation.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.model.actionplan.ActionPlanEntry;
import lu.itrust.business.TS.model.actionplan.ActionPlanMode;
import lu.itrust.business.TS.model.actionplan.summary.SummaryStage;
import lu.itrust.business.TS.model.actionplan.summary.computation.SummaryComputation;
import lu.itrust.business.TS.model.actionplan.summary.helper.MaintenanceRecurrentInvestment;
import lu.itrust.business.TS.model.actionplan.summary.helper.SummaryStandardHelper;
import lu.itrust.business.TS.model.actionplan.summary.helper.SummaryValues;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.parameter.helper.ValueFactory;
import lu.itrust.business.TS.model.standard.AnalysisStandard;
import lu.itrust.business.TS.model.standard.measure.Measure;
import lu.itrust.business.TS.model.standard.measure.NormalMeasure;

/**
 * @author eomar
 *
 */
public class SummaryComputationQualitative extends SummaryComputation {

	private Map<String, AnalysisStandard> analysisStandards;

	/**
	 * 
	 */
	public SummaryComputationQualitative(Analysis analysis, List<AnalysisStandard> analysisStandards) {
		setAnalysis(analysis);
		setPhases(new ArrayList<>());
		setSummaryStages(new ArrayList<SummaryStage>());
		setActionPlans(analysis.getActionPlan(ActionPlanMode.APQ));
		setMaintenances(new HashMap<>());
		setCurrentValues(new SummaryValues(analysisStandards));
		setPreMaintenance(new MaintenanceRecurrentInvestment());
		this.setAnalysisStandards(analysisStandards.stream().map(analysisStandard -> analysis.getAnalysisStandardByLabel(analysisStandard.getStandard().getLabel()))
				.collect(Collectors.toMap(analysisStandard -> analysisStandard.getStandard().getLabel(), Function.identity())));
		generatePreMaintenance(analysisStandards);

		setInternalSetupRate(analysis.getParameter(Constant.PARAMETER_INTERNAL_SETUP_RATE));

		setExternalSetupRate(analysis.getParameter(Constant.PARAMETER_EXTERNAL_SETUP_RATE));

		setSoa(analysis.getParameter(Constant.SOA_THRESHOLD, 100));

		if (!getActionPlans().isEmpty())
			setActionPlanType(getActionPlans().get(0).getActionPlanType());

	}

	private void generatePreMaintenance(List<AnalysisStandard> analysisStandards) {
		Map<String, Boolean> selectedMeasures = analysisStandards.stream().flatMap(analysisStandard -> analysisStandard.getMeasures().stream())
				.collect(Collectors.toMap(Measure::getKey, measure -> true));
		getAnalysisStandards().values().stream().flatMap(standard -> standard.getMeasures().stream()).forEach(measure -> {
			if (!measure.getStatus().equals(Constant.MEASURE_STATUS_NOT_APPLICABLE)) {
				if (measure.getImplementationRateValue((ValueFactory) null) >= 100)
					getPreMaintenance().add(measure.getInternalMaintenance(), measure.getExternalMaintenance(), measure.getRecurrentInvestment());
				if (selectedMeasures.containsKey(measure.getKey()) && !this.getPhases().contains(measure.getPhase()))
					this.getPhases().add(measure.getPhase());
			}
		});
		getPhases().sort((o1, o2) -> Integer.compare(o1.getNumber(), o2.getNumber()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.model.actionplan.summary.computation.
	 * ISummaryComputation#compute(lu.itrust.business.TS.model.actionplan.
	 * ActionPlanMode)
	 */
	@Override
	public void compute(ActionPlanMode mode) {
		if (getActionPlans().isEmpty() || mode != ActionPlanMode.APQ)
			return;

		generateStage(START_P0, true, 0);

		int phase = getActionPlans().get(0).getMeasure().getPhase().getNumber();

		for (ActionPlanEntry actionPlanEntry : getActionPlans()) {
			int measurePhase = actionPlanEntry.getMeasure().getPhase().getNumber();
			if (measurePhase > phase) {
				for (int i = phase; i < measurePhase; i++) {
					generateStage("Phase " + i, false, i);
					resetCurrentData();
				}
				phase = measurePhase;
			}
			nextActionEntry(actionPlanEntry);
		}

		getCurrentValues().conformanceHelper.values().parallelStream().forEach(helper -> helper.conformance = 0);

		generateStage("Phase " + phase, false, phase);

		getSummaryStages().forEach(summary -> {
			summary.getConformances().forEach(conformity -> conformity.setAnalysisStandard(getAnalysisStandards().get(conformity.getAnalysisStandard().getStandard().getLabel())));
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

	private void nextActionEntry(ActionPlanEntry actionPlanEntry) {
		Measure measure = actionPlanEntry.getMeasure();
		SummaryStandardHelper helper = getCurrentValues().conformanceHelper.get(measure.getAnalysisStandard().getStandard().getLabel());
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
		getCurrentValues().internalMaintenance += measure.getInternalMaintenance();
		// in case of a phase calculation multiply external maintenance with
		// phasetime
		getCurrentValues().externalMaintenance += measure.getExternalMaintenance();
		// update recurrent investment
		getCurrentValues().recurrentInvestment += measure.getRecurrentInvestment();
	}

	private void generateStage(String name, boolean isFirst, int number) {
		double phaseTime = 0;
		boolean isFirstValidPhase = false;

		if (number > 0)
			phaseTime = getPhases().stream().filter(phase -> phase.getNumber() == number)
					.map(phase -> Analysis.getYearsDifferenceBetweenTwoDates(phase.getBeginDate(), phase.getEndDate())).findAny().orElse(0d);
		if (isFirst)
			getCurrentValues().implementedCount = 0;
		if (getCurrentValues().previousStage == null)
			getCurrentValues().measureCount = 0;
		else {
			getCurrentValues().measureCount = getCurrentValues().previousStage.getMeasureCount();
			isFirstValidPhase = START_P0.equals(getCurrentValues().previousStage.getStage());
		}

		getCurrentValues().notCompliantMeasure27001Count = 0;
		getCurrentValues().notCompliantMeasure27002Count = 0;

		for (SummaryStandardHelper helper : getCurrentValues().conformanceHelper.values()) {
			helper.conformance = 0;
			int denominator = 0;
			double numerator = 0;
			AnalysisStandard analysisStandard = getAnalysisStandards().get(helper.standard.getStandard().getLabel());
			for (Measure measure : analysisStandard.getMeasures()) {
				double imprate = measure.getImplementationRateValue((ValueFactory) null);
				if (measure.getMeasureDescription().isComputable() && !measure.getStatus().equals(Constant.MEASURE_STATUS_NOT_APPLICABLE)) {
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
						if (imprate < getSoa() && measure instanceof NormalMeasure) {
							if (!isSelected || measure.getPhase().getNumber() > number)
								if (helper.standard.getStandard().is(Constant.STANDARD_27001))
									getCurrentValues().notCompliantMeasure27001Count++;
								else if (helper.standard.getStandard().is(Constant.STANDARD_27002))
									getCurrentValues().notCompliantMeasure27002Count++;
						}
					}

				}
			}

			if (denominator == 0)
				helper.conformance = 0;
			else
				helper.conformance += (numerator / (double) denominator);
		}

		if (isFirstValidPhase) {
			getCurrentValues().internalMaintenance += getPreMaintenance().getInternalMaintenance();
			getCurrentValues().externalMaintenance += getPreMaintenance().getExternalMaintenance();
			getCurrentValues().recurrentInvestment += getPreMaintenance().getRecurrentInvestment();
		}

		MaintenanceRecurrentInvestment maintenanceRecurrentInvestment = getMaintenances().containsKey(number - 1) ? getMaintenances().get(number - 1)
				: new MaintenanceRecurrentInvestment();

		if (getMaintenances().containsKey(number))
			getMaintenances().get(number).update(getCurrentValues().internalMaintenance, getCurrentValues().externalMaintenance, getCurrentValues().recurrentInvestment);
		else
			getMaintenances().put(number,
					new MaintenanceRecurrentInvestment(getCurrentValues().internalMaintenance, getCurrentValues().externalMaintenance, getCurrentValues().recurrentInvestment));

		// ****************************************************************
		// * create summary stage object
		// ****************************************************************
		SummaryStage summaryStage = new SummaryStage();

		// add values to summary stage object
		summaryStage.setStage(name);
		summaryStage.setActionPlanType(getActionPlanType());

		for (String key : getCurrentValues().conformanceHelper.keySet())
			summaryStage.addConformance(getCurrentValues().conformanceHelper.get(key).standard, getCurrentValues().conformanceHelper.get(key).conformance);

		if (getCurrentValues().previousStage != null)
			summaryStage.setMeasureCount(getCurrentValues().implementedCount - getCurrentValues().previousStage.getImplementedMeasuresCount());
		else
			summaryStage.setMeasureCount(getCurrentValues().measureCount);
		summaryStage.setImplementedMeasuresCount(getCurrentValues().implementedCount);
		summaryStage.setCostOfMeasures(getCurrentValues().measureCost);
		summaryStage.setInternalWorkload(getCurrentValues().internalWorkload);
		summaryStage.setExternalWorkload(getCurrentValues().externalWorkload);
		summaryStage.setInvestment(getCurrentValues().investment);
		summaryStage.setNotCompliantMeasure27001Count(getCurrentValues().notCompliantMeasure27001Count);
		summaryStage.setNotCompliantMeasure27002Count(getCurrentValues().notCompliantMeasure27002Count);

		if (isFirstValidPhase) {
			summaryStage.setInternalMaintenance((getPreMaintenance().getInternalMaintenance() + maintenanceRecurrentInvestment.getInternalMaintenance()) * phaseTime);
			summaryStage.setExternalMaintenance((getPreMaintenance().getExternalMaintenance() + maintenanceRecurrentInvestment.getExternalMaintenance()) * phaseTime);
			summaryStage.setRecurrentInvestment((getPreMaintenance().getRecurrentInvestment() + maintenanceRecurrentInvestment.getRecurrentInvestment()) * phaseTime);
		} else {
			summaryStage.setInternalMaintenance(maintenanceRecurrentInvestment.getInternalMaintenance() * phaseTime);
			summaryStage.setExternalMaintenance(maintenanceRecurrentInvestment.getExternalMaintenance() * phaseTime);
			summaryStage.setRecurrentInvestment(maintenanceRecurrentInvestment.getRecurrentInvestment() * phaseTime);
		}

		summaryStage.setRecurrentCost(getCurrentValues().recurrentCost = summaryStage.getInternalMaintenance() * getInternalSetupRate()
				+ summaryStage.getExternalMaintenance() * getExternalSetupRate() + summaryStage.getRecurrentInvestment());

		// update total cost
		summaryStage.setImplementCostOfPhase(getCurrentValues().implementCostOfPhase = (getCurrentValues().internalWorkload * getInternalSetupRate())
				+ (getCurrentValues().externalWorkload * getExternalSetupRate()) + getCurrentValues().investment);

		// in case of a phase calculation multiply external maintenance,
		// internal maintenance with
		// phasetime and with internal and external setup as well as investment
		// with phasetime

		summaryStage.setTotalCostofStage(getCurrentValues().totalCost += (summaryStage.getRecurrentCost() + summaryStage.getImplementCostOfPhase()));

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
	 * @param analysisStandards
	 *            the analysisStandards to set
	 */
	public void setAnalysisStandards(Map<String, AnalysisStandard> analysisStandards) {
		this.analysisStandards = analysisStandards;
	}
}
