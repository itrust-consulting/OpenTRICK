package lu.itrust.business.TS.cssf;

/**
 * EvaluationResult: <br>
 * Represents the Evaluation of a Risk Importance. The Risk Importance can be calculated automaticly
 * when impact and probability values are set (Constructor). Or can be given as Constructor
 * parameter.<br>
 * This class contains:
 * <ul>
 * <li>Probability</li>
 * <li>Impact</li>
 * <li>Importance</li>
 * </ul>
 * 
 * @author itrust consulting s.à.rl. - BJA, SME, EOM
 * @version 0.1
 * @since 2012-12-11
 */
public class EvaluationResult {

	/***********************************************************************************************
	 * Fields
	 **********************************************************************************************/

	/** The Probability */
	private double probability = 0;

	/** The Impact */
	private double impact = 0;

	/** The Importance */
	private double importance = 0;

	/***********************************************************************************************
	 * Constructors
	 **********************************************************************************************/

	/**
	 * Constructor:<br>
	 */
	public EvaluationResult() {
	}

	/**
	 * Constructor:<br>
	 * Sets the Probability and Impact values and calculate the Importance Automaticly.
	 * 
	 * @param probability
	 *            The Probability Value
	 * @param impact
	 *            The Impact Value
	 */
	public EvaluationResult(double probability, double impact) {
		this.setProbability(probability);
		this.setImpact(impact);
	}

	/**
	 * Constructor:<br>
	 * Sets the Probability, Impact and Importance Values.
	 * 
	 * @param probability
	 *            The Probability Value
	 * @param impact
	 *            The Impact Value
	 * @param importance
	 *            The Importance Value
	 */
	public EvaluationResult(double probability, double impact, double importance) {
		this.setProbability(probability);
		this.setImpact(impact);
		this.setImportance(importance);
	}

	/***********************************************************************************************
	 * Methods
	 **********************************************************************************************/

	/**
	 * computeImportance: <br>
	 * Calculates the Importance using the Impact and Probability Value.<br>
	 * Importance = Impact * Probability
	 */
	protected void computeImportance() {
		importance = impact * probability;
	}

	/***********************************************************************************************
	 * Getters and Setters
	 **********************************************************************************************/

	/**
	 * getProbability: <br>
	 * Returns the "probability" Field Value.
	 * 
	 * @return The Probability Value
	 */
	public double getProbability() {
		return probability;
	}

	/**
	 * setProbability: <br>
	 * Sets the "probability" Field with a Value.
	 * 
	 * @param probability
	 *            The value to set the Probability
	 */
	public void setProbability(double probability) {
		if (probability < 0)
			throw new IllegalArgumentException("EvaluationResult#setProbability: " + probability
				+ " should be a natural numbers");
		this.probability = probability;
		computeImportance();
	}

	/**
	 * getImpact: <br>
	 * Returns the "impact" Field Value.
	 * 
	 * @return The Impact Value
	 */
	public double getImpact() {
		return impact;
	}

	/**
	 * setImpact: <br>
	 * Sets the "impact" Field with a value.
	 * 
	 * @param impact
	 *            The Impact Value to set
	 */
	public void setImpact(double impact) {
		if (impact < 0)
			throw new IllegalArgumentException("EvaluationResult#setImpact: " + impact
				+ " should be a natural numbers");
		this.impact = impact;
		computeImportance();
	}

	/**
	 * getImportance: <br>
	 * Return the "importance" Field Value.
	 * 
	 * @return The Importance Value
	 */
	public double getImportance() {
		return importance;
	}

	/**
	 * setImportance: <br>
	 * Sets the "importance" field with a value
	 * 
	 * @param importance
	 *            The value to set the Importance
	 */
	protected void setImportance(double importance) {
		this.importance = importance;
	}
}