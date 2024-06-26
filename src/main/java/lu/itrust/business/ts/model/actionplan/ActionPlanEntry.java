package lu.itrust.business.ts.model.actionplan;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.model.standard.measure.Measure;

/**
 * ActionPlanEntry: <br>
 * Contains an entry in the action plan: This entry's structure is:
 * AnalysisStandard object, Measure object, the total ALE, delta ALE and the
 * ROSI/ROSMI.
 * 
 * @author itrust consulting s.a r.l.
 * @version 0.1
 * @since 2012-09-13
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "ActionPlan")
public class ActionPlanEntry {

	/**
	 * Regular expression to match valid entry position (positive or negative
	 * number or =)
	 */
	@Transient
	public static final Pattern POSITION_REGEX = Pattern.compile("[-+]\\d+|=|\\d+");

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** The ID of the entry */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idActionPlanCalculation")
	private int id = 0;

	/** action plan type */
	@ManyToOne
	@JoinColumn(name = "fiActionPlanType", nullable = false)
	@Access(AccessType.FIELD)
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@Cascade(CascadeType.SAVE_UPDATE)
	private ActionPlanType actionPlanType = null;

	/** The Measure object reference */
	@ManyToOne
	@JoinColumn(name = "fiMeasure", nullable = false)
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@Access(AccessType.FIELD)
	private Measure measure = null;

	/** The position refered from the normal action plan */

	@Column(name = "dtOrder", nullable = false, length = 5)
	private String order = "";

	/** the order inside the action plan type */
	@Column(name = "dtPosition", nullable = false)
	private int position = 0;

	/** The total ALE of each mode (normal, pessimistic, optimistic) */
	@Column(name = "dtTotalALE", nullable = false)
	private double totalALE = 0;

	/** The Delta ALE of each mode (normal, pessimistic, optimistic) */
	@Column(name = "dtDeltaALE", nullable = false)
	private double deltaALE = 0;

	/** The cost of the measure */
	@Column(name = "dtCost", nullable = false)
	private double cost = 0;

	/**
	 * Return of investment for Security and Maturity investment of each mode
	 * (normal, pessimistic, optimistic)
	 */
	@Column(name = "dtROI", nullable = false)
	private double ROI = 0;

	/**
	 * Only for qualitative
	 */
	@Column(name = "dtRiskCount", nullable = false)
	private int riskCount = 0;

	/** list of assets with the current ALE of this entry */
	@OneToMany(mappedBy = "actionPlanEntry")
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@Cascade({ CascadeType.SAVE_UPDATE, CascadeType.DELETE })
	@OrderBy("currentALE DESC")
	@Access(AccessType.FIELD)
	private List<ActionPlanAsset> actionPlanAssets = new ArrayList<>();

	/***********************************************************************************************
	 * Constructor
	 **********************************************************************************************/

	/**
	 * Constructor:<br>
	 */
	public ActionPlanEntry() {
	}

	/**
	 * @param measure
	 * @param riskCount
	 */
	public ActionPlanEntry(Measure measure, ActionPlanType actionplantype, int riskCount) {
		this.measure = measure;
		this.riskCount = riskCount;
		setCost(measure.getCost());
		setActionPlanType(actionplantype);
	}

	/**
	 * Constructor: <br>
	 * 
	 * @param measure
	 * @param actionplantype
	 * @param deltaALE
	 * @throws IllegalArgumentException
	 * @throws TrickException
	 */
	public ActionPlanEntry(Measure measure, ActionPlanType actionplantype, double deltaALE) throws IllegalArgumentException, TrickException {

		// the measure
		setMeasure(measure);

		setActionPlanType(actionplantype);

		// the delta ALE for the measure (normal,optimistic and pessimistic)
		setDeltaALE(deltaALE);

		// the measure's cost (by default if it is a maturity measure)
		setCost(measure.getCost());
	}

	/**
	 * Constructor: <br>
	 * This constructor is only used inside the generateNormalActionPlanEntries
	 * method to use a existing ALE to calculate a new ROSI and set the new list
	 * of assets (with current asset values)
	 * 
	 * @param measure
	 * @param actionPlanType
	 * @param actionPlanAssets
	 * @param totalALE
	 * @param deltaALE
	 * @throws TrickException
	 */
	public ActionPlanEntry(Measure measure, ActionPlanType actionPlanType, List<ActionPlanAsset> actionPlanAssets, double totalALE, double deltaALE) throws TrickException {

		// the measure
		setMeasure(measure);

		// add asset values to entry
		setActionPlanAssets(actionPlanAssets);

		setActionPlanType(actionPlanType);

		// add totalALE
		setTotalALE(totalALE);

		// the delta ALE for the measure (normal,optimistic and pessimistic)
		setDeltaALE(deltaALE);

		// the measure's cost (by default if it is a maturity measure)
		setCost(measure.getCost());
	}

	/***********************************************************************************************
	 * Methods
	 **********************************************************************************************/

	/**
	 * calculateROI: <br>
	 * Calculates the ROI for this entry. This takes the cost of this class.
	 * This is for normal measures the cost of the measure, for the maturity
	 * counts the cost from one SML to the next. The cost value will be adapted
	 * at the moment of creation of the class. In the end, this method adapts
	 * the totalALE value for this stage of the action plan
	 */
	public void calculcateROI() {
		this.ROI = this.deltaALE - this.cost;
		totalALE -= this.deltaALE;
	}

	/***********************************************************************************************
	 * Getters and Setters
	 **********************************************************************************************/

	/**
	 * getId: <br>
	 * Returns the "id" field value
	 * 
	 * @return The Action Plan Entry ID
	 */
	public int getId() {
		return id;
	}

	/**
	 * setId: <br>
	 * Sets the "id" field with a value
	 * 
	 * @param id
	 *            The value to set the Action Plan Entry ID
	 * @throws TrickException
	 */
	public void setId(int id) throws TrickException {
		if (id < 1)
			throw new TrickException("error.action_plan_entry.id", "Id should be greater than 0");
		this.id = id;
	}

	/**
	 * getMeasure: <br>
	 * Returns the "measure" field value
	 * 
	 * @return The Measure
	 */
	public Measure getMeasure() {
		return measure;
	}

	/**
	 * setMeasure: <br>
	 * Sets the "measure" field with an Measure Object. Measure cost is not
	 * updated at this moment.
	 * 
	 * @param measure
	 *            The value to set the Measure
	 * @throws TrickException
	 */
	public void setMeasure(Measure measure) throws TrickException {
		if (measure == null)
			throw new TrickException("error.action_plan_entry.measure", "Measure cannot be empty!");
		this.measure = measure;
	}

	/**
	 * getPosition: <br>
	 * Returns the "position" field value
	 * 
	 * @return The Position of the Entry
	 */
	public String getOrder() {
		return order;
	}

	/**
	 * setPosition: <br>
	 * Sets the "position" field with a value
	 * 
	 * @param position
	 *            The value to set the Entry Position
	 * @throws TrickException
	 */
	public void setOrder(String order) throws TrickException {
		if (order == null || !POSITION_REGEX.matcher(order).find())
			throw new TrickException("error.action_plan_entry.position", "Position is not valid");
		this.order = order;
	}

	/**
	 * getPosition: <br>
	 * Description
	 * 
	 * @return
	 */
	public int getPosition() {
		return position;
	}

	/**
	 * setPosition: <br>
	 * Description
	 * 
	 * @param position
	 */
	public void setPosition(int position) {
		this.position = position;
	}

	/**
	 * getTotalALE: <br>
	 * Returns the "totalALE" field value
	 * 
	 * @return The Total ALE
	 */
	public double getTotalALE() {
		return totalALE;
	}

	/**
	 * setTotalALE: <br>
	 * Sets the "totalALE" field with a value
	 * 
	 * @param totalALE
	 *            The value to set the Total ALE
	 * @throws TrickException
	 */
	public void setTotalALE(double totalALE) throws TrickException {
		if (Double.isNaN(totalALE))
			throw new TrickException("error.action_plan_entry.total.ale.nan", "Please check your data: Total ALE is not a number");
		else if (totalALE < 0)
			throw new TrickException("error.action_plan_entry.total_ale", "Total ALE cannot be negative!");
		this.totalALE = totalALE;
	}

	/**
	 * getDeltaALE: <br>
	 * Returns the "deltaALE" field value
	 * 
	 * @return The Delta ALE
	 */
	public double getDeltaALE() {
		return deltaALE;
	}

	/**
	 * setDeltaALE: <br>
	 * Sets the "deltaALE" field with a value
	 * 
	 * @param deltaALE
	 *            The value to set the Delta ALE
	 * @throws TrickException
	 */
	public void setDeltaALE(double deltaALE) throws TrickException {
		if (Double.isNaN(deltaALE))
			throw new TrickException("error.action_plan_entry.delta_ale.nan", "Please check your data: Delta ALE is not a number");
		if (deltaALE < 0)
			throw new TrickException("error.action_plan_entry.delta_ale", "Delta ALE cannot be negative!");
		this.deltaALE = deltaALE;
	}

	/**
	 * getCost: <br>
	 * Returns the "cost" field value
	 * 
	 * @return The Cost of the Measure
	 */
	public double getCost() {
		return cost;
	}

	/**
	 * setCost: <br>
	 * Sets the "cost" field with a value<br>
	 * + update ROI field value with method calculateROI.
	 * 
	 * @param cost
	 *            The value to set the Cost of the Measure
	 * @throws TrickException
	 */
	public void setCost(double cost) throws TrickException {
		if (Double.isNaN(cost))
			throw new TrickException("error.action_plan_entry.cost.nan", "Please check your data: Cost is not a number");
		else if (cost < 0)
			throw new TrickException("error.action_plan_entry", "Cost cannot be negative");
		this.cost = cost;
		calculcateROI();
	}

	/**
	 * getROI: <br>
	 * Returns the "ROI" field value
	 * 
	 * @return The ROI
	 */
	public double getROI() {
		return ROI;
	}

	/**
	 * setROI: <br>
	 * Sets the "ROI" field with a value
	 * 
	 * @param roi
	 *            The value to set the ROI
	 */
	public void setROI(double roi) {
		ROI = roi;
	}

	/**
	 * getActionPlanAssets: <br>
	 * Returns the actionPlanAssets field value.
	 * 
	 * @return The value of the actionPlanAssets field
	 */
	public List<ActionPlanAsset> getActionPlanAssets() {
		return actionPlanAssets;
	}

	/**
	 * setActionPlanAssets: <br>
	 * Sets the Field "actionPlanAssets" with a value.
	 * 
	 * @param actionPlanAssets
	 *            The Value to set the actionPlanAssets field
	 */
	public void setActionPlanAssets(List<ActionPlanAsset> actionPlanAssets) {
		this.actionPlanAssets = actionPlanAssets;
		for (int i = 0; i < actionPlanAssets.size(); i++)
			this.actionPlanAssets.get(i).setActionPlanEntry(this);
	}

	/**
	 * getActionPlanType: <br>
	 * Returns the actionPlanType field value.
	 * 
	 * @return The value of the actionPlanType field
	 */
	public ActionPlanType getActionPlanType() {
		return actionPlanType;
	}

	/**
	 * setActionPlanType: <br>
	 * Sets the Field "actionPlanType" with a value.
	 * 
	 * @param actionPlanType
	 *            The Value to set the actionPlanType field
	 */
	public void setActionPlanType(ActionPlanType actionPlanType) {
		this.actionPlanType = actionPlanType;
	}

	/**
	 * getActionPlanAsset: <br>
	 * Returns a ActionPlanAsset at the index given as parameter.
	 * 
	 * @param index
	 *            The index inside the ActionPlanAsset List
	 * @return The ActionPlanAsset Object at the given index
	 */
	public ActionPlanAsset getActionPlanAsset(int index) {
		return actionPlanAssets.get(index);
	}

	/**
	 * addActionPlanAsset: <br>
	 * Adds a ActionPlan Asset to the list of ActionPlan Assets
	 * 
	 * @param actionPlanAsset
	 *            The ActionPlanAsset Object to add
	 */
	public void addActionPlanAsset(ActionPlanAsset actionPlanAsset) {
		actionPlanAsset.setActionPlanEntry(this);
		actionPlanAssets.add(actionPlanAsset);
	}

	/**
	 * toString: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ActionPlanEntry {id=" + id + ",actionplantype=" + actionPlanType.getName() + ",position=" + position + ",cost=" + cost + ",ROI=" + ROI + ",totalALE=" + totalALE
				+ "," + "Measure {id=" + measure.getId() + ",standard=" + measure.getMeasureDescription().getStandard().getName()+ ",reference="
				+ measure.getMeasureDescription().getReference() + ",cost=" + measure.getCost() + ",IS=" + measure.getInternalWL() + ",ES=" + measure.getExternalWL() + ",INV="
				+ measure.getInvestment() + ",phase=" + measure.getPhase().getNumber() + "}} ";

	}

	/**
	 * @return the riskCount
	 */
	public int getRiskCount() {
		return riskCount;
	}

	/**
	 * @param riskCount
	 *            the riskCount to set
	 */
	public void setRiskCount(int riskCount) {
		this.riskCount = riskCount;
	}

}