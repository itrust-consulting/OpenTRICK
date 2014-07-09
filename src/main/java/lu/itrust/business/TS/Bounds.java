package lu.itrust.business.TS;

/**
 * Bounds: <br>
 * This class represents the bound of an extended parameter. Values from and to are represented.
 * 
 * @author itrust consulting s.� r.l. - SME,BJA,EOM
 * @version 0.1
 * @since 2012-12-17
 */
public class Bounds implements Cloneable{

	/***********************************************************************************************
	 * Fields
	 **********************************************************************************************/

	/** Value From */
	private double from = 0;

	/** Value To */
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
	 * Updates the Parameter From and To values using a Bounds object as previous To value (which
	 * serves in the current Parameter as From value). If the prevBounds parameter is null, the
	 * previous value will be used as 0 (there are no previous values).
	 * 
	 * @param prevBounds
	 *            The previous Parameter Bounds
	 * @param value
	 *            The current Parameter Value
	 * @param nextValue
	 *            The next Parameter Value
	 */
	public void updateBounds(Bounds prevBounds, double value, double nextValue) {
		if (prevBounds == null) {
			updateBounds(0, value, nextValue);
		} else {
			updateBounds(prevBounds.to, value, nextValue);
		}
	}

	/**
	 * updateBounds: <br>
	 * Updates the Parameter From and To values using a Bounds object as previous To value (which
	 * serves in the current Parameter as From value).
	 * 
	 * @param prevTo
	 *            The previous Parameter To value
	 * @param value
	 *            The current Parameter Value
	 * @param nextValue
	 *            The next Parameter Value
	 */
	public void updateBounds(double prevTo, double value, double nextValue) {
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
	 */
	protected void setFrom(double from) {
		if (from < 0)
			throw new IllegalArgumentException("Bounds#setFrom: from (" + from
				+ ") should be 0 or greater");
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
	 */
	protected void setTo(double to) {
		if (this.from > to) {
			throw new IllegalArgumentException("Bounds#check: from(" + from
				+ ") should be greater to To(" + to + ")");
		}
		this.to = to;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}