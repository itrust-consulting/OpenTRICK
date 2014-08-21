package lu.itrust.business.TS.actionplan;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lu.itrust.business.TS.Asset;

/**
 * ActionPlanAsset: <br>
 * Represents a Asset with its ALE value inside a specific Action Plan Entry.
 * 
 * @author itrust consulting s.a.rl. : EOM, BJA, SME
 * @version 0.1
 * @since 29 janv. 2013
 */
@Entity 
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"fiActionPlanCalculation", "fiAsset"}))
public class ActionPlanAsset {

	/***********************************************************************************************
	 * Fields Declaration
	 **********************************************************************************************/

	/** The ActionPlanAsset id */
	@Id @GeneratedValue 
	@Column(name="idActionPlanAsset")
	private int id = -1;

	/** The ActionPlanEntry Object */
	@ManyToOne 
	@JoinColumn(name="fiActionPlanCalculation", nullable=false)
	@Access(AccessType.FIELD)
	private ActionPlanEntry actionPlanEntry = null;

	/** The Assessment Object */
	@ManyToOne 
	@JoinColumn(name="fiAsset", nullable=false)
	@Access(AccessType.FIELD)
	private Asset asset = null;
	
	/** The ALE value */
	@Column(name="dtCurrentALE", nullable=false)
	@Access(AccessType.FIELD)
	private double currentALE = 0;

	/***********************************************************************************************
	 * Constructors
	 **********************************************************************************************/

	/**
	 * Constructor:<br>
	 */
	public ActionPlanAsset() {
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
	public ActionPlanAsset(ActionPlanEntry actionPlanEntry, Asset asset, double currentALE) {
		this.actionPlanEntry = actionPlanEntry;
		this.asset = asset;
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
	public Asset getAsset() {
		return asset;
	}

	/**
	 * setAssessment: <br>
	 * Sets the Field "assessment" with a value.
	 * 
	 * @param asset
	 *            The Value to set the assessment field
	 */
	public void setAsset(Asset asset) {
		this.asset = asset;
	}
}