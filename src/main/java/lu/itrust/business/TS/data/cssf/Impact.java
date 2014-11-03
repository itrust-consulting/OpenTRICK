package lu.itrust.business.TS.data.cssf;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lu.itrust.business.TS.data.basic.ExtendedParameter;
import lu.itrust.business.TS.data.basic.Parameter;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.tsconstant.Constant;

/**
 * 
 * Impact: <br>
 * This class has as main aim to make easier Impact handling.<br>
 * Impact has the following Impact Categories:
 * <ul>
 * <li>A Reputation Impact Value (either Acronym or Real Value)</li>
 * <li>An Operational Impact Value (either Acronym or Real Value)</li>
 * <li>An Legal Impact Value (either Acronym or Real Value)</li>
 * <li>An Financial Impact Value (either Acronym or Real Value)</li>
 * </ul>
 * 
 * Features:
 * <ul>
 * <li>Auto evaluates the Maximum Impact of the Impact Categories (stored inside
 * "real" field)</li>
 * <li>Returns the Real Impact Category value (from impact fields)</li>
 * <li>Return Impact Acronym using a Real Impact Value as input (if such an
 * acronym exists)</li>
 * </ul>
 * 
 * @author itrust consulting s.�.rl. : BJA, EOM, SME
 * @version 0.1
 * @since 11 d�c. 2012
 */
public class Impact {

	/***********************************************************************************************
	 * Fields
	 **********************************************************************************************/

	/** Reputation Impact, can be a double or a string */
	private Object reputation = 0;

	/** Operational Impact can be a double or a string */
	private Object operational = 0;

	/** Legal Impact can be a double or a string */
	private Object legal = 0;

	/** Financial Impact can be a double or a string */
	private Object financial = 0;

	/** Real Impact, the Maximum of Impact Categories */
	private double real = 0;

	/** Real impact recalculate indicator */
	private boolean change = true;

	/** Acronym regular expression: c([0-9]|10) */
	final static String ACRONYM_REGEX = "^c([0-9]|10)$";

	/** The Analysis Parameters Array */
	private Map<String, Parameter> parameters;

	/***********************************************************************************************
	 * Constructors
	 **********************************************************************************************/

	/**
	 * Constructor:<br>
	 * Takes real (double) values for the different impact categories.
	 * 
	 * @param reputation
	 *            The Reputation Impact Category value
	 * @param operational
	 *            The Operational Impact Category value
	 * @param legal
	 *            The Legal Impact Category value
	 * @param financial
	 *            The Financial Impact Category value
	 * @param parameters
	 *            The Analysis Parameters Array
	 */
	public Impact(double reputation, double operational, double legal, double financial, Map<String, Parameter> parameters) {
		this.setParameters(parameters);//must be first
		this.setReputation(reputation);
		this.setOperational(operational);
		this.setLegal(legal);
		this.setFinancial(financial);
	}

	/**
	 * Constructor:<br>
	 * Takes Text values (String) for the different impact categories.
	 * 
	 * @param reputation
	 *            The Reputation Impact Category as String
	 * @param operational
	 *            The Operational Impact Category as String
	 * @param legal
	 *            The Legal Impact Category as String
	 * @param financial
	 *            The Financial Impact Category as String
	 * @param parameters
	 *            The Analysis Parameters Array
	 */
	public Impact(String reputation, String operational, String legal, String financial, Map<String, Parameter> parameters) {
		this.setParameters(parameters);//must be first
		this.setReputation(reputation);
		this.setOperational(operational);
		this.setLegal(legal);
		this.setFinancial(financial);
	}

	public void setParameters(Map<String, Parameter> parameters2) {
		this.parameters = parameters2;
	}

	/***********************************************************************************************
	 * Getters and Setters
	 **********************************************************************************************/
	/**
	 * setParameters: <br>
	 * Sets the "parameters" field with a value
	 * 
	 * @param parameters
	 *            The value to set the parameters
	 */
	public void setParameters(List<Parameter> parameters) {
		if (this.parameters != null)
			this.parameters.clear();
		this.parameters = new LinkedHashMap<String, Parameter>();
		for (Parameter parameter : parameters)
			if ((parameter instanceof ExtendedParameter) && parameter.getType().getLabel().equals(Constant.PARAMETERTYPE_TYPE_IMPACT_NAME))
				this.parameters.put(((ExtendedParameter) parameter).getAcronym(), parameter);
	}

	/**
	 * getReal: <br>
	 * Returns the Real Maximum Impact value. This value will be auto computed
	 * if one of impacts has changed (The "change" field is true).
	 * 
	 * @return The Real Impact Value (the Maximum of the Categories)
	 */
	public double getReal() {

		// check if real value has to be recalculated -> YES
		if (change)
			updateRealValue();
		return real;
	}

	/**
	 * updateRealValue: <br>
	 * Take the maximum of the 4 Impact Categories and sets the "real" field
	 * value.
	 */
	protected void updateRealValue() {

		// take the maximum and call setReal method
		setReal(Math.max(getRealFinancial(), Math.max(getRealLegal(), Math.max(getRealOperational(), getRealReputation()))));
	}

	/**
	 * setReal: <br>
	 * Sets the Real Impact value and changes the "change" indicator to false.
	 * 
	 * @param max
	 *            The Maximum Impact value
	 */
	private synchronized void setReal(double max) {
		this.real = max;
		change = false;
	}

	/**
	 * getOperational: <br>
	 * Returns the String or Double represenation of the Operational Impact.
	 * 
	 * @return The operational Object
	 */
	public Object getOperational() {
		return operational;
	}

	/**
	 * getAcronymOperational: <br>
	 * if raw value is a string, it will be returned, otherwise it will try to
	 * convert numeric value to acronym.
	 * 
	 * @return
	 * @throws TrickException 
	 */
	public String getAcronymOperational() throws TrickException {

		// checks if the operational object is of type String -> YES
		if (operational instanceof String) {

			// return the Acronym
			return (String) operational;
		} else {

			// transform the operational Real Value into a valid Acronym
			return convertDoubleImpactToAcronym((Double) operational, parameters);
		}
	}

	/**
	 * getRealOperational: <br>
	 * Returns the Real Operational Value. When the field "operational" is a
	 * String type, the value will be converted to Double.
	 * 
	 * @return The Real Operational Impact value
	 */
	public double getRealOperational() {

		// check if operational is a Double Object -> YES
		if (operational instanceof Double)

			// return the Double object
			return (Double) operational;

		// operational is a not a Double Object, convert the String to a Double
		// Object
		return convertStringImpactToDouble((String) operational, parameters);
	}

	/**
	 * setOperational:<br>
	 * Set the Operational Impact with a double value and switch "change" to
	 * true.
	 * 
	 * @param operational
	 *            The Real Operational Value
	 */
	public void setOperational(double operational) {
		this.operational = operational;
		this.change = true;
	}

	/**
	 * setOperational: <br>
	 * Set the Operational Impact Value with a String value and switch "change"
	 * to true. When The given operational value is not a valid Acronym, the
	 * value will be converted to Double.
	 * 
	 * @param operational
	 *            The String represenation of the Operational Impact
	 */
	public void setOperational(String operational) {

		// checks if the parameter is a valid acronym -> TRUE: take this value;
		// FALSE: try to
		// convert to Real
		this.operational = isAcronym(operational) ? operational : convertStringImpactToDouble(operational, parameters);
		this.change = true;
	}

	/**
	 * getAcronymReputation: <br>
	 * Returns the Acronym of the Reputation Impact. If the value is a Double
	 * value, it will be converted into a valid Acronym.
	 * 
	 * @return The Reputation Impact Acronym
	 * @throws TrickException 
	 */
	public String getAcronymReputation() throws TrickException {

		// check if reputation is a String -> YES
		if (reputation instanceof String) {

			// return acronym
			return (String) reputation;
		} else {

			// convert real value to acronym
			return convertDoubleImpactToAcronym((Double) reputation, parameters);
		}
	}

	/**
	 * getRealReputation:<br>
	 * Returns the Real Reputation Value. When the field "reputation" is a
	 * String type, the value will be converted to Double.
	 * 
	 * @return The Real Reputation Impact value
	 */
	public double getRealReputation() {

		// check if reputation is a real value -> YES
		if (reputation instanceof Double) {

			// return value
			return (Double) reputation;

		} else {
			// try to convert to acronym
			return convertStringImpactToDouble((String) reputation, parameters);
		}
	}

	/**
	 * setReputation: <br>
	 * Set the Reputation Impact with a double value and switch "change" to
	 * true.
	 * 
	 * @param reputation
	 *            The Real Reputation value
	 */
	public void setReputation(double reputation) {
		this.reputation = reputation;
		this.change = true;
	}

	/**
	 * setReputation: <br>
	 * Set the Reputation Impact Value with a String value and switch "change"
	 * to true. When The given reputation value is not a valid Acronym, the
	 * value will be converted to Double.
	 * 
	 * @param reputation
	 *            The String representation of the Reputation Impact
	 */
	public void setReputation(String reputation) {
		this.reputation = isAcronym(reputation) ? reputation : convertStringImpactToDouble(reputation, parameters);
		this.change = true;
	}

	/**
	 * getLegal: <br>
	 * Returns the String or Double represenation of the Legal Impact.
	 * 
	 * @return The Legal value
	 */
	public Object getLegal() {
		return legal;
	}

	/**
	 * getAcronymLegal: <br>
	 * Returns the Acronym of the Legal Impact. If the value is a Double value,
	 * it will be converted into a valid Acronym.
	 * 
	 * @return The Legal Impact Acronym
	 * @throws TrickException 
	 */
	public String getAcronymLegal() throws TrickException {

		// check if legal is an Acronym -> YES
		if (legal instanceof String) {

			// return acronym
			return (String) legal;
		} else {

			// try to convert from real value to acronym
			return convertDoubleImpactToAcronym((Double) legal, parameters);
		}
	}

	/**
	 * getRealLegal:<br>
	 * Returns the Real Legal Value. When the field "reputation" is a String
	 * type, the value will be converted to Double.
	 * 
	 * @return The Legal Impact value
	 */
	public double getRealLegal() {

		// check if legal is a real value
		if (legal instanceof Double) {

			// return value
			return (Double) legal;
		} else {

			// try to convert to real value
			return convertStringImpactToDouble((String) legal, parameters);
		}
	}

	/**
	 * setLegal:<br>
	 * Set the Legal Impact with a double value and switch "change" to true.
	 * 
	 * @param legal
	 *            The Real Legal value to set
	 */
	public void setLegal(double legal) {
		this.legal = legal;
		this.change = true;
	}

	/**
	 * setLegal: <br>
	 * Set the Legal Impact Value with a String value and switch "change" to
	 * true. When The given Legal value is not a valid Acronym, the value will
	 * be converted to Double.
	 * 
	 * @param legal
	 *            The String representation of the Legal Impact value
	 */
	public void setLegal(String legal) {

		// check if parameter is a valid acronym -> TRUE: take parameter; FALSE:
		// try to convert to
		// Real
		this.legal = isAcronym(legal) ? legal : convertStringImpactToDouble(legal, parameters);
		this.change = true;
	}

	/**
	 * getFinancial: <br>
	 * Returns the String or Double represenation of the Financial Impact.
	 * 
	 * @return The Financial Impact
	 */
	public Object getFinancial() {
		return financial;
	}

	/**
	 * getAcronymFinancial: <br>
	 * Returns the Acronym of the Financial Impact. If the value is a Double
	 * value, it will be converted into a valid Acronym.
	 * 
	 * @return The Financial Impact Acronym
	 * @throws TrickException 
	 */
	public String getAcronymFinancial() throws TrickException {

		// check if financial is a Acronym -> YES
		if (financial instanceof String) {

			// return Acronym
			return (String) financial;
		} else {

			// try to convert real value to Acronym
			return convertDoubleImpactToAcronym((Double) financial, parameters);
		}
	}

	/**
	 * getRealFinancial: <br>
	 * Returns the Real Financial Value. When the field "financial" is a String
	 * type, the value will be converted to Double.
	 * 
	 * @return The Real Financial Value
	 */
	public double getRealFinancial() {

		// check if financial is a Real Value -> YES
		if (financial instanceof Double) {

			// return value
			return (Double) financial;
		} else {

			// try to convert to Real value
			return convertStringImpactToDouble((String) financial, parameters);
		}
	}

	/**
	 * setFinancial:<br>
	 * Set the Financial Impact with a double value and switch "change" to true.
	 * 
	 * @param financial
	 *            (euros)
	 */
	public void setFinancial(double financial) {
		this.financial = financial;
		this.change = true;
	}

	/**
	 * setFinancial: <br>
	 * update financial and change switch to true.
	 * 
	 * @param financial
	 */
	public void setFinancial(String financial) {

		// check if parameter is a valid acronym -> TRUE: take Parameter; FALSE:
		// try to convert to
		// Real Value
		this.financial = isAcronym(financial) ? financial : convertStringImpactToDouble(financial, parameters);
		this.change = true;
	}

	/**
	 * isAcronym: <br>
	 * Check if the given Acronym value is a valid Acronym.
	 * 
	 * @param acronym
	 *            The Acronym to check
	 * @return True if value meets the {@link #ACRONYM_REGEX regular expression}
	 *         ; False when not
	 */
	public boolean isAcronym(String acronym) {

		// check if null and Regular Expression
		return acronym == null || parameters == null ? false : parameters.containsKey(acronym);
	}

	/**
	 * convertImpactToDouble: <br>
	 * Takes a string value (value from SQLite file) and converts it into a
	 * valid double value.
	 * 
	 * @param impact
	 *            The impact value as string
	 * @return A valid Double value (k euro)
	 */
	public static double convertStringImpactToDouble(String impact, Map<String, Parameter> parameters) {

		double value = 0;
		// ****************************************************************
		// * check value is a valid Acronym -> YES
		// ****************************************************************
		if (parameters.containsKey(impact))
			value = parameters.get(impact).getValue();
		else {
			// ****************************************************************
			// * check if value can be transformed into double
			// ****************************************************************
			try {
				// return the value converted into double value
				value = Double.parseDouble(impact);
			} catch (NumberFormatException e) {
			}
		}
		return value;
	}

	/**
	 * convertDoubleImpactToAcronym: <br>
	 * Returns The Acronym of a given Double Impact Value. The Acronym will be
	 * taken form the Analysis Parameter Array.
	 * 
	 * @param impact
	 *            The Real Impact Value to get the Acronym
	 * @param parameters
	 *            The Parameters Array to find the Acronym
	 * @return The Acronym of the Impact Value
	 * @throws TrickException 
	 */
	public static String convertDoubleImpactToAcronym(double impact, Map<String, Parameter> parameters) throws TrickException {

		// check if impact < 0 -> YES
		if (impact < 0) 
			throw new TrickException("error.impact.impact","Impact should be a natural numbers");

		// parse parameters to find the matching impact
		for (Parameter parameter : parameters.values()) {

			// check if parameter is a ExtendedParameter (for impact and
			// probability parameters) +
			// check if type of parameter is impact +
			// check if impact value is in the parameter bounds
			if ((parameter instanceof ExtendedParameter) && (parameter.getType().getLabel().equals(Constant.PARAMETERTYPE_TYPE_IMPACT_NAME))
					&& ((ExtendedParameter) parameter).getBounds().isInRange(impact)) {

				// returns the Acronym
				return ((ExtendedParameter) parameter).getAcronym();
			}
		}
		throw new TrickException("error.impact.impact.acronym_not_found","Acronym cannot be found");
	}

	/**
	 * toString:<br>
	 * Overrides the toStirng method to display All Impact Category Values.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Impact [reputation=" + reputation + ", operational=" + operational + ", legal=" + legal + ", financial=" + financial + ", real=" + real + "]";
	}
}