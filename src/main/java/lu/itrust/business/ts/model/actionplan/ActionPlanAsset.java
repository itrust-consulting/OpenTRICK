package lu.itrust.business.ts.model.actionplan;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import lu.itrust.business.ts.model.asset.Asset;

/**
 * ActionPlanAsset: <br>
 * Represents a Asset with its ALE value inside a specific Action Plan Entry.
 * 
 * @author itrust consulting s.a.rl. 
 * @version 0.1
 * @since 29 janv. 2013
 */
/**
 * Represents an Action Plan Asset.
 * This class is used to store information about an asset associated with an action plan entry.
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"fiActionPlanCalculation", "fiAsset"}))
public class ActionPlanAsset {

	/***********************************************************************************************
	 * Fields Declaration
	 **********************************************************************************************/

	/** The ActionPlanAsset id */
	@Id 
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="idActionPlanAssetCalculation")
	private int id = 0;

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