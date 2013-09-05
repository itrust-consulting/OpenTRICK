package lu.itrust.business.TS.actionplan;

import lu.itrust.business.TS.Assessment;

/**
 * ActionPlanAsset: <br>
 * Represents a Asset with its ALE value inside a specific Action Plan Entry.
 * 
 * @author itrust consulting s.�.rl. : EOM, BJA, SME
 * @version 0.1
 * @since 29 janv. 2013
 */
public class ActionPlanAssessment {

	/***********************************************************************************************
	 * Fields Declaration
	 **********************************************************************************************/

	/** The ActionPlanAsset id */
	private int id = -1;

	/** The ActionPlanEntry Object */
	private ActionPlanEntry actionPlanEntry = null;

	/** The Assessment Object */
	private Assessment assessment = null;
	
	/** The ALE value */
	private double currentALE = 0;

	/***********************************************************************************************
	 * Constructors
	 **********************************************************************************************/

	/**
	 * Constructor:<br>
	 */
	public ActionPlanAssessment() {
	}

	/**
	 * Constructor:<br>
	 * 
	 * @param actionPlanEntry
	 *            The Action Plan Entry Object
	 * @param assessment
	 *            The assessment Object
	 * @param currentALE
	 *            The ALE value
	 */
	public ActionPlanAssessment(ActionPlanEntry actionPlanEntry, Assessment assessment, double currentALE) {
		this.actionPlanEntry = actionPlanEntry;
		this.assessment = assessment;
		this.currentALE = currentALE;
	}

	/***********************************************************************************************
	 * Getters and Setters
	 **********************************************************************************************/

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

	/**
	 * getActionPlanEntry: <br>
	 * Returns the actionPlanEntry field value.
	 * 
	 * @return The value of the actionPlanEntry field
	 */
	public ActionPlanEntry getActionPlanEntry() {
		return actionPlanEntry;
	}

	/**
	 * setActionPlanEntry: <br>
	 * Sets the Field "actionPlanEntry" with a value.
	 * 
	 * @param actionPlanEntry
	 *            The Value to set the actionPlanEntry field
	 */
	public void setActionPlanEntry(ActionPlanEntry actionPlanEntry) {
		this.actionPlanEntry = actionPlanEntry;
	}

	/**
	 * getCurrentALE: <br>
	 * Returns the currentALE field value.
	 * 
	 * @return The value of the currentALE field
	 */
	public double getCurrentALE() {
		return currentALE;
	}

	/**
	 * setCurrentALE: <br>
	 * Sets the Field "currentALE" with a value.
	 * 
	 * @param currentALE
	 *            The Value to set the currentALE field
	 */
	public void setCurrentALE(double currentALE) {
		this.currentALE = currentALE;
	}

	/**
	 * getAssessment: <br>
	 * Returns the assessment field value.
	 * 
	 * @return The value of the assessment field
	 */
	public Assessment getAssessment() {
		return assessment;
	}

	/**
	 * setAssessment: <br>
	 * Sets the Field "assessment" with a value.
	 * 
	 * @param asset
	 *            The Value to set the assessment field
	 */
	public void setAssessment(Assessment assessment) {
		this.assessment = assessment;
	}
}