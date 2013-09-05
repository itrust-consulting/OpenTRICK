package lu.itrust.business.TS.cssf;

import lu.itrust.business.TS.Asset;
import lu.itrust.business.TS.Scenario;

/**
 * RiskRegisterItem: <br>
 * This Class represents a single Entry inside the Risk Register. A Item has as fields:<br>
 * <ul>
 * <li>Scenario Object</li>
 * <li>Position in the List</li>
 * <li>Raw: Probability - Impact - Importance</li>
 * <li>Net: Probability - Impact - Importance</li>
 * <li>Expected: Probability - Impact - Importance</li>
 * <li>Strategy</li>
 * </ul>
 * 
 * @author itrust consulting s.� r.l. - BJA, SME, EOM
 * @version 0.1
 * @since 2012-12-11
 */
public class RiskRegisterItem {

	/***********************************************************************************************
	 * Fields
	 **********************************************************************************************/

	/** Regular Expression for Strategy */
	public static final String ACCEPT_SHRINK = "Accept|Shrink";

	/** Scenario Object */
	private Scenario scenario = null;
	
	private Asset asset = null;

	/** Identifier */
	private int id = -1;

	/** Position in the RiskRegister */
	private int position = 0;

	/** The Raw Evaluation Data (Probability, Impact and Importance) */
	private EvaluationResult rawEvaluation = new EvaluationResult(0, 0);

	/** The Net Evaluation Data (Probability, Impact and Importance) */
	private EvaluationResult expectedImportance = new EvaluationResult(0, 0);

	/** The Expected Evaluation Data (Probability, Impact and Importance) */
	private EvaluationResult netEvaluation = new EvaluationResult(0, 0);

	/** Strategy */
	private String strategy = "Shrink";

	/***********************************************************************************************
	 * Constructors
	 **********************************************************************************************/

	/**
	 * Constructors:<br>
	 */
	public RiskRegisterItem() {
	}

	/**
	 * Constructor: <br>
	 * 
	 * @param scenario
	 *            The Scenario of this item
	 */
	public RiskRegisterItem(Scenario scenario, Asset asset) {
		this.setScenario(scenario);
		this.setAsset(asset);
	}

	/***********************************************************************************************
	 * Getters and Setters
	 **********************************************************************************************/

	/**
	 * getId: <br>
	 * Returns the "id" field Value.
	 * 
	 * @return The ID of the Risk Register Item
	 */
	public int getId() {
		return id;
	}

	/**
	 * setId: <br>
	 * Sets the field "id" with a value.
	 * 
	 * @param id
	 *            The Value to set the ID
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * getPosition: <br>
	 * Returns the "position" field Value.
	 * 
	 * @return The Postion inside the List
	 */
	public int getPosition() {
		return position;
	}

	/**
	 * setPosition: <br>
	 * Sets the field "position" with a value.
	 * 
	 * @param position
	 *            The Value to set the Position
	 */
	public void setPosition(int position) {
		this.position = position;
	}

	/**
	 * getScenario: <br>
	 * Returns the "Scenario" field Object.
	 * 
	 * @return The Scenario Object of the Entry
	 */
	public Scenario getScenario() {
		return scenario;
	}

	/**
	 * setScenario: <br>
	 * Sets the field "Scenario" with a Scenario Object.
	 * 
	 * @param scenario
	 *            The Scenario Object to set
	 */
	public void setScenario(Scenario scenario) {
		if (scenario == null)
			throw new IllegalArgumentException(
					"RiskRegisterItem#setScenario: scenario can not be null");
		this.scenario = scenario;
	}

	/**
	 * getRawEvaluation: <br>
	 * Returns the "rawEvaluation" field Object.
	 * 
	 * @return The Raw Evaluation Object (With Probability - Impact - Importance)
	 */
	public EvaluationResult getRawEvaluation() {
		return rawEvaluation;
	}

	/**
	 * setRawEvaluation: <br>
	 * Sets the field "id" with a value.
	 * 
	 * @param rawEvaluation
	 *            The Raw Evaluation Object
	 */
	public void setRawEvaluation(EvaluationResult rawEvaluation) {
		this.rawEvaluation = rawEvaluation;
	}

	/**
	 * getNetEvaluation: <br>
	 * Returns the "netEvaluation" field Object.
	 * 
	 * @return The Net Evaluation Object (With Probability - Impact - Importance)
	 */
	public EvaluationResult getNetEvaluation() {
		return netEvaluation;
	}

	/**
	 * setNetEvaluation: <br>
	 * Sets the field "netEvaluation" with a Object.
	 * 
	 * @param netEvaluation
	 *            The Net Evaluation Object to set
	 */
	public void setNetEvaluation(EvaluationResult netEvaluation) {
		this.netEvaluation = netEvaluation;
	}

	/**
	 * getExpectedImportance: <br>
	 * Returns the "expectedImportance" field Object.
	 * 
	 * @return The Expected Evaluation Object (With Probability - Impact - Importance)
	 */
	public EvaluationResult getExpectedImportance() {
		return expectedImportance;
	}

	/**
	 * setExpectedImportance: <br>
	 * Sets the field "expectedImportance" with a Object.
	 * 
	 * @param expectedImportance
	 *            The Expected Evaluation Object to set
	 */
	public void setExpectedImportance(EvaluationResult expectedImportance) {
		this.expectedImportance = expectedImportance;
	}

	/**
	 * getStrategy: <br>
	 * Returns the "strategy" field Value.
	 * 
	 * @return The Strategy
	 */
	public String getStrategy() {
		return strategy;
	}

	/**
	 * setStrategy: <br>
	 * Sets the field "strategy" with a value.
	 * 
	 * @param strategy
	 *            The Strategy to set
	 */
	public void setStrategy(String strategy) {

		// check if strategy is Shrink or Accepted
		if (strategy == null || !strategy.matches(ACCEPT_SHRINK))
			throw new IllegalArgumentException("RiskRegisterItem#setStrategy " + strategy
				+ " should meet this regular expression," + ACCEPT_SHRINK);
		this.strategy = strategy;
	}

	public Asset getAsset() {
		return asset;
	}

	public void setAsset(Asset asset) {
		this.asset = asset;
	}
}