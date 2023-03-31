package lu.itrust.business.ts.model.parameter.helper;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import lu.itrust.business.ts.exception.TrickException;

/**
 * Bounds: <br>
 * This class represents the bound of an extended parameter. Values from and to are represented.
 * 
 * @author itrust consulting s.� r.l. - SME,BJA,EOM
 * @version 0.1
 * @since 2012-12-17
 */
@Embeddable
public class Bounds implements Cloneable {

	/***********************************************************************************************
	 * Fields
	 **********************************************************************************************/

	/** Value From */
	@Column(name = "dtFrom", nullable = false)
	private double from = 0;

	/** Value To */
	@Column(name = "dtTo", nullable = false)
	private double to = 0;

	/***********************************************************************************************
	 * Constructors
	 **********************************************************************************************/

	/**
	 * Constructor:<br>
	 */
	public Bounds() {
	}

	/**
	 * Constructor: <br>
	 * Takes value from and to as parameters to set the fields.
	 * 
	 * @param from
	 *            The From value
	 * @param to
	 *            The To value
	 */
	public Bounds(double from, double to) {
		this.from = from;
		this.to = to;
	}

	/***********************************************************************************************
	 * Methods
	 **********************************************************************************************/

	/**
	 * updateBounds: <br>
	 * Updates the SimpleParameter From and To values using a Bounds object as previous To value (which
	 * serves in the current SimpleParameter as From value). If the prevBounds parameter is null, the
	 * previous value will be used as 0 (there are no previous values).
	 * 
	 * @param prevBounds
	 *            The previous SimpleParameter Bounds
	 * @param value
	 *            The current SimpleParameter Value
	 * @param nextValue
	 *            The next SimpleParameter Value
	 * @throws TrickException
	 */
	public void updateBounds(Bounds prevBounds, double value, double nextValue) throws TrickException {
		if (prevBounds == null) {
			updateBounds(0, value, nextValue);
		} else {
			updateBounds(prevBounds.to, value, nextValue);
		}
	}

	/**
	 * updateBounds: <br>
	 * Updates the SimpleParameter From and To values using a Bounds object as previous To value (which
	 * serves in the current SimpleParameter as From value).
	 * 
	 * @param prevTo
	 *            The previous SimpleParameter To value
	 * @param value
	 *            The current SimpleParameter Value
	 * @param nextValue
	 *            The next SimpleParameter Value
	 * @throws TrickException
	 */
	public void updateBounds(double prevTo, double value, double nextValue) throws TrickException {
		setFrom(prevTo);
		setTo(Math.sqrt(value * nextValue));
	}

	/**
	 * isInRange: <br>
	 * Check if a givben value is between "from" value and "to" value.
	 * 
	 * @param value
	 *            The value to perform the check
	 * 
	 * @return True if value is between "from" and "to"; False when not
	 */
	public boolean isInRange(double value) {
		return from <= value && value < to;
	}

	/***********************************************************************************************
	 * Getters and Setters
	 **********************************************************************************************/

	/**
	 * getFrom:<br>
	 * Returns the "from" field value.
	 * 
	 * @return The From value
	 */
	public double getFrom() {
		return from;
	}

	/**
	 * setFrom: <br>
	 * Sets the "From" field value.
	 * 
	 * @param from
	 * @throws TrickException
	 */
	protected void setFrom(double from) throws TrickException {
		if (from < 0)
			throw new TrickException("error.extended_parameter.bounds.from", from + " should be 0 or greater", Double.toString(from));
		this.from = from;
	}

	/**
	 * getTo:<br>
	 * Returns the "to" field value.
	 * 
	 * @return The To value
	 */
	public double getTo() {
		return to;
	}

	/**
	 * setTo: <br>
	 * Sets the "to" field value.
	 * 
	 * @param to
	 *            The value to set the "to" field
	 * @throws TrickException
	 */
	protected void setTo(double to) throws TrickException {
		if (this.from > to)
			throw new TrickException("error.extended_parameter.bounds.to", String.format("%d should be %d or greater", to, from), String.valueOf(to), String.valueOf(from));
		this.to = to;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Bounds clone(){
		try {
			return (Bounds) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new TrickException("error.clone.bounds", "Bounds cannot be copied", e);
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Bounds [from=" + from + ", to=" + to + "]";
	}
}