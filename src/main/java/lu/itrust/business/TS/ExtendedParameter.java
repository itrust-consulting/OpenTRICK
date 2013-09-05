package lu.itrust.business.TS;

/**
 * ExtendedParameter: <br>
 * This class represents an Extended Parameter and all its data.
 * 
 * The Class extends Parameter which has basic parameter fields.
 * 
 * This class is used to store Extended Parameter. Extended parameters are: <br>
 * <ul>
 * <li>Impact values</li>
 * <li>Likelihood values</li>
 * </ul>
 * 
 * @author itrust consulting s.à r.l. - SME,BJA
 * @version 0.1
 * @since 2012-08-21
 */
public class ExtendedParameter extends Parameter {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;

	/** The Extended Parameter Level (default: 0-5 or 0-6 -> NOT restricted) */
	private int level = 0;

	/** The Extended Parameter Acronym */
	private String acronym = "";

	/** Extended Parameter From And To values */
	private Bounds bounds = null;

	/***********************************************************************************************
	 * Getters and Setters
	 **********************************************************************************************/

	/**
	 * getLevel: <br>
	 * Returns the "level" field value
	 * 
	 * @return The Level of the Extended Parameter
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * setLevel: <br>
	 * Sets the "level" field with a value
	 * 
	 * @param level
	 *            The value to set the Level
	 */
	public void setLevel(int level) {
		if ((level < 0) || (level > 10)) {
			throw new IllegalArgumentException(
					"Extended Parameter Level needs to be between 0 and 10 included!");
		}
		this.level = level;
	}

	/**
	 * getAcronym: <br>
	 * Returns the "acronym" field value
	 * 
	 * @return The Acronym
	 */
	public String getAcronym() {
		return acronym;
	}

	/**
	 * setAcronym: <br>
	 * Sets the "acronym" field with a value
	 * 
	 * @param acronym
	 *            The value to set the Acronym
	 */
	public void setAcronym(String acronym) {
		this.acronym = acronym;
	}

	/**
	 * getBounds: <br>
	 * Returns the "bounds" field value
	 * 
	 * @return The Bounds values (from and to values)
	 */
	public Bounds getBounds() {
		return bounds;
	}

	/**
	 * setBounds: <br>
	 * Sets the "bounds" field with a value
	 * 
	 * @param bounds
	 *            The value to set the Bound values (from and to values)
	 */
	public void setBounds(Bounds bounds) {
		this.bounds = bounds;
	}
}