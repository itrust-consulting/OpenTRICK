package lu.itrust.business.ts.model.cssf;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
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

import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.model.asset.Asset;
import lu.itrust.business.ts.model.scenario.Scenario;

/**
 * Represents a Risk Register Item.
 * 
 * This class is used to store information about a risk register item, including its identifier, scenario, asset, and evaluation data.
 * It provides methods to get and set the values of its fields, as well as merge the properties of another risk register item into this one.
 * It also provides a method to check if the asset and scenario IDs match the IDs of the current RiskRegisterItem.
 * 
 * This Class represents a single Entry inside the Risk Register. An Item has fields:<br>
 * <ul>
 * <li>Scenario Object</li>
 * <li>Position in the List</li>
 * <li>Raw: Probability - Impact - Importance</li>
 * <li>Net: Probability - Impact - Importance</li>
 * <li>Expected: Probability - Impact - Importance</li>
 * <li>Strategy</li>
 * </ul>
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "RiskRegister", uniqueConstraints = @UniqueConstraint(columnNames = { "fiAnalysis", "fiAsset", "fiScenario" }))
public class RiskRegisterItem {

	/***********************************************************************************************
	 * Fields
	 **********************************************************************************************/

	/** Identifier */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idRiskRegisterItem")
	private int id = 0;

	/** Scenario Object */
	@ManyToOne
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@JoinColumn(name = "fiScenario", nullable = false)
	@Access(AccessType.FIELD)
	private Scenario scenario = null;

	@ManyToOne
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@JoinColumn(name = "fiAsset", nullable = false)
	@Access(AccessType.FIELD)
	private Asset asset = null;

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

	/**
	 * Merges the properties of the given risk register item into this risk register item.
	 * 
	 * @param riskRegister The risk register item to merge.
	 * @return The merged risk register item.
	 */
	public RiskRegisterItem merge(RiskRegisterItem riskRegister) {
		if (riskRegister != null) {
			this.expectedEvaluation = riskRegister.expectedEvaluation;
			this.netEvaluation = riskRegister.netEvaluation;
			this.rawEvaluation = riskRegister.rawEvaluation;
		}
		return this;
	}

	/**
	 * Checks if the given asset and scenario IDs match the IDs of the current RiskRegisterItem.
	 * 
	 * @param idAsset The ID of the asset to compare.
	 * @param idScenario The ID of the scenario to compare.
	 * @return {@code true} if the asset and scenario IDs match, {@code false} otherwise.
	 */
	public Boolean is(int idAsset, int idScenario) {
		return asset.getId() == idAsset && scenario.getId() == idScenario;
	}
	
	/**
	 * @return key
	 * @see #key
	 */
	public String getKey() {
		return key(asset, scenario);
	}

	/**
	 * @return key
	 * @see #keyName
	 */
	public String getKeyName() {
		return keyName(asset, scenario);
	}

	/**
	 * 
	 * @param asset
	 * @param scenario
	 * @return asset.id+"^ID-'RISK_PROFILE'-ID^"+scenario.id
	 */
	public static String key(Asset asset, Scenario scenario) {
		return asset.getId() + "^ID-'RISK_REGISTER'-ID^" + scenario.getId();
	}

	/**
	 * 
	 * @param asset
	 * @param scenario
	 * @return asset.getName()+"^NAME-'RISK_PROFILE'-NAME^"+scenario.getName()
	 */
	public static String keyName(Asset asset, Scenario scenario) {
		return asset.getName() + "^NAME-'RISK_REGISTER'-NAME^" + scenario.getName();
	}

	/**
	 * Checks if the risk register item is compliant based on the given impact and probability values.
	 *
	 * @param impact the impact value to compare with the net evaluation impact
	 * @param probability the probability value to compare with the net evaluation probability
	 * @return true if the net evaluation impact is greater than or equal to the given impact
	 *         and the net evaluation probability is greater than or equal to the given probability,
	 *         false otherwise
	 */
	public boolean isCompliant(double impact, double probability) {
		return netEvaluation.getImpact() >= impact && netEvaluation.getProbability() >= probability;
	}
}