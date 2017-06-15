/**
 * 
 */
package lu.itrust.business.TS.model.actionplan.summary.computation;

import java.util.List;
import java.util.Map;

import lu.itrust.business.TS.model.actionplan.ActionPlanEntry;
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
public abstract class SummaryComputation implements ISummaryComputation {

	private double soa;
	
	private double internalSetupRate;
	
	private double externalSetupRate;
	
	private Analysis analysis;
	
	private ActionPlanType actionPlanType;

	private List<Phase> phases;

	private SummaryValues currentValues;
	
	private List<ActionPlanEntry> actionPlans;
	
	private List<SummaryStage> summaryStages;
	
	private MaintenanceRecurrentInvestment preMaintenance;
	
	private Map<Integer, MaintenanceRecurrentInvestment> maintenances;


	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.model.actionplan.summary.computation.
	 * ISummaryComputation#getAnalysis()
	 */
	@Override
	public Analysis getAnalysis() {
		return this.analysis;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.model.actionplan.summary.computation.
	 * ISummaryComputation#getCurrentValues()
	 */
	@Override
	public SummaryValues getCurrentValues() {
		return this.currentValues;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.model.actionplan.summary.computation.
	 * ISummaryComputation#getMaintenances()
	 */
	@Override
	public Map<Integer, MaintenanceRecurrentInvestment> getMaintenances() {
		return this.maintenances;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.model.actionplan.summary.computation.
	 * ISummaryComputation#getPhases()
	 */
	@Override
	public List<Phase> getPhases() {
		return this.phases;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.model.actionplan.summary.computation.
	 * ISummaryComputation#getSummaryStages()
	 */
	@Override
	public List<SummaryStage> getSummaryStages() {
		return this.summaryStages;
	}

	/**
	 * @param analysis
	 *            the analysis to set
	 */
	public void setAnalysis(Analysis analysis) {
		this.analysis = analysis;
	}

	/**
	 * @param currentValues
	 *            the currentValues to set
	 */
	public void setCurrentValues(SummaryValues currentValues) {
		this.currentValues = currentValues;
	}

	/**
	 * @param summaryStages the summaryStages to set
	 */
	public void setSummaryStages(List<SummaryStage> summaryStages) {
		this.summaryStages = summaryStages;
	}

	/**
	 * @param maintenances the maintenances to set
	 */
	public void setMaintenances(Map<Integer, MaintenanceRecurrentInvestment> maintenances) {
		this.maintenances = maintenances;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.model.actionplan.summary.computation.
	 * ISummaryComputation#getPreMaintenance()
	 */
	@Override
	public MaintenanceRecurrentInvestment getPreMaintenance() {
		return preMaintenance;
	}

	/**
	 * @param preMaintenance the preMaintenance to set
	 */
	public void setPreMaintenance(MaintenanceRecurrentInvestment preMaintenance) {
		this.preMaintenance = preMaintenance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.model.actionplan.summary.computation.
	 * ISummaryComputation#getActionPlans()
	 */
	@Override
	public List<ActionPlanEntry> getActionPlans() {
		return actionPlans;
	}

	/**
	 * @param actionPlans the actionPlans to set
	 */
	public void setActionPlans(List<ActionPlanEntry> actionPlans) {
		this.actionPlans = actionPlans;
	}
	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.model.actionplan.summary.computation.ISummaryComputation#getActionPlanType()
	 */
	@Override
	public ActionPlanType getActionPlanType() {
		return actionPlanType;
	}

	/**
	 * @param actionPlanType the actionPlanType to set
	 */
	public void setActionPlanType(ActionPlanType actionPlanType) {
		this.actionPlanType = actionPlanType;
	}

	/**
	 * @param phases the phases to set
	 */
	public void setPhases(List<Phase> phases) {
		this.phases = phases;
	}

	/**
	 * @return the soa
	 */
	@Override
	public double getSoa() {
		return soa;
	}

	/**
	 * @param soa the soa to set
	 */
	public void setSoa(double soa) {
		this.soa = soa;
	}

	/**
	 * @return the internalSetupRate
	 */
	@Override
	public double getInternalSetupRate() {
		return internalSetupRate;
	}

	/**
	 * @param internalSetupRate the internalSetupRate to set
	 */
	public void setInternalSetupRate(double internalSetupRate) {
		this.internalSetupRate = internalSetupRate;
	}

	/**
	 * @return the externalSetupRate
	 */
	@Override
	public double getExternalSetupRate() {
		return externalSetupRate;
	}

	/**
	 * @param externalSetupRate the externalSetupRate to set
	 */
	public void setExternalSetupRate(double externalSetupRate) {
		this.externalSetupRate = externalSetupRate;
	}
	
	

}
