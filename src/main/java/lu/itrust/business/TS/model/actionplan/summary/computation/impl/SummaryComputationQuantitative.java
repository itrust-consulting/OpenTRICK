/**
 * 
 */
package lu.itrust.business.TS.model.actionplan.summary.computation.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
public class SummaryComputationQuantitative extends SummaryComputation {

	private ValueFactory factory;

	/**
	 * @param factory
	 * 
	 */
	public SummaryComputationQuantitative(Analysis analysis, ValueFactory factory, List<AnalysisStandard> analysisStandards) {
		setFactory(factory);
		setAnalysis(analysis);
		setPhases(new ArrayList<>());
		setCurrentValues(new SummaryValues(analysisStandards));
		setPreMaintenance(new MaintenanceRecurrentInvestment());
		setInternalSetupRate(analysis.getParameter(Constant.PARAMETER_INTERNAL_SETUP_RATE));
		setExternalSetupRate(analysis.getParameter(Constant.PARAMETER_EXTERNAL_SETUP_RATE));
		setSoa(analysis.getParameter(Constant.SOA_THRESHOLD, 100));
		generatePreMaintenance(analysisStandards);
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
		setActionPlans(getAnalysis().getActionPlan(mode));
		if (getActionPlans().isEmpty())
			return;
		resetClassData();
		setActionPlanType(getActionPlans().get(0).getActionPlanType());
		boolean anticipated = true, byPhase = false;
		int phase = 0;
		// ****************************************************************
		// * generate first stage
		// ****************************************************************
		// add start value of ALE (for first stage (P0))
		getCurrentValues().totalALE = getActionPlans().get(0).getTotalALE() + getActionPlans().get(0).getDeltaALE();
		// generate first stage
		generateStage(START_P0, true, phase);
		// ****************************************************************
		// * check if calculation by phase
		// ****************************************************************
		switch (getActionPlanType().getActionPlanMode()) {
		case APPN:
		case APPO:
		case APPP:
			// set flag
			byPhase = true;
			// retrieve first phase number
			phase = getActionPlans().get(0).getMeasure().getPhase().getNumber();
			break;
		default:
			break;
		}
		// ****************************************************************
		// * parse action plan and calculate summary until last stage
		// ****************************************************************
		// parse action plan
		for (ActionPlanEntry actionPlanEntry : getActionPlans()) {
			// check if calculation by phase -> YES
			if (byPhase) {
				int measurePhase = actionPlanEntry.getMeasure().getPhase().getNumber();
				if (measurePhase > phase) {
					for (int i = phase; i < measurePhase; i++) {
						generateStage("Phase " + i, false, i);
						resetCurrentData();
					}
					phase = measurePhase;
				}
			} else if (anticipated && actionPlanEntry.getROI() < 0) {
				// ****************************************************************
				// * generate stage for anticipated level
				// ****************************************************************
				generateStage("Anticipated", false, phase);
				// deactivate flag
				anticipated = false;
			}
			// ****************************************************************
			// * calculate values for next run
			// ****************************************************************
			nextActionEntry(actionPlanEntry);
		}
		// ****************************************************************
		// * calculate last phase
		// ****************************************************************
		// reinitialise variables
		getCurrentValues().conformanceHelper.values().parallelStream().forEach(helper -> helper.conformance = 0);
		// check if by phase -> YES
		if (byPhase) {
			// ****************************************************************
			// * generate stage for phase
			// ****************************************************************
			generateStage("Phase " + phase, false, phase);
		} else {
			// ****************************************************************
			// * generate stage for all measures
			// ****************************************************************
			generateStage("All Measures", false, phase);
		}
		
		getAnalysis().addSummaryEntries(getSummaryStages());
	}

	/**
	 * @return the factory
	 */
	public ValueFactory getFactory() {
		return factory;
	}

	/**
	 * @param factory
	 *            the factory to set
	 */
	public void setFactory(ValueFactory factory) {
		this.factory = factory;
	}

	private void generatePreMaintenance(List<AnalysisStandard> analysisStandards) {
		analysisStandards.stream().flatMap(standard -> standard.getMeasures().stream()).forEach(measure -> {
			if (!measure.getStatus().equals(Constant.MEASURE_STATUS_NOT_APPLICABLE)) {
				if (measure.getImplementationRateValue(getFactory()) >= 100)
					getPreMaintenance().add(measure.getInternalMaintenance(), measure.getExternalMaintenance(), measure.getRecurrentInvestment());
				if (!this.getPhases().contains(measure.getPhase()))
					this.getPhases().add(measure.getPhase());
			}
		});
		getPhases().sort((o1, o2) -> Integer.compare(o1.getNumber(), o2.getNumber()));
	}

	private void generateStage(String name, boolean isFirst, int number) {
		double phaseTime = 0;
		boolean isFirstValidPhase = false;

		if (number > 0)
			phaseTime = getPhases().stream().filter(phase -> phase.getNumber() == number)
					.map(phase -> phase.getTime()).findAny().orElse(0d);
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
			for (Measure measure : helper.standard.getMeasures()) {
				double imprate = measure.getImplementationRateValue(getFactory());
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
		summaryStage.setTotalALE(getCurrentValues().totalALE);
		summaryStage.setDeltaALE(getCurrentValues().deltaALE);
		summaryStage.setCostOfMeasures(getCurrentValues().measureCost);
		summaryStage.setROSI(getCurrentValues().ROSI);
		summaryStage.setRelativeROSI(getCurrentValues().relativeROSI);
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

	private void nextActionEntry(ActionPlanEntry actionPlanEntry) {
		Measure measure = actionPlanEntry.getMeasure();
		SummaryStandardHelper helper = getCurrentValues().conformanceHelper.get(measure.getAnalysisStandard().getStandard().getLabel());
		helper.measures.add(measure);
		getCurrentValues().measureCount++;
		getCurrentValues().implementedCount++;
		getCurrentValues().measureCost += measure.getCost();
		// set total ALE value
		getCurrentValues().totalALE = actionPlanEntry.getTotalALE();
		// update delta ALE value
		getCurrentValues().deltaALE += actionPlanEntry.getDeltaALE();
		// update ROSI
		getCurrentValues().ROSI += actionPlanEntry.getROI();
		// calculate relative ROSI
		if (getCurrentValues().measureCost == 0) {
			getCurrentValues().relativeROSI = 0;
		} else {
			getCurrentValues().relativeROSI = getCurrentValues().ROSI / getCurrentValues().measureCost;
		}

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

	private void resetClassData() {
		if (getSummaryStages() != null)
			setCurrentValues(new SummaryValues(getCurrentValues().conformanceHelper.values().stream().map(value -> value.standard).collect(Collectors.toList())));
		setSummaryStages(new ArrayList<>());
		setMaintenances(new HashMap<>());
	}

	private void resetCurrentData() {
		getCurrentValues().conformanceHelper.values().parallelStream().forEach(helper -> helper.conformance = 0);
		getCurrentValues().ROSI = 0;
		getCurrentValues().deltaALE = 0;
		getCurrentValues().totalCost = 0;
		getCurrentValues().investment = 0;
		getCurrentValues().measureCost = 0;
		getCurrentValues().measureCount = 0;
		getCurrentValues().relativeROSI = 0;
		getCurrentValues().externalWorkload = 0;
		getCurrentValues().internalWorkload = 0;
		getCurrentValues().implementCostOfPhase = 0;
	}

}
