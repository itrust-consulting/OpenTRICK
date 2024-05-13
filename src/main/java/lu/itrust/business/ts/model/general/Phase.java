package lu.itrust.business.ts.model.general;

import java.time.Duration;
import java.time.Period;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.model.analysis.Analysis;

/**
 * Represents a phase in the business process.
 * Each phase has a unique identifier, phase number, begin date, end date, and associated analysis.
 * It also contains various transient fields for tracking measures, workloads, and other metrics.
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "fiAnalysis", "dtNumber" }))
public class Phase implements Cloneable {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** phase identifier, unsaved value = -1 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idPhase")
	private int id = 0;

	/** The Phase Number */
	@Column(name = "dtNumber", nullable = false)
	private int number;

	/** The Begin Date of the Phase */
	//@Temporal(TemporalType.DATE)
	@Column(name = "dtBeginDate")
	private Date beginDate;

	/** The End Date of the Phase */
	//@Temporal(TemporalType.DATE)
	@Column(name = "dtEndDate")
	private Date endDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fiAnalysis", nullable = false)
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@Access(AccessType.FIELD)
	private Analysis analysis = null;

	@Transient
	private int measureCount;

	@Transient
	private int implementedMeasureCount;

	@Transient
	private double implementedInternalWorkload;

	@Transient
	private double implementedExternalWorkload;

	@Transient
	private double investment;

	@Transient
	private boolean removable = true;

	@Transient
	private int complianceCount;

	@Transient
	private double internalWorkload;

	@Transient
	private double externalWorkload;

	/***********************************************************************************************
	 * Constructors
	 **********************************************************************************************/

	/**
	 * Constructor:<br>
	 */
	public Phase() {
	}

	/**
	 * Constructor:<br>
	 *
	 * @param number The Phase Number
	 */
	public Phase(int number) {
		this.id = 0;
		this.number = number;
	}

	/***********************************************************************************************
	 * Getters and Setters
	 **********************************************************************************************/

	/**
	 * getNumber: <br>
	 * Returns the "number" field value
	 *
	 * @return The Phase Number
	 */
	public int getNumber() {
		return number;
	}

	/**
	 * setNumber: <br>
	 * Sets the "number" field with a value
	 *
	 * @param number The value to set the Phase Number
	 */
	public void setNumber(int number) {
		this.number = number;
	}

	/**
	 * getBegindate: <br>
	 * Returns the "beginDate" field value
	 *
	 * @return The Begin Date of the Phase
	 */
	public Date getBeginDate() {
		return beginDate;
	}

	/**
	 * setBegindate: <br>
	 * Sets the "beginDate" field with a value
	 *
	 * @param beginDate The value to set the Begin Date of the Phase
	 * @throws TrickException
	 */
	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	/**
	 * getEnddate: <br>
	 * Returns the "endDate" field value
	 *
	 * @return The End Date of the Phase
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * setEnddate: <br>
	 * Sets the "endDate" field with a value
	 *
	 * @param endDate The value to set the End Date of the Phase
	 * @throws TrickException
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * setDates: <br>
	 * Description
	 *
	 * @param beginDate
	 * @param endDate
	 * @throws TrickException
	 */
	public void setDates(Date beginDate, Date endDate) throws TrickException {
		if (!beginDate.before(endDate))
			throw new TrickException("error.phase.begin_date.invalid", "Phase begin time cannot be greater than phase end time");

		if (!endDate.after(beginDate))
			throw new TrickException("error.phase.begin_date.invalid", "Phase end time cannot be less than phase begin time");

		this.beginDate = beginDate;
		this.endDate = endDate;
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
	 * @param analysis The Value to set the analysis field
	 */
	public void setAnalysis(Analysis analysis) {
		this.analysis = analysis;
	}

	/**
	 * hashCode: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + number;
		return result;
	}

	/**
	 * equals: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Phase))
			return false;
		Phase other = (Phase) obj;
		if (id != other.id)
			return false;
		if (number != other.number)
			return false;
		return true;
	}

	/**
	 * getId: <br>
	 * Returns the id field value.
	 *
	 * @return The value of the id field
	 */
	public int getId() {
		return id;
	}

	/**
	 * setId: <br>
	 * Sets the Field "id" with a value.
	 *
	 * @param id The Value to set the id field
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * clone: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Phase clone() throws CloneNotSupportedException {
		return (Phase) super.clone();
	}

	/**
	 * duplicate: <br>
	 * Description
	 *
	 * @return
	 * @throws CloneNotSupportedException
	 */
	public Phase duplicate(Analysis analysis) throws CloneNotSupportedException {
		Phase phase = (Phase) super.clone();
		phase.id = 0;
		phase.analysis = analysis;
		return phase;
	}

	public double getTime() {
		return ComputeDiff(beginDate, endDate);
	}

	/**
	 * ComputeDiff : <br>
	 * This method Calculates an Double Value that Indicates the Difference between
	 * two Dates. It is used to Calculate the Size of the Phase in Years.
	 *
	 * @param beginDate begin date (should be smallest date)
	 * @param endDate   end date (should be biggest date)
	 * @return
	 */
	public static double ComputeDiff(java.util.Date beginDate, java.util.Date endDate) {
		// ****************************************************************
		// * initialise variables
		// ****************************************************************
		double result = 0;
		double yearInMiliseconds = 0;
		Calendar calendarBeginDate = Calendar.getInstance();
		Calendar calendarEndDate = Calendar.getInstance();

		// ****************************************************************
		// * calculate duration of years between two dates
		// ****************************************************************

		// ****************************************************************
		// check if dates are null
		// ****************************************************************
		if ((beginDate == null) || (endDate == null)) {

			// set defualt duration of 1 year
			result = 1.;
		}

		// set year in miliseconds
		yearInMiliseconds = 1000L * 60 * 60 * 24 * 365.25;

		// ****************************************************************
		// * set values for begin and end date
		// ****************************************************************
		calendarBeginDate.setTime(beginDate);
		calendarEndDate.setTime(endDate);

		// calculate difference between two dates
		result = Math.abs((calendarEndDate.getTimeInMillis() - calendarBeginDate.getTimeInMillis()) / yearInMiliseconds);

		// ****************************************************************
		// * return difference of two dates in years
		// ****************************************************************
		return result;
	}

	/**
	 * Returns the measure count.
	 *
	 * @return the measure count
	 */
	public int getMeasureCount() {
		return measureCount;
	}

	/**
	 * Sets the measure count for this Phase.
	 *
	 * @param measureCount the new measure count
	 */
	public void setMeasureCount(int measureCount) {
		this.measureCount = measureCount;
	}

	/**
	 * Returns the investment amount for this phase.
	 *
	 * @return the investment amount
	 */
	public double getInvestment() {
		return investment;
	}

	/**
	 * Sets the investment amount for this phase.
	 *
	 * @param investment the investment amount to set
	 */
	public void setInvestment(double investment) {
		this.investment = investment;
	}

	/**
	 * Calculates and returns the compliance rate of the phase.
	 * The compliance rate is calculated as the ratio of the compliance count to the measure count.
	 * If the measure count is 0, the compliance rate is 0.0.
	 *
	 * @return the compliance rate of the phase
	 */
	public double getComplianceRate() {
		return measureCount == 0 ? 0.0 : ((double) complianceCount) / (double) measureCount;
	}

	/**
	 * Returns the compliance count.
	 *
	 * @return the compliance count
	 */
	public int getComplianceCount() {
		return complianceCount;
	}

	/**
	 * Sets the compliance count for this phase.
	 *
	 * @param complianceCount the compliance count to set
	 */
	public void setComplianceCount(int complianceCount) {
		this.complianceCount = complianceCount;
	}

	/**
	 * Returns the internal workload of the phase.
	 *
	 * @return the internal workload of the phase
	 */
	public double getInternalWorkload() {
		return internalWorkload;
	}

	/**
	 * Sets the internal workload for this phase.
	 *
	 * @param internalWorkload the internal workload value to set
	 */
	public void setInternalWorkload(double internalWorkload) {
		this.internalWorkload = internalWorkload;
	}

	/**
	 * Returns the external workload for this phase.
	 *
	 * @return the external workload
	 */
	public double getExternalWorkload() {
		return externalWorkload;
	}

	/**
	 * Sets the external workload for this phase.
	 *
	 * @param externalWorkload the external workload value to set
	 */
	public void setExternalWorkload(double externalWorkload) {
		this.externalWorkload = externalWorkload;
	}

	/**
		* Returns the duration between the beginDate and endDate.
		*
		* @return the duration between the beginDate and endDate
		*/
	public Duration getDuration() {
		return Duration.ofMillis(endDate.getTime() - beginDate.getTime());
	}

	/**
		* Returns the period between the begin date and end date.
		*
		* @return the period between the begin date and end date
		*/
	public Period getPeriod() {
		return Period.between(beginDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()).normalized();
	}

	/**
	 * Returns the format code based on the period.
	 * The format code is determined by the number of years, months, and days in the period.
	 *
	 * @return The format code.
	 */
	public int getFormatCode() {
		final Period period = getPeriod();
		if (period.isZero())
			return 123;
		else if (period.getDays() == 0)
			return period.getMonths() == 0 ? 23 : period.getYears() == 0 ? 13 : 3;
		else if (period.getMonths() == 0)
			return period.getYears() == 0 ? 12 : 2;
		return period.getYears() == 0 ? 1 : 0;

	}

	/**
	 * Returns the count format code based on the implemented measure count.
	 * 
	 * @return the count format code:
	 *         - 2 if the implemented measure count is 0
	 *         - 1 if the measure count is equal to the implemented measure count
	 *         - 0 otherwise
	 */
	public int getCountFormatCode() {
		if (implementedMeasureCount == 0)
			return 2;
		else if (measureCount == implementedMeasureCount)
			return 1;
		else
			return 0;
	}

	/**
	 * Returns the internal workload format code.
	 * 
	 * @return The internal workload format code:
	 *         - 2 if the implemented internal workload is 0.
	 *         - 1 if the internal workload is equal to the implemented internal workload.
	 *         - 0 otherwise.
	 */
	public int getInternalWorkloadFormatCode() {
		if (implementedInternalWorkload == 0)
			return 2;
		else if (internalWorkload == implementedInternalWorkload)
			return 1;
		else
			return 0;
	}

	/**
	 * Returns the external workload format code based on the implemented external workload and the external workload.
	 * 
	 * @return the external workload format code:
	 *         - 2 if the implemented external workload is 0
	 *         - 1 if the external workload is equal to the implemented external workload
	 *         - 0 otherwise
	 */
	public int getExternalWorkloadFormatCode() {
		if (implementedExternalWorkload == 0)
			return 2;
		else if (externalWorkload == implementedExternalWorkload)
			return 1;
		else
			return 0;
	}

	/**
	 * Returns whether the phase is removable or not.
	 *
	 * @return true if the phase is removable, false otherwise
	 */
	public boolean isRemovable() {
		return removable;
	}

	/**
	 * Sets the flag indicating whether the object is removable.
	 *
	 * @param removable true if the object is removable, false otherwise
	 */
	public void setRemovable(boolean removable) {
		this.removable = removable;
	}

	/**
	 * Checks if the phase is out of date.
	 * 
	 * @return true if the measure count is not equal to the compliance count and the end date is not null and is before the current time; false otherwise.
	 */
	public boolean isOutToDate() {
		return getMeasureCount() != getComplianceCount() && endDate != null && endDate.getTime() < System.currentTimeMillis();
	}

	/**
	 * Returns the count of implemented measures.
	 *
	 * @return the count of implemented measures
	 */
	public int getImplementedMeasureCount() {
		return implementedMeasureCount;
	}

	/**
	 * Sets the number of implemented measures.
	 *
	 * @param implementedMeasureCount the number of implemented measures
	 */
	public void setImplementedMeasureCount(int implementedMeasureCount) {
		this.implementedMeasureCount = implementedMeasureCount;
	}

	/**
	 * Returns the implemented internal workload for this phase.
	 *
	 * @return the implemented internal workload
	 */
	public double getImplementedInternalWorkload() {
		return implementedInternalWorkload;
	}

	/**
	 * Sets the implemented internal workload for this phase.
	 *
	 * @param implementedInternalWorkload the implemented internal workload to set
	 */
	public void setImplementedInternalWorkload(double implementedInternalWorkload) {
		this.implementedInternalWorkload = implementedInternalWorkload;
	}

	/**
	 * Returns the implemented external workload for this phase.
	 *
	 * @return the implemented external workload
	 */
	public double getImplementedExternalWorkload() {
		return implementedExternalWorkload;
	}

	/**
	 * Sets the implemented external workload for this phase.
	 *
	 * @param implementedExternalWorkload the implemented external workload to set
	 */
	public void setImplementedExternalWorkload(double implementedExternalWorkload) {
		this.implementedExternalWorkload = implementedExternalWorkload;
	}

}