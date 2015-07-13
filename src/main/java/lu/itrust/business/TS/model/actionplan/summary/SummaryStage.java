package lu.itrust.business.TS.model.actionplan.summary;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.actionplan.ActionPlanType;
import lu.itrust.business.TS.model.standard.AnalysisStandard;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

/**
 * SummaryStage: <br>
 * Has all data for a single stage (phase of summary)
 * 
 * @author itrust consulting s.a r.l. - SME,BJA
 * @version 0.1
 * @since 2012-10-09
 */
@Entity
@Table(name = "ActionPlanSummary")
public class SummaryStage {

	/** Regular exptression of Phase Names */
	public static final String STAGE_REGEX = "Start\\(P0\\)|Phase [1-9]\\d*|Anticipated|All Measures";

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** ID of Stage */
	@Id
	@GeneratedValue
	@Column(name = "idActionPlanSummary")
	private int id = -1;

	@ManyToOne
	@JoinColumn(name = "fiActionPlanType", nullable = false)
	@Access(AccessType.FIELD)
	@Cascade(CascadeType.SAVE_UPDATE)
	private ActionPlanType actionPlanType = null;

	/** Name or Identifier of the Stage */
	@Column(name = "dtName", nullable = false)
	private String stage = "";

	@OneToMany
	@JoinColumn(name = "fiActionPlanSummary", nullable = false)
	@Cascade({ CascadeType.SAVE_UPDATE, CascadeType.DELETE })
	@Access(AccessType.FIELD)
	private List<SummaryStandardConformance> conformances = new ArrayList<SummaryStandardConformance>();

	/** Number of Measures in this Stage */
	@Column(name = "dtMeasureCount", nullable = false)
	private int measureCount = 0;

	/** Number of Implemented Measures in this Stage */
	@Column(name = "dtImplementedMeasureCount", nullable = false)
	private int implementedMeasuresCount = 0;

	/** Total ALE for this Stage */
	@Column(name = "dtCurrentTotalALE", nullable = false)
	private double totalALE = 0;

	/** Delta ALE for this Stage (calculate sum of deltaALE from actionplan entries) */
	@Column(name = "dtCurrentDeltaALE", nullable = false)
	private double deltaALE = 0;

	/** Cost of Measures for this Stage (calculate sum of cost from measures in actionplan) */
	@Column(name = "dtCurrentCostMeasures", nullable = false)
	private double costOfMeasures = 0;

	/** ROSI for this Stage (take last actionplan entry's ROSI value) */
	@Column(name = "dtROSI", nullable = false)
	private double ROSI = 0;

	/** ROSI / Cost Of Measures */
	@Column(name = "dtRelativeROSI", nullable = false)
	private double relativeROSI = 0;

	/** Sum of Internal Workloads taken from Action Plan Entries */
	@Column(name = "dtTotalInternalWorkLoad", nullable = false)
	private double internalWorkload = 0;

	/** Sum of External Workloads taken from Action Plan Entries */
	@Column(name = "dtTotalExternalWorkLoad", nullable = false)
	private double externalWorkload = 0;

	/** Sum of Investments taken from Action Plan Entries */
	@Column(name = "dtInvestment", nullable = false)
	private double investment = 0;

	/** Sum of ((InternalWorkload * MaintenanceRecurrentInvestment) / 100) taken from Action Plan Entries */
	@Column(name = "dtTotalInternalMaintenance", nullable = false)
	private double internalMaintenance = 0;

	/** Sum of ((ExternalWorkload * MaintenanceRecurrentInvestment) / 100) taken from Action Plan Entries */
	@Column(name = "dtTotalExternalMaintenance", nullable = false)
	private double externalMaintenance = 0;

	/** Sum of recurrent Investment */
	@Column(name = "dtRecurrentInvestment", nullable = false)
	private double recurrentInvestment = 0;

	/** Sum of ((Investments * MaintenanceRecurrentInvestment) / 100) taken from Action Plan Entries */
	@Column(name = "dtRecurrentCost", nullable = false)
	private double recurrentCost = 0;

	/**
	 * Sum of (InternalWorkload * InternalSetupRate) + (InternalWorkload * InternalSetupRate) +
	 * (Investment) + (((InternalWorkload * MaintenanceRecurrentInvestment) / 100) * InternalSetupRate ) +
	 * (((ExternalWorkload * MaintenanceRecurrentInvestment) / 100) * ExternalSetupRate) + ((Investments * MaintenanceRecurrentInvestment)
	 * / 100)
	 **/
	@Column(name = "dtTotalCost", nullable = false)
	private double totalCostofStage;

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
	 * @throws TrickException
	 */
	public void setId(int id) throws TrickException {
		if (id < 1)
			throw new TrickException("error.summary_stage.id", "Id should be greater than 0!");
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
	 * @throws TrickException
	 */
	public void setStage(String stage) throws TrickException {
		if (stage == null || !stage.matches(STAGE_REGEX))
			throw new TrickException("error.summary_stage.stage", "Stage is not valid");
		this.stage = stage;
	}

	public Double getSingleConformance(String label) {
		for (SummaryStandardConformance conformance : this.conformances)
			if (conformance.getAnalysisStandard().getStandard().getLabel().equals(label))
				return conformance.getConformance();
		return 0.0;
	}

	/**
	 * getConformances: <br>
	 * Description
	 * 
	 * @return
	 */
	public List<SummaryStandardConformance> getConformances() {
		return this.conformances;
	}

	/**
	 * setConformances: <br>
	 * Description
	 * 
	 * @param conformances
	 */
	public void setConformances(List<SummaryStandardConformance> conformances) {
		this.conformances = conformances;
	}

	/**
	 * addConformance: <br>
	 * Description
	 * 
	 * @param analysisStandard
	 * @param conformance
	 */
	public void addConformance(AnalysisStandard analysisStandard, double conformance) {
		this.conformances.add(new SummaryStandardConformance(analysisStandard, conformance));
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
	 * @throws TrickException
	 */
	public void setMeasureCount(int measureCount) throws TrickException {
		if (measureCount < 0)
			throw new TrickException("error.summary_stage.measure_count", "Measure count should be 0 or greater");
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
	 * @throws TrickException
	 */
	public void setImplementedMeasuresCount(int implementedMeasuresCount) throws TrickException {
		if (implementedMeasuresCount < 0)
			throw new TrickException("error.summary_stage.implemented_measures_count", "Implemented measures count should be 0 or greater");
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
	 * @throws TrickException
	 */
	public void setTotalALE(double totalALE) throws TrickException {
		if (totalALE < 0)
			throw new TrickException("error.summary_stage.total_ale", "Total ALE should be 0 or greater");
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
	 * @throws TrickException
	 */
	public void setDeltaALE(double deltaALE) throws TrickException {
		if (deltaALE < 0)
			throw new TrickException("error.summary_stage.delta_ale", "Delta ALE should be 0 or greater");
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
	 * @throws TrickException
	 */
	public void setCostOfMeasures(double costOfMeasures) throws TrickException {
		if (Double.isNaN(costOfMeasures))
			throw new TrickException("error.summary.cost_of_measure.nan", "Please ckeck your data: Cost of measure is not a number");
		if (costOfMeasures < 0)
			throw new TrickException("error.summary.cost_of_measure", "Measures cost should be 1 or greater");
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
	 * @throws TrickException
	 */
	public void setROSI(double ROSI) throws TrickException {
		if (Double.isNaN(ROSI))
			throw new TrickException("error.summary.rosi.nan", "Please ckeck your data: rosi is not a number");
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
	 * @throws TrickException
	 */
	public void setRelativeROSI(double relativeROSI) throws TrickException {
		if (Double.isNaN(relativeROSI))
			throw new TrickException("error.summary.relative_rosi.nan", "Please ckeck your data: relative rosi is not a number");
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
	 * @throws TrickException
	 */
	public void setInternalWorkload(double internalWorkload) throws TrickException {
		if (Double.isNaN(internalWorkload))
			throw new TrickException("error.summary.internal_workload.nan", "Please ckeck your data: Internal workload is not a number");
		if (internalWorkload < 0)
			throw new TrickException("error.summary.internal_workload", "Internal workload should be 0 or greater");
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
	 * @throws TrickException
	 */
	public void setExternalWorkload(double externalWorkload) throws TrickException {
		if (Double.isNaN(externalWorkload))
			throw new TrickException("error.summary.external_workload.nan", "Please ckeck your data: External workload is not a number");
		if (externalWorkload < 0)
			throw new TrickException("error.summary.external_workload.nan", "External workload should be 0 or greater");
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
	 * @throws TrickException
	 */
	public void setInvestment(double investment) throws TrickException {
		if (Double.isNaN(investment))
			throw new TrickException("error.summary.investment.nan", "Please ckeck your data: Investment is not a number");
		if (investment < 0)
			throw new TrickException("error.summary.investment", "Investment should be 0 or greater");
		this.investment = investment;
	}

	/**
	 * getInternalMaintenance: <br>
	 * Returns the "internalMaintenance" field value
	 * 
	 * @return The Internal MaintenanceRecurrentInvestment
	 */
	public double getInternalMaintenance() {
		return internalMaintenance;
	}

	/**
	 * setInternalMaintenance: <br>
	 * Sets the "internalMaintenance" field with a value
	 * 
	 * @param internalMaintenance
	 *            The value to set the Internal MaintenanceRecurrentInvestment
	 * @throws TrickException
	 */
	public void setInternalMaintenance(double internalMaintenance) throws TrickException {
		if (Double.isNaN(internalMaintenance))
			throw new TrickException("error.summary.internal_maintenance.nan", "Please ckeck your data: Internal maintenance is not a number");
		if (internalMaintenance < 0)
			throw new TrickException("error.summary.internal_maintenance", "Internal maintenance should be 0 or greater");
		this.internalMaintenance = internalMaintenance;
	}

	/**
	 * getExternalMaintenance: <br>
	 * Returns the "externalMaintenance" field value
	 * 
	 * @return The External MaintenanceRecurrentInvestment
	 */
	public double getExternalMaintenance() {
		return externalMaintenance;
	}

	/**
	 * setExternalMaintenance: <br>
	 * Sets the "externalMaintenance" field with a value
	 * 
	 * @param externalMaintenance
	 *            The value to set the External MaintenanceRecurrentInvestment
	 * @throws TrickException
	 */
	public void setExternalMaintenance(double externalMaintenance) throws TrickException {
		if (Double.isNaN(externalMaintenance))
			throw new TrickException("error.summary.external_maintenance.nan", "Please ckeck your data: External maintenance is not a number");
		if (externalMaintenance < 0)
			throw new TrickException("error.summary.external_maintenance", "External maintenance should be 0 or greater");
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
	 * @throws TrickException
	 */
	public void setRecurrentCost(double recurrentCost) throws TrickException {
		if (Double.isNaN(recurrentCost))
			throw new TrickException("error.summary.recurrent_cost.nan", "Please ckeck your data: Recurrent cost is not a number");
		if (recurrentCost < 0)
			throw new TrickException("error.summary.recurrent_cost", "Recurrent cost should be 0 or greater");
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
	 * @throws TrickException
	 */
	public void setTotalCostofStage(double totalCostofStage) throws TrickException {
		if (Double.isNaN(recurrentCost))
			throw new TrickException("error.summary.total_cost_of_stage.nan", "Please ckeck your data: Total cost of stage is not a number");
		if (totalCostofStage < 0)
			throw new TrickException("error.summary.total_cost_of_stage", "Stage total cost should be 0 or greater");
		this.totalCostofStage = totalCostofStage;
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

	/**
	 * getRecurrentInvestment: <br>
	 * Returns the recurrentInvestment field value.
	 * 
	 * @return The value of the recurrentInvestment field
	 */
	public double getRecurrentInvestment() {
		return recurrentInvestment;
	}

	/**
	 * setRecurrentInvestment: <br>
	 * Sets the Field "recurrentInvestment" with a value.
	 * 
	 * @param recurrentInvestment
	 *            The Value to set the recurrentInvestment field
	 * @throws TrickException
	 */
	public void setRecurrentInvestment(double recurrentInvestment) throws TrickException {
		if (Double.isNaN(recurrentInvestment))
			throw new TrickException("error.summary.recurrent_investment.nan", "Please ckeck your data: Recurrent investment is not a number");
		this.recurrentInvestment = recurrentInvestment;
	}
}