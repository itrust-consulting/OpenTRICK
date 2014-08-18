package lu.itrust.business.TS;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import lu.itrust.business.exception.TrickException;

/**
 * Parameter: <br>
 * This class represents a Parameter and its data.
 * 
 * @author itrust consulting s.à r.l. - BJA,SME
 * @version 0.1
 * @since 2012-08-21
 */
@Entity 
public class Phase implements Serializable, Cloneable {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** serialVersionUID */
	@Transient
	private static final long serialVersionUID = 1L;

	/** phase identifier, unsaved value = -1 */
	@Id @GeneratedValue 
	@Column(name="idPhase")
	private int id = -1;

	/** Analysis Object */
	@ManyToOne 
	@JoinColumn(name="fiAnalysis")
	private Analysis analysis = null;

	/** The Phase Number */
	@Column(name="dtNumber")
	private int number;

	/** The Begin Date of the Phase */
	@Column(name="dtBeginDate")
	private Date beginDate;

	/** The End Date of the Phase */
	@Column(name="dtEndDate")
	private Date endDate;

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
	 * @param number
	 *            The Phase Number
	 */
	public Phase(int number) {
		this.id = -1;
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
	 * @param number
	 *            The value to set the Phase Number
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
	 * @param beginDate
	 *            The value to set the Begin Date of the Phase
	 * @throws TrickException
	 */
	public void setBeginDate(Date beginDate) throws TrickException {
		if ((beginDate != null) && (this.endDate != null) && (!beginDate.before(endDate)))
			throw new TrickException("error.phase.begin_date.invalid", "Begin date cannot be empty or later than end date");
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
	 * @param endDate
	 *            The value to set the End Date of the Phase
	 * @throws TrickException
	 */
	public void setEndDate(Date endDate) throws TrickException {
		if ((endDate != null) && (this.beginDate != null) && (!endDate.after(this.beginDate)))
			throw new TrickException("error.phase.end_date.invalid", "End date cannot be empty or earlier than begin date");
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
	 * @param analysis
	 *            The Value to set the analysis field
	 */
	public void setAnalysis(Analysis analysis) {
		this.analysis = analysis;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((analysis == null) ? 0 : analysis.hashCode());
		result = prime * result + number;
		return result;
	}

	/*
	 * (non-Javadoc)
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
		if (analysis == null) {
			if (other.analysis != null)
				return false;
		} else if (!getAnalysis().equals(other.getAnalysis()))
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
	 * @param id
	 *            The Value to set the id field
	 */
	public void setId(int id) {
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Phase clone() throws CloneNotSupportedException {
		return (Phase) super.clone();
	}

	public Phase duplicate() throws CloneNotSupportedException {
		Phase phase = (Phase) super.clone();
		phase.id = -1;
		return phase;
	}

}