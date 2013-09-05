package lu.itrust.business.TS.actionplan;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.Measure;
import lu.itrust.business.TS.NormMeasure;

/**
 * ActionPlanEntry: <br>
 * Contains an entry in the action plan: This entry's structure is: AnalysisNorm object, Measure
 * object, the total ALE, delta ALE and the ROSI/ROSMI.
 * 
 * @author itrust consulting s.��� r.l. - BJA,SME
 * @version 0.1
 * @since 2012-09-13
 */
public class ActionPlanEntry implements Serializable {

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** The ID of the entry */
	private int id = -1;

	/** id unsaved value = null */
	private Analysis analysis = null;

	private ActionPlanType actionPlanType = null;

	/** The Measure object reference */
	private Measure measure = null;

	/** The position refered from the normal action plan */
	private String position = "";

	/** The total ALE of each mode (normal, pessimistic, optimistic) */
	private double totalALE = 0;

	/** The Delta ALE of each mode (normal, pessimistic, optimistic) */
	private double deltaALE = 0;

	/** The cost of the measure */
	private double cost = 0;

	/** Regular expression to match valid entry position (positive or negative number or =) */
	public static final String POSITION_REGEX = "[-+]\\d+|=|\\d+";

	/**
	 * Return of investment for Security and Maturity investment of each mode (normal, pessimistic,
	 * optimistic)
	 **/
	private double ROI = 0;

	/** Vector of assets at this state in the final action plan */
	//private List<ActionPlanAssessment> actionPlanAssessments = new ArrayList<ActionPlanAssessment>();

	private List<ActionPlanAsset> actionPlanAssets = new ArrayList<ActionPlanAsset>();
	
	/***********************************************************************************************
	 * Constructor
	 **********************************************************************************************/

	/**
	 * Constructor:<br>
	 */
	public ActionPlanEntry() {
	}

	/**
	 * Constructor:<br>
	 * Creates a new instance of the ActionPlanEntry class
	 * 
	 * @param analysisNorm
	 *            The AnalysisNorm Object reference
	 * @param measure
	 *            The Measure Object reference
	 * @param deltaALE
	 *            The Delta ALE
	 * @throws IllegalArgumentException
	 */
	public ActionPlanEntry(Measure measure, double deltaALE) throws IllegalArgumentException {

		// the measure
		setMeasure(measure);

		// the delta ALE for the measure (normal,optimistic and pessimistic)
		setDeltaALE(deltaALE);

		// the measure's cost (by default if it is a maturity measure)
		setCost(measure.getCost());
	}

	/**
	 * Constructor: <br>
	 * This constructor is only used inside the generateNormalActionPlanEntries method to use a
	 * existing ALE to calculate a new ROSI and set the new list of assets (with current asset
	 * values)
	 * 
	 * @param analysisNorm
	 *            The AnalysisNorm Object
	 * @param measure
	 *            The Measure object
	 * @param actionPlanType2
	 * @param tmpAssets
	 *            The Asset List object
	 * @param totalALE
	 *            The Total ALE value
	 * @param deltaALE
	 *            The Delta ALE value
	 */
	public ActionPlanEntry(NormMeasure measure, ActionPlanType actionPlanType,
			List<ActionPlanAsset> actionPlanAssets, double totalALE, double deltaALE) {

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
	
//	/**
//	 * Constructor: <br>
//	 * This constructor is only used inside the generateNormalActionPlanEntries method to use a
//	 * existing ALE to calculate a new ROSI and set the new list of assets (with current asset
//	 * values)
//	 * 
//	 * @param analysisNorm
//	 *            The AnalysisNorm Object
//	 * @param measure
//	 *            The Measure object
//	 * @param actionPlanType2
//	 * @param tmpAssets
//	 *            The Asset List object
//	 * @param totalALE
//	 *            The Total ALE value
//	 * @param deltaALE
//	 *            The Delta ALE value
//	 */
//	public ActionPlanEntry(NormMeasure measure, ActionPlanType actionPlanType,
//			List<ActionPlanAssessment> actionPlanAssessments, double totalALE, double deltaALE) {
//
//		// the measure
//		setMeasure(measure);
//
//		// add asset values to entry
//		setActionPlanAssessments(actionPlanAssessments);
//
//		setActionPlanType(actionPlanType);
//
//		// add totalALE
//		setTotalALE(totalALE);
//
//		// the delta ALE for the measure (normal,optimistic and pessimistic)
//		setDeltaALE(deltaALE);
//
//		// the measure's cost (by default if it is a maturity measure)
//		setCost(measure.getCost());
//	}

	/***********************************************************************************************
	 * Methods
	 **********************************************************************************************/

	/**
	 * calculateROI: <br>
	 * Calculates the ROI for this entry. This takes the cost of this class. This is for normal
	 * measures the cost of the measure, for the maturity counts the cost from one SML to the next.
	 * The cost value will be adapted at the moment of creation of the class. In the end, this
	 * method adapts the totalALE value for this stage of the action plan
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
	 */
	public void setId(int id) {
		if (id < 1) {
			throw new IllegalArgumentException("ID needs to be > 1");
		}
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
	 * Sets the "measure" field with an Measure Object. Measure cost is not updated at this moment.
	 * 
	 * @param measure
	 *            The value to set the Measure
	 */
	public void setMeasure(Measure measure) {
		if (measure == null) {
			throw new IllegalArgumentException("Measure cannot be null!");
		}
		this.measure = measure;
	}

	/**
	 * getPosition: <br>
	 * Returns the "position" field value
	 * 
	 * @return The Position of the Entry
	 */
	public String getPosition() {
		return position;
	}

	/**
	 * setPosition: <br>
	 * Sets the "position" field with a value
	 * 
	 * @param position
	 *            The value to set the Entry Position
	 */
	public void setPosition(String position) {
		if ((position == null) || (!position.matches(POSITION_REGEX))) {
			throw new IllegalArgumentException("Position should meet this regular expression "
				+ POSITION_REGEX);
		}
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
	 */
	public void setTotalALE(double totalALE) {
		if (totalALE < 0) {
			throw new IllegalArgumentException("Total ALE cannot be < 0!");
		}
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
	 */
	public void setDeltaALE(double deltaALE) {
		if (deltaALE < 0) {
			throw new IllegalArgumentException("Delta ALE cannot be < 0!");
		}
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
	 */
	public void setCost(double cost) {
		if (cost < 0) {
			throw new IllegalArgumentException("Cost needs to be >= 0");
		}
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

//	/**
//	 * getActionPlanAssets: <br>
//	 * Returns the actionPlanAssets field value.
//	 * 
//	 * @return The value of the actionPlanAssets field
//	 */
//	public List<ActionPlanAssessment> getActionPlanAssessments() {
//		return actionPlanAssessments;
//	}
	
//	/**
//	 * setActionPlanAssets: <br>
//	 * Sets the Field "actionPlanAssets" with a value.
//	 * 
//	 * @param actionPlanAssets
//	 *            The Value to set the actionPlanAssets field
//	 */
//	public void setActionPlanAssessments(List<ActionPlanAssessment> actionPlanAssets) {
//		this.actionPlanAssessments = actionPlanAssessments;
//		for (int i = 0; i < actionPlanAssets.size(); i++)
//			this.actionPlanAssessmets.get(i).setActionPlanEntry(this);
//	}

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
	 * getAnalysis: <br>
	 * Returns the analysis field value.
	 * 
	 * @return The value of the analysis field
	 */
	public Analysis getAnalysis() {
		return analysis;
	}

	/**
	 * setAnalysis: <br>
	 * Sets the Field "analysis" with a value.
	 * 
	 * @param analysis
	 *            The Value to set the analysis field
	 */
	public void setAnalysis(Analysis analysis) {
		this.analysis = analysis;
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

//	/**
//	 * getActionPlanAsset: <br>
//	 * Returns a ActionPlanAsset at the index given as parameter.
//	 * 
//	 * @param index
//	 *            The index inside the ActionPlanAsset List
//	 * @return The ActionPlanAsset Object at the given index
//	 */
//	public ActionPlanAssessment getActionPlanAssessment(int index) {
//		return actionPlanAssessments.get(index);
//	}

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
	
//	/**
//	 * addActionPlanAsset: <br>
//	 * Adds a ActionPlan Asset to the list of ActionPlan Assets
//	 * 
//	 * @param actionPlanAsset
//	 *            The ActionPlanAsset Object to add
//	 */
//	public void addActionPlanAssessment(ActionPlanAssessment actionPlanAsset) {
//		actionPlanAsset.setActionPlanEntry(this);
//		actionPlanAssessments.add(actionPlanAsset);
//	}
}