package lu.itrust.business.TS.actionplan;

import lu.itrust.business.TS.Analysis;

/**
 * SummaryStage: <br>
 * Has all data for a single stage (phase of summary)
 * 
 * @author itrust consulting s.� r.l. - SME,BJA
 * @version 0.1
 * @since 2012-10-09
 */
public class SummaryStage {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** ID of Stage */
	private int id = -1;

	/** id unsaved value = null */
	private Analysis analysis = null;

	private ActionPlanType actionPlanType = null;

	/** Name or Identifier of the Stage */
	private String stage = "";

	/** Percentage of AnalysisNorm 27001 Conformance for this Stage */
	private double conformance27001 = 0;

	/** Percentage of AnalysisNorm 27002 Conformance for this Stage */
	private double conformance27002 = 0;

	/** Number of Measures in this Stage */
	private int measureCount;

	/** Number of Implemented Measures in this Stage */
	private int implementedMeasuresCount;

	/** Total ALE for this Stage */
	private double totalALE;

	/** Delta ALE for this Stage (calculate sum of deltaALE from actionplan entries) */
	private double deltaALE;

	/** Cost of Measures for this Stage (calculate sum of cost from measures in actionplan) */
	private double costOfMeasures;

	/** ROSI for this Stage (take last actionplan entry's ROSI value) */
	private double ROSI;

	/** ROSI / Cost Of Measures */
	private double relativeROSI;

	/** Sum of Internal Workloads taken from Action Plan Entries */
	private double internalWorkload;

	/** Sum of External Workloads taken from Action Plan Entries */
	private double externalWorkload;

	/** Sum of Investments taken from Action Plan Entries */
	private double investment;

	/** Sum of ((InternalWorkload * Maintenance) / 100) taken from Action Plan Entries */
	private double internalMaintenance;

	/** Sum of ((ExternalWorkload * Maintenance) / 100) taken from Action Plan Entries */
	private double externalMaintenance;

	/** Sum of ((Investments * Maintenance) / 100) taken from Action Plan Entries */
	private double recurrentCost;

	/**
	 * Sum of (InternalWorkload * InternalSetupRate) + (InternalWorkload * InternalSetupRate) +
	 * (Investment) + (((InternalWorkload * Maintenance) / 100) * InternalSetupRate ) +
	 * (((ExternalWorkload * Maintenance) / 100) * ExternalSetupRate) + ((Investments * Maintenance)
	 * / 100)
	 **/
	private double totalCostofStage;

	/** Regular exptression of Phase Names */
	public static final String STAGE_REGEX =
		"Start\\(P0\\)|Phase [1-9]\\d*|Anticipated|All Measures";

	/***********************************************************************************************
	 * Constructors
	 **********************************************************************************************/

	/**
	 * Constructor:<br>
	 */
	public SummaryStage() {
	}

	/***********************************************************************************************
	 * Getters and Setters
	 **********************************************************************************************/

	/**
	 * getId: <br>
	 * Returns the "id" field value
	 * 
	 * @return The Summary Stage ID
	 */
	public int getId() {
		return id;
	}

	/**
	 * setId: <br>
	 * Sets the "id" field with a value
	 * 
	 * @param id
	 *            The value to set the Summary Stage ID
	 */
	public void setId(int id) {
		if (id < 1)
			throw new IllegalArgumentException("Id should be 1 or greater");
		this.id = id;
	}

	/**
	 * getStage: <br>
	 * Returns the "stage" field value
	 * 
	 * @return The Stage Title
	 */
	public String getStage() {
		return stage;
	}

	/**
	 * setStage: <br>
	 * Sets the "stage" field with a value
	 * 
	 * @param stage
	 *            The value to set the Stage Title
	 */
	public void setStage(String stage) {
		if (stage == null || !stage.matches(STAGE_REGEX))
			throw new IllegalArgumentException("Stage should meet this regular expression "
				+ STAGE_REGEX);
		this.stage = stage;
	}

	/**
	 * getConformance27001: <br>
	 * Returns the "conformance27001" field value
	 * 
	 * @return The Percentage of AnalysisNorm 27001 Conformance
	 */
	public double getConformance27001() {
		return conformance27001;
	}

	/**
	 * setConformance27001: <br>
	 * Sets the "conformance27001" field with a value
	 * 
	 * @param conformance27001
	 *            The value to set the Percentage of AnalysisNorm 27001 Conformance
	 */
	public void setConformance27001(double conformance27001) {
		if (conformance27001 < 0 || conformance27001 > 1)
			throw new IllegalArgumentException("Conformance27001 should be between 0 and 1 :"+conformance27001);
		this.conformance27001 = conformance27001;
	}

	/**
	 * getConformance27002: <br>
	 * Returns the "conformance27002" field value
	 * 
	 * @return The Percentage of AnalysisNorm 27002 Conformance
	 */
	public double getConformance27002() {
		return conformance27002;
	}

	/**
	 * setConformance27002: <br>
	 * Sets the "conformance27002" field with a value
	 * 
	 * @param conformance27002
	 *            The value to set the Percentage of AnalysisNorm 27002 Conformance
	 */
	public void setConformance27002(double conformance27002) {
		if (conformance27002 < 0 || conformance27002 > 1)
			throw new IllegalArgumentException("Conformance27002 should be between 0 and 1 :"+conformance27002);
		this.conformance27002 = conformance27002;
	}

	/**
	 * getMeasureCount: <br>
	 * Returns the "measureCount" field value
	 * 
	 * @return The Number of Measures of this Stage
	 */
	public int getMeasureCount() {
		return measureCount;
	}

	/**
	 * setMeasureCount: <br>
	 * Sets the "measureCount" field with a value
	 * 
	 * @param measureCount
	 *            The value to set the Number of Measures of this Stage
	 */
	public void setMeasureCount(int measureCount) {
		if (measureCount < 0) {
			throw new IllegalArgumentException("MeasureCount should be 0 or greater");
		}
		this.measureCount = measureCount;
	}

	/**
	 * getImplementedMeasuresCount: <br>
	 * Returns the "implementedMeasuresCount" field value
	 * 
	 * @return The Number of Implemented Measures in this Stage
	 */
	public int getImplementedMeasuresCount() {
		return implementedMeasuresCount;
	}

	/**
	 * setImplementedMeasuresCount: <br>
	 * Sets the "implementedMeasuresCount" field with a value
	 * 
	 * @param implementedMeasuresCount
	 *            The value to set the Number of Implemented Measures in this Stage
	 */
	public void setImplementedMeasuresCount(int implementedMeasuresCount) {
		if (implementedMeasuresCount < 0)
			throw new IllegalArgumentException("ImplementedMeasuresCount should be 0 or greater");
		this.implementedMeasuresCount = implementedMeasuresCount;
	}

	/**
	 * getTotalALE: <br>
	 * Returns the "totalALE" field value
	 * 
	 * @return The Total ALE at the End of the Stage
	 */
	public double getTotalALE() {
		return totalALE;
	}

	/**
	 * setTotalALE: <br>
	 * Sets the "totalALE" field with a value
	 * 
	 * @param totalALE
	 *            The value to set the Total ALE at the End of the Stage
	 */
	public void setTotalALE(double totalALE) {
		if (totalALE < 0)
			throw new IllegalArgumentException("TotalALE should be 0 or greater");
		this.totalALE = totalALE;
	}

	/**
	 * getDeltaALE: <br>
	 * Returns the "deltaALE" field value
	 * 
	 * @return The Risk Reduction ALE (Delta ALE)
	 */
	public double getDeltaALE() {
		return deltaALE;
	}

	/**
	 * setDeltaALE: <br>
	 * Sets the "deltaALE" field with a value
	 * 
	 * @param deltaALE
	 *            The value to set the Delta ALE
	 */
	public void setDeltaALE(double deltaALE) {
		if (deltaALE < 0)
			throw new IllegalArgumentException("DeltaALE should be 0 or greater");
		this.deltaALE = deltaALE;
	}

	/**
	 * getCostOfMeasures: <br>
	 * Returns the "costOfMeasures" field value
	 * 
	 * @return The Cost of all Measures in this Stage
	 */
	public double getCostOfMeasures() {
		return costOfMeasures;
	}

	/**
	 * setCostOfMeasures: <br>
	 * Sets the "costOfMeasures" field with a value
	 * 
	 * @param costOfMeasures
	 *            The value to set the Cost of all Measures in this Stage
	 */
	public void setCostOfMeasures(double costOfMeasures) {
		if (costOfMeasures < 0) {
			throw new IllegalArgumentException("CostOfMeasures should be 1 or greater");
		}
		this.costOfMeasures = costOfMeasures;
	}

	/**
	 * getROSI: <br>
	 * Returns the "ROSI" field value
	 * 
	 * @return The ROSI or ROSMI of the last Entry of the Stage
	 */
	public double getROSI() {
		return ROSI;
	}

	/**
	 * setROSI: <br>
	 * Sets the "ROSI" field with a value
	 * 
	 * @param ROSI
	 *            The value to set the ROSI or ROSMI of the last Entry of the Stage
	 */
	public void setROSI(double ROSI) {
		this.ROSI = ROSI;
	}

	/**
	 * getRelativeROSI: <br>
	 * Returns the "relativeROSI" field value
	 * 
	 * @return The Relative ROSI
	 */
	public double getRelativeROSI() {
		return relativeROSI;
	}

	/**
	 * setRelativeROSI: <br>
	 * Sets the "relativeROSI" field with a value
	 * 
	 * @param relativeROSI
	 *            The value to set the Relative ROSI
	 */
	public void setRelativeROSI(double relativeROSI) {
		this.relativeROSI = relativeROSI;
	}

	/**
	 * getInternalWorkload: <br>
	 * Returns the "internalWorkload" field value
	 * 
	 * @return The Internal Workload
	 */
	public double getInternalWorkload() {
		return internalWorkload;
	}

	/**
	 * setInternalWorkload: <br>
	 * Sets the "internalWorkload" field with a value
	 * 
	 * @param internalWorkload
	 *            The value to set the Internal Workload
	 */
	public void setInternalWorkload(double internalWorkload) {
		if (internalWorkload < 0)
			throw new IllegalArgumentException("InternalWorkload should be 0 or greater");
		this.internalWorkload = internalWorkload;
	}

	/**
	 * getExternalWorkload: <br>
	 * Returns the "externalWorkload" field value
	 * 
	 * @return The External Workload
	 */
	public double getExternalWorkload() {
		return externalWorkload;
	}

	/**
	 * setExternalWorkload: <br>
	 * Sets the "externalWorkload" field with a value
	 * 
	 * @param externalWorkload
	 *            The value to set the External Workload
	 */
	public void setExternalWorkload(double externalWorkload) {
		if (externalWorkload < 0)
			throw new IllegalArgumentException("ExternalWorkload should be 0 or greater");
		this.externalWorkload = externalWorkload;
	}

	/**
	 * getInvestment: <br>
	 * Returns the "investment" field value
	 * 
	 * @return The Investment
	 */
	public double getInvestment() {
		return investment;
	}

	/**
	 * setInvestment: <br>
	 * Sets the "investment" field with a value
	 * 
	 * @param investment
	 *            The value to set the Investment
	 */
	public void setInvestment(double investment) {
		if (investment < 0)
			throw new IllegalArgumentException("Investment should be 0 or greater");
		this.investment = investment;
	}

	/**
	 * getInternalMaintenance: <br>
	 * Returns the "internalMaintenance" field value
	 * 
	 * @return The Internal Maintenance
	 */
	public double getInternalMaintenance() {
		return internalMaintenance;
	}

	/**
	 * setInternalMaintenance: <br>
	 * Sets the "internalMaintenance" field with a value
	 * 
	 * @param internalMaintenance
	 *            The value to set the Internal Maintenance
	 */
	public void setInternalMaintenance(double internalMaintenance) {
		if (internalMaintenance < 0)
			throw new IllegalArgumentException("InternalMaintenance should be 0 or greater");
		this.internalMaintenance = internalMaintenance;
	}

	/**
	 * getExternalMaintenance: <br>
	 * Returns the "externalMaintenance" field value
	 * 
	 * @return The External Maintenance
	 */
	public double getExternalMaintenance() {
		return externalMaintenance;
	}

	/**
	 * setExternalMaintenance: <br>
	 * Sets the "externalMaintenance" field with a value
	 * 
	 * @param externalMaintenance
	 *            The value to set the External Maintenance
	 */
	public void setExternalMaintenance(double externalMaintenance) {
		if (externalMaintenance < 0)
			throw new IllegalArgumentException("ExternalMaintenance should be 0 or greater");
		this.externalMaintenance = externalMaintenance;
	}

	/**
	 * getRecurrentCost: <br>
	 * Returns the "recurrentCost" field value
	 * 
	 * @return The Recurrent Cost
	 */
	public double getRecurrentCost() {
		return recurrentCost;
	}

	/**
	 * setRecurrentCost: <br>
	 * Sets the "recurrentCost" field with a value
	 * 
	 * @param recurrentCost
	 *            The value to set the Recurrent Cost
	 */
	public void setRecurrentCost(double recurrentCost) {
		if (recurrentCost < 0)
			throw new IllegalArgumentException("RecurrentCost should be 0 or greater");
		this.recurrentCost = recurrentCost;
	}

	/**
	 * getTotalCostofStage: <br>
	 * Returns the "totalCostofStage" field value
	 * 
	 * @return The Total Cost of this Stage
	 */
	public double getTotalCostofStage() {
		return totalCostofStage;
	}

	/**
	 * setTotalCostofStage: <br>
	 * Sets the "totalCostofStage" field with a value
	 * 
	 * @param totalCostofStage
	 *            The value to set the Total Cost of this Stage
	 */
	public void setTotalCostofStage(double totalCostofStage) {
		if (totalCostofStage < 0)
			throw new IllegalArgumentException("TotalCostofStage should be 0 or greater");
		this.totalCostofStage = totalCostofStage;
	}

	/**
	 * getAnalysis: <br>
	 * Returns the analysis field value.
	 * 
	 * @return The value of the analysis field
	 */
	public Analysis getAnalysis() {
		return analysis;
	}

	/**
	 * setAnalysis: <br>
	 * Sets the Field "analysis" with a value.
	 * 
	 * @param analysis
	 *            The Value to set the analysis field
	 */
	public void setAnalysis(Analysis analysis) {
		this.analysis = analysis;
	}

	/**
	 * getActionPlanType: <br>
	 * Returns the actionPlanType field value.
	 * 
	 * @return The value of the actionPlanType field
	 */
	public ActionPlanType getActionPlanType() {
		return actionPlanType;
	}

	/**
	 * setActionPlanType: <br>
	 * Sets the Field "actionPlanType" with a value.
	 * 
	 * @param actionPlanType
	 *            The Value to set the actionPlanType field
	 */
	public void setActionPlanType(ActionPlanType actionPlanType) {
		this.actionPlanType = actionPlanType;
	}
}