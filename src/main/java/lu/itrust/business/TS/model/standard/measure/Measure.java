package lu.itrust.business.TS.model.standard.measure;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.AnalysisSetting;
import lu.itrust.business.TS.model.general.Phase;
import lu.itrust.business.TS.model.parameter.IAcronymParameter;
import lu.itrust.business.TS.model.parameter.helper.ValueFactory;
import lu.itrust.business.TS.model.standard.AnalysisStandard;
import lu.itrust.business.TS.model.standard.Standard;
import lu.itrust.business.TS.model.standard.measuredescription.MeasureDescription;

/**
 * Measure: <br>
 * This class represents a Measure and its data. This class has fields that are
 * used in Maturity and AnalysisStandard Meaure classes. (Both are extended by
 * this class)
 * 
 * @author itrust consulting s.à r.l. - BJA,SME
 * @version 0.1
 * @since 2012-08-21
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Measure implements Cloneable {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** The Measure Identifier */
	private int id = -1;

	/** Analysis standard Object */
	private AnalysisStandard analysisStandard = null;

	/** The Measure Domain */
	private MeasureDescription measureDescription = null;

	/** The Measure Status (AP, NA, M) */
	private String status = "NA";

	/** The Implementation Rate */
	private Object implementationRate = null;

	/** The Internal WorkLoad (in Man Days) */
	private double internalWL = 0;

	/** The External WorkLoad (in Man Days) */
	private double externalWL = 0;

	/** The Investment of the Measure (Currency) */
	private double investment = 0;

	/** The LifeTime of the Measure (in Years) */
	private double lifetime = 0;

	/**
	 * The internal MaintenanceRecurrentInvestment of the Measure (in Man Days)
	 */
	private double internalMaintenance = 0;

	/**
	 * The external MaintenanceRecurrentInvestment of the Measure (in Man Days)
	 */
	private double externalMaintenance = 0;

	/** The recurrent investment of maintenance of the Measure (Currency) */
	private double recurrentInvestment = 0;

	/** The measure importance */
	private int importance = 2;

	/** The Cost of the Measure (Currency) */
	private double cost = 0;

	/** The Comment on this Measure */
	private String comment = "";

	/** The "ToDo" of this Measure */
	private String toDo = "";

	private String responsible = "";

	/** ticket id for ticketing system */
	private String ticket;

	/** The Phase object for this measure */
	private Phase phase = null;

	public Measure() {
	}

	public Measure(MeasureDescription measureDescription) {
		this.measureDescription = measureDescription;
	}

	/***********************************************************************************************
	 * Getters and Setters
	 **********************************************************************************************/

	/**
	 * getPhase: <br>
	 * Returns the "phase" field value (object)
	 * 
	 * @return The Phase object
	 */
	@ManyToOne
	@JoinColumn(name = "fiPhase", nullable = false)
	@Cascade({ CascadeType.SAVE_UPDATE, CascadeType.MERGE })
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@Access(AccessType.FIELD)
	public Phase getPhase() {
		return phase;
	}

	/**
	 * setPhase: <br>
	 * Sets the "phase" field with a Phase object
	 * 
	 * @param phase
	 *              The object to set the Phase
	 */
	public void setPhase(Phase phase) {
		this.phase = phase;
	}

	/**
	 * getId: <br>
	 * Returns the "id" field value
	 * 
	 * @return The Measure Identifier
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idMeasure")
	public int getId() {
		return id;
	}

	/**
	 * setId: <br>
	 * Sets the "id" field with a value
	 * 
	 * @param id
	 *           The Measure ID
	 */
	public void setId(int id) {
		if (id < 1) {
			throw new IllegalArgumentException("Measure ID value needs to be >= 1!");
		}
		this.id = id;
	}

	/**
	 * getStatus: <br>
	 * Returns the "status" field value
	 * 
	 * @return The Status of the Measure
	 */
	@Column(name = "dtStatus", nullable = false)
	public String getStatus() {
		return status;
	}

	/**
	 * setStatus: <br>
	 * Sets the "status" field with a value
	 * 
	 * @param status
	 *               The value to set the Status
	 */
	public void setStatus(String status) {

		if ((status == null) || (status.trim().isEmpty())
				|| !status.trim().matches(Constant.REGEXP_VALID_MEASURE_STATUS))
			this.status = Constant.MEASURE_STATUS_NOT_APPLICABLE;
		else
			this.status = status.trim();
	}

	/**
	 * getImplementationRate: <br>
	 * Returns the "implementationRate" field value as Object
	 * 
	 * @return The Implementation Rate Value as Object
	 */
	@Transient
	public Object getImplementationRate() {
		return this.implementationRate;
	}

	/**
	 * setImplementationRate: <br>
	 * Sets the "implementationRate" field with a Object
	 * 
	 * @param implementationRate
	 *                           The value to set the Implementation Rate
	 * @throws TrickException
	 */
	public void setImplementationRate(Object implementationRate) throws TrickException {
		this.implementationRate = implementationRate;
	}

	/**
	 * getImplementationRate: <br>
	 * Returns the "implementationRate" field value (Real Value)
	 * 
	 * @return The Implementation Rate Real Value
	 */
	@Transient
	public abstract double getImplementationRateValue(ValueFactory factory);

	/**
	 * getImplementationRate: <br>
	 * Returns the "implementationRate" field value (Real Value)
	 * 
	 * @return The Implementation Rate Real Value
	 */
	@Transient
	public abstract double getImplementationRateValue(Map<String, Double> factory);

	@Transient
	public double getImplementationRateValue() {
		return getImplementationRateValue(Collections.emptyMap());
	}

	@Transient
	public abstract List<String> getVariablesInvolvedInImplementationRateValue();

	@Transient
	public double getImplementationRateValue(List<IAcronymParameter> expressionParameters) {
		// Turn expression parameters into a map { key => value }
		return this.getImplementationRateValue(new ValueFactory(expressionParameters));
	}

	/**
	 * getInternalWL: <br>
	 * Returns the "internalWL" field value
	 * 
	 * @return The Internal Workload
	 */
	@Column(name = "dtInternalWorkLoad", nullable = false)
	public double getInternalWL() {
		return internalWL;
	}

	/**
	 * setInternalWL: <br>
	 * Sets the "internalWL" field with a value
	 * 
	 * @param internalWL
	 *                   The value to set the Internal Workload
	 * @throws TrickException
	 */
	public void setInternalWL(double internalWL) throws TrickException {
		if (internalWL < 0)
			throw new TrickException("error.measure.internal_workload", "Internal workload cannot be negative");
		this.internalWL = internalWL;
	}

	/**
	 * getExternalWL: <br>
	 * Returns the "externalWL" field value
	 * 
	 * @return The External Workload
	 */
	@Column(name = "dtExternalWorkLoad", nullable = false)
	public double getExternalWL() {
		return externalWL;
	}

	/**
	 * setExternalWL: <br>
	 * Sets the "externalWL" field with a value
	 * 
	 * @param externalWL
	 *                   The value to set the External Workload
	 * @throws TrickException
	 */
	public void setExternalWL(double externalWL) throws TrickException {
		if (externalWL < 0)
			throw new TrickException("error.measure.external_workload", "External workload cannot be negative");
		this.externalWL = externalWL;
	}

	/**
	 * getInvestment: <br>
	 * Returns the "investment" field value
	 * 
	 * @return The Investment fo the measure
	 */
	@Column(name = "dtInvestment", nullable = false)
	public double getInvestment() {
		return investment;
	}

	/**
	 * setInvestment: <br>
	 * Sets the "investment" field with a value
	 * 
	 * @param investment
	 *                   The value to set the Investment
	 * @throws TrickException
	 */
	public void setInvestment(double investment) throws TrickException {
		if (investment < 0)
			throw new TrickException("error.measure.investment", "Investment cannot be negative");
		this.investment = investment;
	}

	/**
	 * getLifetime: <br>
	 * Returns the "lifetime" field value
	 * 
	 * @return The Lifetime value
	 */
	@Column(name = "dtLifetime", nullable = false)
	public double getLifetime() {
		return lifetime;
	}

	/**
	 * setLifetime: <br>
	 * Sets the "lifetime" field with a value
	 * 
	 * @param lifetime
	 *                 The value to set the Lifetime
	 * @throws TrickException
	 */
	public void setLifetime(double lifetime) throws TrickException {
		if (lifetime < 0)
			throw new TrickException("error.measure.lifetime", "Lifetime cannot be negative");
		this.lifetime = lifetime;
	}

	/**
	 * getCost: <br>
	 * Returns the "cost" field value
	 * 
	 * @return The Cost of the Measure
	 */
	@Column(name = "dtCost", nullable = false)
	public double getCost() {
		return cost;
	}

	/**
	 * setCost: <br>
	 * Sets the "cost" field with a value
	 * 
	 * @param cost
	 *             The value to set the Cost
	 * @throws TrickException
	 */
	public void setCost(double cost) throws TrickException {
		if (cost < 0 && cost != -1)
			throw new TrickException("error.measure.cost", "Cost cannot be negative");
		this.cost = cost;
	}

	/**
	 * getComment: <br>
	 * Returns the "comment" field value
	 * 
	 * @return The Measure Comment
	 */
	@Column(name = "dtComment", nullable = false, length = 16777216)
	public String getComment() {
		return comment;
	}

	/**
	 * setComment: <br>
	 * Sets the "comment" field with a value
	 * 
	 * @param comment
	 *                The value to set the Comment
	 */
	public void setComment(String comment) {
		this.comment = comment == null ? "" : comment;
	}

	/**
	 * getToDo: <br>
	 * Returns the "toDo" field value
	 * 
	 * @return The "ToDo" Comment
	 */
	@Column(name = "dtToDo", nullable = false, length = 16777216)
	public String getToDo() {
		return toDo;
	}

	/**
	 * setToDo: <br>
	 * Sets the "toDo" field with a value
	 * 
	 * @param toDo
	 *             The value to set the "ToDo" comment
	 */
	public void setToDo(String toDo) {
		this.toDo = toDo == null ? "" : toDo;
	}

	/**
	 * getAnalysisStandard: <br>
	 * Description
	 * 
	 * @return
	 */
	@ManyToOne
	@Access(AccessType.FIELD)
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@JoinColumn(name = "fiAnalysisStandard", nullable = false)
	@Cascade({ CascadeType.SAVE_UPDATE, CascadeType.MERGE })
	public AnalysisStandard getAnalysisStandard() {
		return analysisStandard;
	}

	/**
	 * setAnalysisStandard: <br>
	 * Description
	 * 
	 * @param analysisStandard
	 */
	public void setAnalysisStandard(AnalysisStandard analysisStandard) {
		this.analysisStandard = analysisStandard;
	}

	/**
	 * getMeasureDescription: <br>
	 * Returns the measureDescription field value.
	 * 
	 * @return The value of the measureDescription field
	 */
	@ManyToOne
	@Access(AccessType.FIELD)
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@JoinColumn(name = "fiMeasureDescription", nullable = false)
	@Cascade({ CascadeType.SAVE_UPDATE, CascadeType.MERGE })
	public MeasureDescription getMeasureDescription() {
		return measureDescription;
	}

	/**
	 * setMeasureDescription: <br>
	 * Sets the Field "measureDescription" with a value.
	 * 
	 * @param measureDescription
	 *                           The Value to set the measureDescription field
	 */
	public void setMeasureDescription(MeasureDescription measureDescription) {
		this.measureDescription = measureDescription;
	}

	/**
	 * getInternalMaintenance: <br>
	 * Returns the internalMaintenance field value.
	 * 
	 * @return The value of the internalMaintenance field
	 */
	@Column(name = "dtInternalMaintenance", nullable = false)
	public double getInternalMaintenance() {
		return internalMaintenance;
	}

	/**
	 * setInternalMaintenance: <br>
	 * Sets the Field "internalMaintenance" with a value.
	 * 
	 * @param internalMaintenance
	 *                            The Value to set the internalMaintenance field
	 */
	public void setInternalMaintenance(double internalMaintenance) {
		this.internalMaintenance = internalMaintenance;
	}

	/**
	 * getExternalMaintenance: <br>
	 * Returns the externalMaintenance field value.
	 * 
	 * @return The value of the externalMaintenance field
	 */
	@Column(name = "dtExternalMaintenance", nullable = false)
	public double getExternalMaintenance() {
		return externalMaintenance;
	}

	/**
	 * setExternalMaintenance: <br>
	 * Sets the Field "externalMaintenance" with a value.
	 * 
	 * @param externalMaintenance
	 *                            The Value to set the externalMaintenance field
	 */
	public void setExternalMaintenance(double externalMaintenance) {
		this.externalMaintenance = externalMaintenance;
	}

	/**
	 * getRecurrentInvestment: <br>
	 * Returns the recurrentInvestment field value.
	 * 
	 * @return The value of the recurrentInvestment field
	 */
	@Column(name = "dtRecurrentInvestment", nullable = false)
	public double getRecurrentInvestment() {
		return recurrentInvestment;
	}

	/**
	 * setRecurrentInvestment: <br>
	 * Sets the Field "recurrentInvestment" with a value.
	 * 
	 * @param recurrentInvestment
	 *                            The Value to set the recurrentInvestment field
	 */
	public void setRecurrentInvestment(double recurrentInvestment) {
		this.recurrentInvestment = recurrentInvestment;
	}

	/**
	 * TicketingTask id
	 * 
	 * @return the ticket
	 */
	@Column(name = "dtTicket")
	public String getTicket() {
		return ticket;
	}

	/**
	 * TicketingTask id
	 * 
	 * @param ticket
	 *               the ticket to set
	 */
	public void setTicket(String ticket) {
		this.ticket = ticket;
	}

	@Column(name = "dtImportance")
	public int getImportance() {
		return importance;
	}

	public void setImportance(int importance) {
		if (importance < 1 || importance > 3)
			throw new TrickException("error.measure.importance.out_of_bound",
					"Measure importance should be between 1 and 3");
		this.importance = importance;
	}

	/**
	 * hashCode:<br>
	 * Used inside equals method.
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((measureDescription == null) ? 0 : measureDescription.hashCode());
		result = prime * result + id;
		return result;
	}

	/**
	 * equals: <br>
	 * Check if this object equals another object of the same type. Equal means: the
	 * field id, description, domain and reference.
	 * 
	 * @param obj
	 *            The other object to check on
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null)
			return false;

		if (getClass() != obj.getClass())
			return false;

		Measure other = (Measure) obj;

		if (getId() > 0 && other.getId() > 0)
			return getId() == other.getId();

		if (measureDescription == null) {
			if (other.measureDescription != null)
				return false;
		} else if (!measureDescription.equals(other.measureDescription))
			return false;
		return true;
	}

	/**
	 * ComputeCost: <br>
	 * Description
	 * 
	 * @param measure
	 * @param analysis
	 * @throws TrickException
	 */
	public static void ComputeCost(Measure measure, Analysis analysis) throws TrickException {
		// ****************************************************************
		// * variable initialisation
		// ****************************************************************

		// ****************************************************************
		// * select external and internal setup rate from parameters
		// ****************************************************************

		final double internalSetupValue = analysis.findParameter(Constant.PARAMETER_INTERNAL_SETUP_RATE);

		final double externalSetupValue = analysis.findParameter(Constant.PARAMETER_EXTERNAL_SETUP_RATE);

		final double lifetimeDefault = analysis.findParameter(Constant.PARAMETER_LIFETIME_DEFAULT);

		final double implementationRate = measure.getImplementationRateValue(analysis.getExpressionParameters())*0.01;

		final boolean isFullCostRelated = analysis.findSetting(AnalysisSetting.ALLOW_FULL_COST_RELATED_TO_MEASURE);

		// calculate the cost
		double cost = Analysis.computeCost(internalSetupValue, externalSetupValue, lifetimeDefault,
				measure.getInternalMaintenance(), measure.getExternalMaintenance(),
				measure.getRecurrentInvestment(), measure.getInternalWL(), measure.getExternalWL(),
				measure.getInvestment(), measure.getLifetime(), implementationRate, isFullCostRelated);
				
		// return calculated cost
		if (cost > 0)
			measure.setCost(cost);
		else
			measure.setCost(0);
	}

	/**
	 * clone: <br>
	 * Description
	 * 
	 * @throws TrickException
	 *
	 * @{tags
	 *
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Measure clone() throws CloneNotSupportedException {
		return (Measure) super.clone();
	}

	/**
	 * duplicate: <br>
	 * Description
	 * 
	 * @return
	 * @throws CloneNotSupportedException
	 * @throws TrickException
	 */
	public Measure duplicate(AnalysisStandard astandard, Phase phase) throws CloneNotSupportedException {
		Measure measure = (Measure) super.clone();
		measure.id = -1;
		measure.setAnalysisStandard(astandard);
		measure.setPhase(phase);
		if (astandard.getStandard().isAnalysisOnly()) {
			MeasureDescription desc = measureDescription.duplicate(astandard.getStandard());
			measure.setMeasureDescription(desc);
		}
		return measure;
	}

	@Column(name = "dtResponsible")
	public String getResponsible() {
		return responsible;
	}

	public void setResponsible(String responsible) {
		this.responsible = responsible;
	}

	@Transient
	public String getKey() {
		return key(measureDescription.getStandard(), measureDescription.getReference());
	}

	@Transient
	public String getKeyName() {
		return keyName(measureDescription.getStandard(), measureDescription.getReference());
	}

	@Transient
	public static String key(Standard standard, String reference) {
		return standard.getId() + "^-'MEASURE'-^" + reference;
	}

	public static String keyName(Standard standard, String reference) {
		return standard.getName() + "^NAME-'MEASURE'-NAME^" + reference;
	}
}
