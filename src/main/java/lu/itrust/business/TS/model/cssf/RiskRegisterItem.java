package lu.itrust.business.TS.model.cssf;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.asset.Asset;
import lu.itrust.business.TS.model.scenario.Scenario;

/**
 * RiskRegisterItem: <br>
 * This Class represents a single Entry inside the Risk Register. A Item has as
 * fields:<br>
 * <ul>
 * <li>Scenario Object</li>
 * <li>Position in the List</li>
 * <li>Raw: Probability - Impact - Importance</li>
 * <li>Net: Probability - Impact - Importance</li>
 * <li>Expected: Probability - Impact - Importance</li>
 * <li>Strategy</li>
 * </ul>
 * 
 * @author itrust consulting s.a r.l. - BJA, SME, EOM
 * @version 0.1
 * @since 2012-12-11
 */
@Entity
@Table(name = "RiskRegister", uniqueConstraints = @UniqueConstraint(columnNames = { "fiAnalysis", "fiAsset", "fiScenario" }))
public class RiskRegisterItem {

	/***********************************************************************************************
	 * Fields
	 **********************************************************************************************/

	/** Identifier */
	@Id
	@GeneratedValue
	@Column(name = "idRiskRegisterItem")
	private int id = -1;

	/** Scenario Object */
	@ManyToOne
	@JoinColumn(name = "fiScenario", nullable = false)
	@Access(AccessType.FIELD)
	private Scenario scenario = null;

	@ManyToOne
	@JoinColumn(name = "fiAsset", nullable = false)
	@Access(AccessType.FIELD)
	private Asset asset = null;

	/** Position in the RiskRegister */
	@Column(name = "dtOrder", nullable = false)
	private int position = 0;

	/** The Expected Evaluation Data (Probability, Impact and Importance) */
	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "impact", column = @Column(name = "dtNetEvaluationImpact", nullable = false)),
			@AttributeOverride(name = "probability", column = @Column(name = "dtNetEvaluationProbability", nullable = false)),
			@AttributeOverride(name = "importance", column = @Column(name = "dtNetEvaluationImportance", nullable = false)) })
	@Access(AccessType.FIELD)
	private EvaluationResult netEvaluation = null;

	/** The Raw Evaluation Data (Probability, Impact and Importance) */
	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "impact", column = @Column(name = "dtRawEvaluationImpact", nullable = false)),
			@AttributeOverride(name = "probability", column = @Column(name = "dtRawEvaluationProbability", nullable = false)),
			@AttributeOverride(name = "importance", column = @Column(name = "dtRawEvaluationImportance", nullable = false)) })
	@Access(AccessType.FIELD)
	private EvaluationResult rawEvaluation = null;

	/** The Net Evaluation Data (Probability, Impact and Importance) */
	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "impact", column = @Column(name = "dtExpEvaluationImpact", nullable = false)),
			@AttributeOverride(name = "probability", column = @Column(name = "dtExpEvaluationProbability", nullable = false)),
			@AttributeOverride(name = "importance", column = @Column(name = "dtExpEvaluationImportance", nullable = false)) })
	@Access(AccessType.FIELD)
	private EvaluationResult expectedEvaluation = null;


	/***********************************************************************************************
	 * Constructors
	 **********************************************************************************************/

	/**
	 * Constructors:<br>
	 * 
	 * @throws TrickException
	 */
	public RiskRegisterItem() throws TrickException {
		rawEvaluation = new EvaluationResult(0, 0);
		expectedEvaluation = new EvaluationResult(0, 0);
		netEvaluation = new EvaluationResult(0, 0);
	}

	/**
	 * Constructor: <br>
	 * 
	 * @param scenario
	 *            The Scenario of this item
	 * @throws TrickException
	 */
	public RiskRegisterItem(Scenario scenario, Asset asset) throws TrickException {
		this.setScenario(scenario);
		this.setAsset(asset);
		rawEvaluation = new EvaluationResult(0, 0);
		expectedEvaluation = new EvaluationResult(0, 0);
		netEvaluation = new EvaluationResult(0, 0);
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
	 * @throws TrickException
	 */
	public void setScenario(Scenario scenario) throws TrickException {
		if (scenario == null)
			throw new TrickException("error.risk_register.scenario.empty", "Scenario cannot be empty");
		this.scenario = scenario;
	}

	/**
	 * getRawEvaluation: <br>
	 * Returns the "rawEvaluation" field Object.
	 * 
	 * @return The Raw Evaluation Object (With Probability - Impact -
	 *         Importance)
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
	 * @return The Net Evaluation Object (With Probability - Impact -
	 *         Importance)
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
	 * getExpectedEvaluation: <br>
	 * Returns the "expectedEvaluation" field Object.
	 * 
	 * @return The Expected Evaluation Object (With Probability - Impact -
	 *         Importance)
	 */
	public EvaluationResult getExpectedEvaluation() {
		return expectedEvaluation;
	}

	/**
	 * setExpectedEvaluation: <br>
	 * Sets the field "expectedEvaluation" with a Object.
	 * 
	 * @param expectedEvaluation
	 *            The Expected Evaluation Object to set
	 */
	public void setExpectedEvaluation(EvaluationResult expectedEvaluation) {
		this.expectedEvaluation = expectedEvaluation;
	}



	public Asset getAsset() {
		return asset;
	}

	public void setAsset(Asset asset) {
		this.asset = asset;
	}

	public RiskRegisterItem merge(RiskRegisterItem riskRegister) {
		if (riskRegister != null) {
			this.position = riskRegister.position;
			this.expectedEvaluation = riskRegister.expectedEvaluation;
			this.netEvaluation = riskRegister.netEvaluation;
			this.rawEvaluation = riskRegister.rawEvaluation;
		}
		return this;
	}

	public Boolean is(int idAsset, int idScenario) {
		return asset.getId() == idAsset && scenario.getId() == idScenario;
	}
}