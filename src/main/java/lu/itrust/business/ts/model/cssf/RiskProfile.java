/**
 * 
 */
package lu.itrust.business.ts.model.cssf;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.persistence.AssociationOverride;
import jakarta.persistence.AssociationOverrides;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import lu.itrust.business.ts.model.asset.Asset;
import lu.itrust.business.ts.model.parameter.IParameter;
import lu.itrust.business.ts.model.scale.ScaleType;
import lu.itrust.business.ts.model.scenario.Scenario;
import lu.itrust.business.ts.model.standard.measure.Measure;


/**
 * The RiskProfile class represents a risk profile entity in the system.
 * It contains information about the risk profile, such as the identifier, asset, scenario, risk strategy, risk treatment,
 * action plan, measures, and probability impacts.
 * 
 * This class also provides methods for setting and retrieving the properties of a risk profile, as well as cloning and duplicating
 * risk profiles.
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "fiAsset", "fiScenario" }),
		@UniqueConstraint(columnNames = { "dtIdentifier", "fiAnalysis" }) })
public class RiskProfile implements Cloneable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idRiskProfile")
	private int id;

	@Column(name = "dtIdentifier")
	private String identifier;

	@ManyToOne
	@JoinColumn(name = "fiAsset")
	@Cascade(CascadeType.SAVE_UPDATE)
	private Asset asset;

	@ManyToOne
	@JoinColumn(name = "fiScenario")
	@Cascade(CascadeType.SAVE_UPDATE)
	private Scenario scenario;

	@Enumerated(EnumType.STRING)
	@Column(name = "dtStrategy")
	private RiskStrategy riskStrategy = RiskStrategy.REDUCE;

	@Column(name = "dtTreatment", length = 1024)
	private String riskTreatment;

	@Column(name = "dtActionPlan", length = 1024)
	private String actionPlan;

	@ManyToMany
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@JoinTable(name = "RiskProfileMeasures", joinColumns = @JoinColumn(name = "fiRiskProfile"), inverseJoinColumns = @JoinColumn(name = "fiMeasure"), uniqueConstraints = @UniqueConstraint(columnNames = {
			"fiRiskProfile", "fiMeasure" }))
	@Cascade(CascadeType.SAVE_UPDATE)
	private List<Measure> measures = new LinkedList<>();

	@Embedded
	@AttributeOverrides(@AttributeOverride(name = "vulnerability", column = @Column(name = "dtRawVulnerability")))
	@AssociationOverrides({
			@AssociationOverride(name = "probability", joinColumns = @JoinColumn(name = "fiRawProbability")),
			@AssociationOverride(name = "impacts", joinTable = @JoinTable(name = "RiskProfileRawImpacts", joinColumns = @JoinColumn(name = "fiRiskProfile"), inverseJoinColumns = @JoinColumn(name = "fiRawImpact"), uniqueConstraints = @UniqueConstraint(columnNames = {
					"fiRawImpact", "fiRiskProfile" }))) })
	@Cascade(CascadeType.ALL)
	private RiskProbaImpact rawProbaImpact;

	@Embedded
	@AttributeOverrides(@AttributeOverride(name = "vulnerability", column = @Column(name = "dtExpVulnerability")))
	@AssociationOverrides({
			@AssociationOverride(name = "probability", joinColumns = @JoinColumn(name = "fiExpProbability")),
			@AssociationOverride(name = "impacts", joinTable = @JoinTable(name = "RiskProfileExpImpacts", joinColumns = @JoinColumn(name = "fiRiskProfile"), inverseJoinColumns = @JoinColumn(name = "fiExpImpact"), uniqueConstraints = @UniqueConstraint(columnNames = {
					"fiExpImpact", "fiRiskProfile" }))) })
	@Cascade(CascadeType.ALL)
	private RiskProbaImpact expProbaImpact;

	/**
	 * 
	 */
	public RiskProfile() {
	}

	public RiskProfile(Asset asset, Scenario scenario) {
		setAsset(asset);
		setScenario(scenario);
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *           the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the identifier
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * @param identifier
	 *                   the identifier to set
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier == null || identifier.isEmpty() ? null : identifier;
	}

	/**
	 * @return the asset
	 */
	public Asset getAsset() {
		return asset;
	}

	/**
	 * @param asset
	 *              the asset to set
	 */
	public void setAsset(Asset asset) {
		this.asset = asset;
	}

	/**
	 * @return the scenario
	 */
	public Scenario getScenario() {
		return scenario;
	}

	/**
	 * @param scenario
	 *                 the scenario to set
	 */
	public void setScenario(Scenario scenario) {
		this.scenario = scenario;
	}

	/**
	 * @return the riskStrategy
	 */
	public RiskStrategy getRiskStrategy() {
		return riskStrategy;
	}

	/**
	 * @param riskStrategy
	 *                     the riskStrategy to set
	 */
	public void setRiskStrategy(RiskStrategy riskStrategy) {
		if (riskStrategy == null)
			riskStrategy = RiskStrategy.REDUCE;
		this.riskStrategy = riskStrategy;
	}

	/**
	 * @return the riskTreatment
	 */
	public String getRiskTreatment() {
		return riskTreatment;
	}

	/**
	 * @param riskTreatment
	 *                      the riskTreatment to set
	 */
	public void setRiskTreatment(String riskTreatment) {
		this.riskTreatment = riskTreatment;
	}

	/**
	 * @return the actionPlan
	 */
	public String getActionPlan() {
		return actionPlan;
	}

	/**
	 * @param actionPlan
	 *                   the actionPlan to set
	 */
	public void setActionPlan(String actionPlan) {
		this.actionPlan = actionPlan;
	}

	/**
	 * @return the rawProbaImpact
	 */
	public RiskProbaImpact getRawProbaImpact() {
		return rawProbaImpact;
	}

	/**
	 * @return the measures
	 */
	public List<Measure> getMeasures() {
		return measures;
	}

	/**
	 * @param measures
	 *                 the measures to set
	 */
	public void setMeasures(List<Measure> measures) {
		this.measures = measures;
	}

	/**
	 * @param rawProbaImpact
	 *                       the rawProbaImpact to set
	 */
	public void setRawProbaImpact(RiskProbaImpact rawProbaImpact) {
		this.rawProbaImpact = rawProbaImpact;
	}

	/**
	 * @return the expProbaImpact
	 */
	public RiskProbaImpact getExpProbaImpact() {
		return expProbaImpact;
	}

	/**
	 * @param expProbaImpact
	 *                       the expProbaImpact to set
	 */
	public void setExpProbaImpact(RiskProbaImpact expProbaImpact) {
		this.expProbaImpact = expProbaImpact;
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

	public Boolean is(int idAsset, int idScenario) {
		return asset.getId() == idAsset && scenario.getId() == idScenario;
	}

	public int getComputedRawImportance() {
		return rawProbaImpact == null ? 0 : rawProbaImpact.getImportance();
	}

	public int getComputedExpImportance() {
		return expProbaImpact == null ? 0 : expProbaImpact.getImportance();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public RiskProfile clone() throws CloneNotSupportedException {
		RiskProfile riskProfile = (RiskProfile) super.clone();
		riskProfile.asset = asset.clone();
		riskProfile.scenario.clone();
		if (rawProbaImpact != null)
			riskProfile.rawProbaImpact = rawProbaImpact.clone();
		if (expProbaImpact != null)
			riskProfile.expProbaImpact = expProbaImpact.clone();
		return riskProfile;
	}


	/**
	 * Creates a duplicate of the current risk profile.
	 *
	 * @return A new instance of the RiskProfile class with the same values as the original.
	 * @throws CloneNotSupportedException if cloning is not supported for this object.
	 */
	public RiskProfile duplicate() throws CloneNotSupportedException {
		RiskProfile riskProfile = (RiskProfile) super.clone();
		riskProfile.id = 0;
		return riskProfile;
	}
	

	/**
	 * Creates a duplicate of the risk profile with updated data.
	 * 
	 * @param assets     a map of asset IDs to Asset objects
	 * @param scenarios  a map of scenario IDs to Scenario objects
	 * @param parameters a map of parameter names to IParameter objects
	 * @param measures   a map of measure names to Measure objects
	 * @return a duplicate RiskProfile object with updated data
	 * @throws CloneNotSupportedException if cloning is not supported
	 */
	public RiskProfile duplicate(Map<Integer, Asset> assets, Map<Integer, Scenario> scenarios,
			Map<String, IParameter> parameters, Map<String, Measure> measures)
			throws CloneNotSupportedException {
		RiskProfile riskProfile = (RiskProfile) super.clone();
		riskProfile.updateData(assets, scenarios, parameters, measures);
		riskProfile.id = 0;
		return riskProfile;
	}

	/**
	 * Updates the data of the RiskProfile object.
	 * 
	 * @param assets     a map of asset IDs to Asset objects
	 * @param scenarios  a map of scenario IDs to Scenario objects
	 * @param parameters a map of parameter names to IParameter objects
	 * @param measures   a map of measure names to Measure objects
	 * @throws CloneNotSupportedException if cloning is not supported for the rawProbaImpact or expProbaImpact objects
	 */
	public void updateData(Map<Integer, Asset> assets, Map<Integer, Scenario> scenarios,
			Map<String, IParameter> parameters, Map<String, Measure> measures)
			throws CloneNotSupportedException {
		if (rawProbaImpact != null)
			rawProbaImpact = rawProbaImpact.duplicate(parameters);
		if (expProbaImpact != null)
			expProbaImpact = expProbaImpact.duplicate(parameters);
		this.asset = assets.get(this.asset.getId());
		this.scenario = scenarios.get(scenario.getId());
		this.measures = this.measures.stream().filter(measure -> measures.containsKey(measure.getKeyName()))
				.map(measure -> measures.get(measure.getKeyName()))
				.collect(Collectors.toList());
	}

	/**
	 * Returns a Boolean value indicating whether the asset and scenario are selected.
	 *
	 * @return true if both the asset and scenario are selected, false otherwise
	 */
	public Boolean isSelected() {
		return asset.isSelected() && scenario.isSelected();
	}

	/**
	 * Removes the impacts associated with the given scale type from the risk profile.
	 *
	 * @param scaleType the scale type to remove impacts for
	 */
	public void remove(ScaleType scaleType) {
		if (rawProbaImpact != null)
			rawProbaImpact.getImpacts().removeIf(parameter -> parameter.isMatch(scaleType.getName()));
		if (expProbaImpact != null)
			expProbaImpact.getImpacts().removeIf(parameter -> parameter.isMatch(scaleType.getName()));
	}

	/**
	 * 
	 * @param asset
	 * @param scenario
	 * @return KEY
	 * @see #key(int, int)
	 */
	public static String key(Asset asset, Scenario scenario) {
		return key(asset.getId(), scenario.getId());
	}

	/**
	 * 
	 * @param asset
	 * @param scenario
	 * @return key
	 * @see #keyName(String, String)
	 */
	public static String keyName(Asset asset, Scenario scenario) {
		return keyName(asset.getName(), scenario.getName());
	}

	/**
	 * 
	 * @param assetId
	 * @param scenarioId
	 * @return assetId+"^ID-'RISK_PROFILE'-ID^"+scenarioId
	 */
	public static String key(int assetId, int scenarioId) {
		return assetId + "^ID-'RISK_PROFILE'-ID^" + scenarioId;
	}

	/**
	 * 
	 * @param assetName
	 * @param scenarioName
	 * @return assetName+"^NAME-'RISK_PROFILE'-NAME^"+scenarioName
	 */
	public static String keyName(String assetName, String scenarioName) {
		return assetName + "^NAME-'RISK_PROFILE'-NAME^" + scenarioName;
	}
}
