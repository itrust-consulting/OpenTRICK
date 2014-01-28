package lu.itrust.business.TS;

import java.io.Serializable;
import java.util.List;

import lu.itrust.business.TS.tsconstant.Constant;

/**
 * Measure: <br>
 * This class represents a Measure and its data. This class has fields that are used in Maturity and
 * AnalysisNorm Meaure classes. (Both are extended by this class)
 * 
 * @author itrust consulting s.à r.l. - BJA,SME
 * @version 0.1
 * @since 2012-08-21
 */
public abstract class Measure implements Serializable, Cloneable {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/
		
	/** serialVersionUID */
	private static final long serialVersionUID = 1L;

	/** The Measure Identifier */
	private int id = -1;

	/** Analysis Norm Object */
	private AnalysisNorm analysisNorm = null;

	/** The Measure Domain */
	private MeasureDescription measureDescription = null;

	/** Flag to determine if measure can be used in the action plan (before: measure had to be level 3) */
	private boolean computable = true;
	
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

	/** The Maintenance of the Measure (in percent) */
	private double maintenance = 0;

	/** The Cost of the Measure (Currency) */
	private double cost = 0;

	/** The Comment on this Measure */
	private String comment = "";

	/** The "ToDo" comment of this Measure */
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
			throw new IllegalArgumentException(
					"Measure Status value needs to be one of these values (AP, NA, M)!");
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
	public Object getImplementationRate() {
		return this.implementationRate;
	}

	/**
	 * setImplementationRate: <br>
	 * Sets the "implementationRate" field with a Object
	 * 
	 * @param implementationRate
	 *            The value to set the Implementation Rate
	 */
	public void setImplementationRate(Object implementationRate) {
		this.implementationRate = implementationRate;
	}

	/**
	 * getImplementationRate: <br>
	 * Returns the "implementationRate" field value (Real Value)
	 * 
	 * @return The Implementation Rate Real Value
	 */
	public abstract double getImplementationRateValue();

	/**
	 * getInternalWL: <br>
	 * Returns the "internalWL" field value
	 * 
	 * @return The Internal Workload
	 */
	public double getInternalWL() {
		return internalWL;
	}

	/**
	 * setInternalWL: <br>
	 * Sets the "internalWL" field with a value
	 * 
	 * @param internalWL
	 *            The value to set the Internal Workload
	 */
	public void setInternalWL(double internalWL) {
		if (internalWL < 0) {
			throw new IllegalArgumentException("Measure InternalWL needs to be >= 0!");
		}
		this.internalWL = internalWL;
	}

	/**
	 * getExternalWL: <br>
	 * Returns the "externalWL" field value
	 * 
	 * @return The External Workload
	 */
	public double getExternalWL() {
		return externalWL;
	}

	/**
	 * setExternalWL: <br>
	 * Sets the "externalWL" field with a value
	 * 
	 * @param externalWL
	 *            The value to set the External Workload
	 */
	public void setExternalWL(double externalWL) {
		if (externalWL < 0) {
			throw new IllegalArgumentException("Measure ExternalWL needs to be >= 0!");
		}
		this.externalWL = externalWL;
	}

	/**
	 * getInvestment: <br>
	 * Returns the "investment" field value
	 * 
	 * @return The Investment fo the measure
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
		if (investment < 0) {
			throw new IllegalArgumentException("Measure Investment needs to be >= 0!");
		}
		this.investment = investment;
	}

	/**
	 * getLifetime: <br>
	 * Returns the "lifetime" field value
	 * 
	 * @return The Lifetime value
	 */
	public double getLifetime() {
		return lifetime;
	}

	/**
	 * setLifetime: <br>
	 * Sets the "lifetime" field with a value
	 * 
	 * @param lifetime
	 *            The value to set the Lifetime
	 */
	public void setLifetime(double lifetime) {
		if (lifetime < 0) {
			throw new IllegalArgumentException("Measure Lifetime needs to be >= 0!");
		}
		this.lifetime = lifetime;
	}

	/**
	 * getMaintenance: <br>
	 * Returns the "maintenance" field value
	 * 
	 * @return The Maintenance
	 */
	public double getMaintenance() {
		return maintenance;
	}

	/**
	 * setMaintenance: <br>
	 * Sets the "maintenance" field with a value
	 * 
	 * @param maintenance
	 *            The value to set the Maintenance
	 */
	public void setMaintenance(double maintenance) {
		if (maintenance < 0 && maintenance!=-1) {
			throw new IllegalArgumentException("Measure Maintenance needs to be >= 0!");
		}
		this.maintenance = maintenance;
	}

	/**
	 * getCost: <br>
	 * Returns the "cost" field value
	 * 
	 * @return The Cost of the Measure
	 */
	public double getCost() {
		return cost;
	}

	/**
	 * setCost: <br>
	 * Sets the "cost" field with a value
	 * 
	 * @param cost
	 *            The value to set the Cost
	 */
	public void setCost(double cost) {
		if (cost < 0) {
			throw new IllegalArgumentException("Measure Cost needs to be >= 0!");
		}
		this.cost = cost;
	}

	/**
	 * getComment: <br>
	 * Returns the "comment" field value
	 * 
	 * @return The Measure Comment
	 */
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
		result =
			prime * result + ((measureDescription == null) ? 0 : measureDescription.hashCode());
		result = prime * result + id;
		return result;
	}

	/**
	 * equals: <br>
	 * Check if this object equals another object of the same type. Equal means: the field id,
	 * description, domain and reference.
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
		if (measureDescription == null) {
			if (other.measureDescription != null) {
				return false;
			}
		} else if (!measureDescription.equals(other.measureDescription)) {
			return false;
		}
		if (id != other.id) {
			return false;
		}
		return true;
	}
	
	public static void ComputeCost(Measure measure, List<Parameter> parameters) {
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

		// parse parameters
		for (Parameter parameter : parameters) {

			// check if parameter is Internal Setup Rate -> YES
			if (parameter.getDescription().equals(Constant.PARAMETER_INTERNAL_SETUP_RATE)) {

				// ****************************************************************
				// * set internal Setup rate
				// ****************************************************************
				internalSetupValue = parameter.getValue();

				// check if all parameters are set -> YES
				if ((internalSetupValue != -1) && (externalSetupValue != -1) && (lifetimeDefault != -1) && (maintenanceDefault != -1)) {

					// leave loop
					break;
				}
			}

			// check if parameter is External Setup Rate -> YES
			if (parameter.getDescription().equals(Constant.PARAMETER_EXTERNAL_SETUP_RATE)) {

				// ****************************************************************
				// * set external setup rate
				// ****************************************************************
				externalSetupValue = parameter.getValue();

				// check if all parameters are set -> YES
				if ((internalSetupValue != -1) && (externalSetupValue != -1) && (lifetimeDefault != -1) && (maintenanceDefault != -1)) {

					// leave loop
					break;
				}
			}

			// check if parameter is default lifetime -> YES
			if (parameter.getDescription().equals(Constant.PARAMETER_LIFETIME_DEFAULT)) {

				// ****************************************************************
				// * set default lifetime
				// ****************************************************************
				lifetimeDefault = parameter.getValue();

				// check if all parameters are set -> YES
				if ((internalSetupValue != -1) && (externalSetupValue != -1) && (lifetimeDefault != -1) && (maintenanceDefault != -1)) {

					// leave loop
					break;
				}
			}

			// check if parameter is default maintenance -> YES
			if (parameter.getDescription().equals(Constant.PARAMETER_MAINTENANCE_DEFAULT)) {

				// ****************************************************************
				// * set default maintenance
				// ****************************************************************
				maintenanceDefault = parameter.getValue();

				// check if all parameters are set -> YES
				if ((internalSetupValue != -1) && (externalSetupValue != -1) && (lifetimeDefault != -1) && (maintenanceDefault != -1)) {

					// leave loop
					break;
				}
			}
		}

		// calculate the cost
		cost = Analysis.computeCost(internalSetupValue, externalSetupValue, lifetimeDefault, maintenanceDefault, measure.getInternalWL(), measure.getExternalWL(),
				measure.getInvestment(), measure.getLifetime(), measure.getMaintenance());
		// return calculated cost
		if (cost >= 0)
			measure.setCost(cost);
		System.out.println(cost);
	}

	/* (non-Javadoc)
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

	/** isComputable: <br>
	 * Returns the computable field value.
	 * 
	 * @return The value of the computable field
	 */
	public boolean isComputable() {
		return computable;
	}

	/** setComputable: <br>
	 * Sets the Field "computable" with a value.
	 * 
	 * @param computable 
	 * 			The Value to set the computable field
	 */
	public void setComputable(boolean computable) {
		this.computable = computable;
	}
}