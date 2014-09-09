package lu.itrust.business.TS;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.exception.TrickException;

/**
 * Measure: <br>
 * This class represents a Measure and its data. This class has fields that are
 * used in Maturity and AnalysisNorm Meaure classes. (Both are extended by this
 * class)
 * 
 * @author itrust consulting s.à r.l. - BJA,SME
 * @version 0.1
 * @since 2012-08-21
 */
@Entity 
@Inheritance(strategy=InheritanceType.JOINED)
public abstract class Measure implements Serializable, Cloneable {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** serialVersionUID */
	@Transient
	private static final long serialVersionUID = 1L;

	/** The Measure Identifier */
	private int id = -1;

	/** Analysis Norm Object */
	private AnalysisNorm analysisNorm = null;

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

	/** The old Maintenance of the Measure (in percent) */
	private double maintenance = 0;

	/** The internal Maintenance of the Measure (in Man Days) */
	private double internalMaintenance = 0;

	/** The external Maintenance of the Measure (in Man Days) */
	private double externalMaintenance = 0;

	/** The recurrent investment of maintenance of the Measure (Currency) */
	private double recurrentInvestment = 0;

	/** The Cost of the Measure (Currency) */
	private double cost = 0;

	/** The Comment on this Measure */
	private String comment = "";

	/** The "ToDo" of this Measure */
	private String toDo = "";

	/** The Phase object for this measure */
	private Phase phase = null;

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
	@JoinColumn(name="fiPhase", nullable=false)
	@Cascade(CascadeType.SAVE_UPDATE)
	@Access(AccessType.FIELD)
	public Phase getPhase() {
		return phase;
	}

	/**
	 * setPhase: <br>
	 * Sets the "phase" field with a Phase object
	 * 
	 * @param phase
	 *            The object to set the Phase
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
	@Id @GeneratedValue 
	@Column(name="idMeasure")
	public int getId() {
		return id;
	}

	/**
	 * setId: <br>
	 * Sets the "id" field with a value
	 * 
	 * @param id
	 *            The Measure ID
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
	@Column(name="dtStatus", nullable=false)
	public String getStatus() {
		return status;
	}

	/**
	 * setStatus: <br>
	 * Sets the "status" field with a value
	 * 
	 * @param status
	 *            The value to set the Status
	 */
	public void setStatus(String status) {

		if ((status == null) || (status.trim().isEmpty())) {
			this.status = Constant.MEASURE_STATUS_NOT_APPLICABLE;
		} else if (!status.trim().matches(Constant.REGEXP_VALID_MEASURE_STATUS)) {
			throw new IllegalArgumentException("Measure Status value needs to be one of these values (AP, NA, M)!");
		} else {
			this.status = status.trim();
		}
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
	 *            The value to set the Implementation Rate
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
	public abstract double getImplementationRateValue();

	/**
	 * getInternalWL: <br>
	 * Returns the "internalWL" field value
	 * 
	 * @return The Internal Workload
	 */
	@Column(name="dtInternalWorkLoad", nullable=false)
	@Access(AccessType.FIELD)
	public double getInternalWL() {
		return internalWL;
	}

	/**
	 * setInternalWL: <br>
	 * Sets the "internalWL" field with a value
	 * 
	 * @param internalWL
	 *            The value to set the Internal Workload
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
	@Column(name="dtExternalWorkLoad", nullable=false)
	@Access(AccessType.FIELD)
	public double getExternalWL() {
		return externalWL;
	}

	/**
	 * setExternalWL: <br>
	 * Sets the "externalWL" field with a value
	 * 
	 * @param externalWL
	 *            The value to set the External Workload
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
	@Column(name="dtInvestment", nullable=false)
	@Access(AccessType.FIELD)
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
	@Column(name="dtLifetime", nullable=false)
	@Access(AccessType.FIELD)
	public double getLifetime() {
		return lifetime;
	}

	/**
	 * setLifetime: <br>
	 * Sets the "lifetime" field with a value
	 * 
	 * @param lifetime
	 *            The value to set the Lifetime
	 * @throws TrickException
	 */
	public void setLifetime(double lifetime) throws TrickException {
		if (lifetime < 0)
			throw new TrickException("error.measure.lifetime", "Lifetime cannot be negative");
		this.lifetime = lifetime;
	}

	/**
	 * getMaintenance: <br>
	 * Returns the "maintenance" field value
	 * 
	 * @return The Maintenance
	 */
	@Column(name="dtMaintenance", nullable=false)
	@Access(AccessType.FIELD)
	public double getMaintenance() {
		return maintenance;
	}

	/**
	 * setMaintenance: <br>
	 * Sets the "maintenance" field with a value
	 * 
	 * @param maintenance
	 *            The value to set the Maintenance
	 * @throws TrickException
	 */
	public void setMaintenance(double maintenance) throws TrickException {
		if (maintenance < 0 && maintenance != -1)
			throw new TrickException("error.measure.maintenance", "Maintenance cannot be negative except -1");
		this.maintenance = maintenance;
	}

	/**
	 * getCost: <br>
	 * Returns the "cost" field value
	 * 
	 * @return The Cost of the Measure
	 */
	@Column(name="dtCost", nullable=false)
	@Access(AccessType.FIELD)
	public double getCost() {
		return cost;
	}

	/**
	 * setCost: <br>
	 * Sets the "cost" field with a value
	 * 
	 * @param cost
	 *            The value to set the Cost
	 * @throws TrickException
	 */
	public void setCost(double cost) throws TrickException {
		if (cost < 0)
			throw new TrickException("error.measure.cost", "Cost cannot be negative");
		this.cost = cost;
	}

	/**
	 * getComment: <br>
	 * Returns the "comment" field value
	 * 
	 * @return The Measure Comment
	 */
	@Column(name="dtComment", nullable=false, columnDefinition="LONGTEXT")
	@Access(AccessType.FIELD)
	public String getComment() {
		return comment;
	}

	/**
	 * setComment: <br>
	 * Sets the "comment" field with a value
	 * 
	 * @param comment
	 *            The value to set the Comment
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * getToDo: <br>
	 * Returns the "toDo" field value
	 * 
	 * @return The "ToDo" Comment
	 */
	@Column(name="dtToDo", nullable=false, columnDefinition="LONGTEXT")
	@Access(AccessType.FIELD)
	public String getToDo() {
		return toDo;
	}

	/**
	 * setToDo: <br>
	 * Sets the "toDo" field with a value
	 * 
	 * @param toDo
	 *            The value to set the "ToDo" comment
	 */
	public void setToDo(String toDo) {
		this.toDo = toDo;
	}

	/**
	 * getNorm: <br>
	 * Returns the analysisNorm field value.
	 * 
	 * @return The value of the analysisNorm field
	 */
	@ManyToOne 
	@JoinColumn(name="fiAnalysisNorm", nullable=false)
	@Access(AccessType.FIELD)
	public AnalysisNorm getAnalysisNorm() {
		return analysisNorm;
	}

	/**
	 * setNorm: <br>
	 * Sets the Field "analysisNorm" with a value.
	 * 
	 * @param analysisNorm
	 *            The Value to set the analysisNorm field
	 */
	public void setAnalysisNorm(AnalysisNorm analysisNorm) {
		this.analysisNorm = analysisNorm;
	}

	/**
	 * getMeasureDescription: <br>
	 * Returns the measureDescription field value.
	 * 
	 * @return The value of the measureDescription field
	 */
	@ManyToOne 
	@JoinColumn(name="fiMeasureDescription", nullable=false)
	@Access(AccessType.FIELD)
	@Cascade(CascadeType.ALL)
	public MeasureDescription getMeasureDescription() {
		return measureDescription;
	}

	/**
	 * setMeasureDescription: <br>
	 * Sets the Field "measureDescription" with a value.
	 * 
	 * @param measureDescription
	 *            The Value to set the measureDescription field
	 */
	public void setMeasureDescription(MeasureDescription measureDescription) {
		this.measureDescription = measureDescription;
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
	 * Check if this object equals another object of the same type. Equal means:
	 * the field id, description, domain and reference.
	 * 
	 * @param obj
	 *            The other object to check on
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Measure other = (Measure) obj;

		if (getId() > 0 && getId() == other.getId())
			return true;
		else if (getId() > 0 && other.getId() > 0)
			return false;
		else if (measureDescription == null) {
			if (other.measureDescription != null) {
				return false;
			}
		} else if (!measureDescription.equals(other.measureDescription)) {
			return false;
		}
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
	@Deprecated
	public static void ComputeCost(Measure measure, List<Parameter> parameters) throws TrickException {
		// ****************************************************************
		// * variable initialisation
		// ****************************************************************
		double cost = 0;
		double externalSetupValue = -1;
		double internalSetupValue = -1;
		double lifetimeDefault = -1;
		double maintenanceDefault = -1;

		// ****************************************************************
		// * select external and internal setup rate from parameters
		// ****************************************************************

		for (Parameter parameter : parameters) {
			if (parameter.getDescription().equals(Constant.PARAMETER_INTERNAL_SETUP_RATE)) {
				internalSetupValue = parameter.getValue();
				break;
			}

		}

		for (Parameter parameter : parameters) {
			if (parameter.getDescription().equals(Constant.PARAMETER_EXTERNAL_SETUP_RATE)) {
				externalSetupValue = parameter.getValue();
				break;
			}

		}

		for (Parameter parameter : parameters) {
			if (parameter.getDescription().equals(Constant.PARAMETER_LIFETIME_DEFAULT)) {
				lifetimeDefault = parameter.getValue();
				break;
			}

		}

		for (Parameter parameter : parameters) {
			if (parameter.getDescription().equals(Constant.PARAMETER_MAINTENANCE_DEFAULT)) {
				maintenanceDefault = parameter.getValue();
				break;
			}

		}

		// calculate the cost
		cost = Analysis.computeCost(internalSetupValue, externalSetupValue, lifetimeDefault, maintenanceDefault, measure.getMaintenance(), measure.getInternalWL(),
				measure.getExternalWL(), measure.getInvestment(), measure.getLifetime());
		// return calculated cost
		if (cost >= 0)
			measure.setCost(cost);
	}

	public static void ComputeCost(Measure measure, Analysis analysis) throws TrickException {
		// ****************************************************************
		// * variable initialisation
		// ****************************************************************
		double cost = 0;
		double externalSetupValue = -1;
		double internalSetupValue = -1;
		double lifetimeDefault = -1;

		// ****************************************************************
		// * select external and internal setup rate from parameters
		// ****************************************************************

		internalSetupValue = analysis.getParameter(Constant.PARAMETER_INTERNAL_SETUP_RATE);

		externalSetupValue = analysis.getParameter(Constant.PARAMETER_EXTERNAL_SETUP_RATE);

		lifetimeDefault = analysis.getParameter(Constant.PARAMETER_LIFETIME_DEFAULT);

		// calculate the cost
		cost = Analysis.computeCost(internalSetupValue, externalSetupValue, lifetimeDefault, measure.getInternalMaintenance(), measure.getExternalMaintenance(),
				measure.getRecurrentInvestment(), measure.getInternalWL(), measure.getExternalWL(), measure.getInvestment(), measure.getLifetime());
		// return calculated cost
		if (cost >= 0)
			measure.setCost(cost);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Measure clone() throws CloneNotSupportedException {
		return (Measure) super.clone();
	}

	public Measure duplicate() throws CloneNotSupportedException {
		Measure measure = (Measure) super.clone();
		measure.id = -1;
		return measure;
	}

	/**
	 * getInternalMaintenance: <br>
	 * Returns the internalMaintenance field value.
	 * 
	 * @return The value of the internalMaintenance field
	 */
	@Column(name="dtInternalMaintenance", nullable=false)
	@Access(AccessType.FIELD)
	public double getInternalMaintenance() {
		return internalMaintenance;
	}

	/**
	 * setInternalMaintenance: <br>
	 * Sets the Field "internalMaintenance" with a value.
	 * 
	 * @param internalMaintenance
	 *            The Value to set the internalMaintenance field
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
	@Column(name="dtExternalMaintenance", nullable=false)
	@Access(AccessType.FIELD)
	public double getExternalMaintenance() {
		return externalMaintenance;
	}

	/**
	 * setExternalMaintenance: <br>
	 * Sets the Field "externalMaintenance" with a value.
	 * 
	 * @param externalMaintenance
	 *            The Value to set the externalMaintenance field
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
	@Column(name="dtRecurrentInvestment", nullable=false)
	@Access(AccessType.FIELD)
	public double getRecurrentInvestment() {
		return recurrentInvestment;
	}

	/**
	 * setRecurrentInvestment: <br>
	 * Sets the Field "recurrentInvestment" with a value.
	 * 
	 * @param recurrentInvestment
	 *            The Value to set the recurrentInvestment field
	 */
	public void setRecurrentInvestment(double recurrentInvestment) {
		this.recurrentInvestment = recurrentInvestment;
	}

}