package lu.itrust.business.TS.model.cssf.helper;

import lu.itrust.business.TS.model.assessment.Assessment;
import lu.itrust.business.TS.model.parameter.helper.ValueFactory;
import lu.itrust.business.TS.model.parameter.value.IValue;

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
	private IValue reputation;

	/** Operational Impact can be a double or a string */
	private IValue operational;

	/** Legal Impact can be a double or a string */
	private IValue legal;

	/** Financial Impact can be a double or a string */
	private IValue financial;

	/** Acronym regular expression: c([0-9]|10) */
	final static String ACRONYM_REGEX = "^c([0-9]|10)$";

	/** The Analysis Parameters Array */
	private ValueFactory factory;

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
	public Impact(double reputation, double operational, double legal, double financial, ValueFactory factory) {
		setFactory(factory);// must be first
		this.setReputation(factory.findImpactRep(reputation));
		this.setOperational(factory.findImpactOp(operational));
		this.setLegal(factory.findImpactLeg(legal));
		this.setFinancial(factory.findImpactFin(financial));
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
	public Impact(String reputation, String operational, String legal, String financial, ValueFactory factory) {
		setFactory(factory);
		this.setReputation(factory.findImpactRep(reputation));
		this.setOperational(factory.findImpactOp(operational));
		this.setLegal(factory.findImpactLeg(legal));
		this.setFinancial(factory.findImpactFin(financial));
	}

	public Impact(Assessment assessment, ValueFactory factory) {
		this(assessment.getImpactRep(), assessment.getImpactOp(), assessment.getImpactLeg(), assessment.getImpactFin(), factory);
	}

	/**
	 * getReal: <br>
	 * Returns the Real Maximum Impact value. This value will be auto computed
	 * if one of impacts has changed (The "change" field is true).
	 * 
	 * @return The Real Impact Value (the Maximum of the Categories)
	 */
	public double getReal() {
		return findMaxByLevel();
	}

	private double findMaxByLevel() {
		return IValue.maxByLevel(financial, IValue.maxByLevel(operational, IValue.maxByLevel(legal, reputation))).getReal();
	}

	/**
	 * getOperational: <br>
	 * Returns the String or Double represenation of the Operational Impact.
	 * 
	 * @return The operational Object
	 */
	public IValue getOperational() {
		return operational;
	}

	/**
	 * setOperational:<br>
	 * Set the Operational Impact with a double value and switch "change" to
	 * true.
	 * 
	 * @param operational
	 *            The Real Operational Value
	 */
	public void setOperational(IValue operational) {
		this.operational = operational;
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
		this.operational = factory.findImpactOp(operational);
	}

	/**
	 * setReputation: <br>
	 * Set the Reputation Impact with a double value and switch "change" to
	 * true.
	 * 
	 * @param reputation
	 *            The Real Reputation value
	 */
	public void setReputation(IValue reputation) {
		this.reputation = reputation;
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
		this.reputation = factory.findImpactRep(reputation);
	}

	/**
	 * getLegal: <br>
	 * Returns the String or Double represenation of the Legal Impact.
	 * 
	 * @return The Legal value
	 */
	public IValue getLegal() {
		return legal;
	}

	/**
	 * setLegal:<br>
	 * Set the Legal Impact with a double value and switch "change" to true.
	 * 
	 * @param iValue
	 *            The Real Legal value to set
	 */
	public void setLegal(IValue iValue) {
		this.legal = iValue;
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
		this.legal = factory.findImpactLeg(legal);
	}

	/**
	 * getFinancial: <br>
	 * Returns the String or Double represenation of the Financial Impact.
	 * 
	 * @return The Financial Impact
	 */
	public IValue getFinancial() {
		return financial;
	}

	/**
	 * setFinancial:<br>
	 * Set the Financial Impact with a double value and switch "change" to true.
	 * 
	 * @param iValue
	 *            (euros)
	 */
	public void setFinancial(IValue iValue) {
		this.financial = iValue;
	}

	/**
	 * setFinancial: <br>
	 * update financial and change switch to true.
	 * 
	 * @param financial
	 */
	public void setFinancial(String financial) {
		// check if parameter is a valid acronym -> TRUE: take SimpleParameter; FALSE:
		// try to convert to
		// Real Value
		this.financial = factory.findImpactFin(financial);
	}
	
	/**
	 * @return the reputation
	 */
	public IValue getReputation() {
		return reputation;
	}

	/**
	 * @return the factory
	 */
	public ValueFactory getFactory() {
		return factory;
	}

	/**
	 * @param factory
	 *            the factory to set
	 */
	public void setFactory(ValueFactory factory) {
		this.factory = factory;
	}

	/**
	 * toString:<br>
	 * Overrides the toStirng method to display All Impact Category Values.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Impact [reputation=" + reputation + ", operational=" + operational + ", legal=" + legal + ", financial=" + financial + ", real=" + getReal() + "]";
	}
}