package lu.itrust.business.ts.model.general.helper;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * Represents a phase form.
 */
public class PhaseForm {
	
	private int id;
	
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date begin;
	
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date end;
	
	private int number;
	
	private boolean beginEnabled;
	
	private boolean endEnabled = true;
	
	public PhaseForm() {
	}

	/**
	 * Returns the ID of the PhaseForm.
	 *
	 * @return the ID of the PhaseForm
	 */
	public int getId() {
		return id;
	}

	/**
	 * Sets the ID of the PhaseForm.
	 *
	 * @param id the ID to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Returns the begin date of the phase.
	 *
	 * @return the begin date of the phase
	 */
	public Date getBegin() {
		return begin;
	}

	/**
	 * Sets the begin date for the phase.
	 *
	 * @param begin the begin date to set
	 */
	public void setBegin(Date begin) {
		this.begin = begin;
	}

	/**
	 * Returns the end date of the phase.
	 *
	 * @return the end date of the phase
	 */
	public Date getEnd() {
		return end;
	}

	/**
	 * Sets the end date of the phase.
	 *
	 * @param end the end date to be set
	 */
	public void setEnd(Date end) {
		this.end = end;
	}

	/**
	 * Returns whether the begin date is enabled for the phase.
	 *
	 * @return true if the begin date is enabled, false otherwise
	 */
	public boolean isBeginEnabled() {
		return beginEnabled;
	}

	/**
	 * Sets whether the begin date is enabled for the phase.
	 *
	 * @param beginEnabled true to enable the begin date, false to disable it
	 */
	public void setBeginEnabled(boolean beginEnabled) {
		this.beginEnabled = beginEnabled;
	}

	/**
	 * Returns whether the end date is enabled for the phase.
	 *
	 * @return true if the end date is enabled, false otherwise
	 */
	public boolean isEndEnabled() {
		return endEnabled;
	}

	/**
	 * Sets whether the end date is enabled for the phase.
	 *
	 * @param endEnabled true to enable the end date, false to disable it
	 */
	public void setEndEnabled(boolean endEnabled) {
		this.endEnabled = endEnabled;
	}

	/**
	 * Returns the number of the phase.
	 *
	 * @return the number of the phase
	 */
	public int getNumber() {
		return number;
	}

	/**
	 * Sets the number of the phase.
	 *
	 * @param number the number to set
	 */
	public void setNumber(int number) {
		this.number = number;
	}
	
}
