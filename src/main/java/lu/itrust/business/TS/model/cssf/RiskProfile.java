/**
 * 
 */
package lu.itrust.business.TS.model.cssf;

import java.util.Map;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import lu.itrust.business.TS.model.asset.Asset;
import lu.itrust.business.TS.model.parameter.Parameter;
import lu.itrust.business.TS.model.scenario.Scenario;

/**
 * @author eomar
 *
 */
@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "fiAsset", "fiScenario" }), @UniqueConstraint(columnNames = { "dtIdentifier", "fiAnalysis" }) })
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

	@Embedded
	@AssociationOverrides({ @AssociationOverride(name = "probability", joinColumns = @JoinColumn(name = "fiRawProbability")),
			@AssociationOverride(name = "impactRep", joinColumns = @JoinColumn(name = "fiRawImpactRep")),
			@AssociationOverride(name = "impactOp", joinColumns = @JoinColumn(name = "fiRawImpactOp")),
			@AssociationOverride(name = "impactLeg", joinColumns = @JoinColumn(name = "fiRawImpactLeg")),
			@AssociationOverride(name = "impactFin", joinColumns = @JoinColumn(name = "fiRawImpactFin")) })
	private RiskProbaImpact rawProbaImpact;

	@Embedded
	@AssociationOverrides({ @AssociationOverride(name = "probability", joinColumns = @JoinColumn(name = "fiExpProbability")),
			@AssociationOverride(name = "impactRep", joinColumns = @JoinColumn(name = "fiExpImpactRep")),
			@AssociationOverride(name = "impactOp", joinColumns = @JoinColumn(name = "fiExpImpactOp")),
			@AssociationOverride(name = "impactLeg", joinColumns = @JoinColumn(name = "fiExpImpactLeg")),
			@AssociationOverride(name = "impactFin", joinColumns = @JoinColumn(name = "fiExpImpactFin")) })
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
	 *            the id to set
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
	 *            the identifier to set
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	/**
	 * @return the asset
	 */
	public Asset getAsset() {
		return asset;
	}

	/**
	 * @param asset
	 *            the asset to set
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
	 *            the scenario to set
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
	 *            the riskStrategy to set
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
	 *            the riskTreatment to set
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
	 *            the actionPlan to set
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
	 * @param rawProbaImpact
	 *            the rawProbaImpact to set
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
	 *            the expProbaImpact to set
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

	/**
	 * 
	 * @param asset
	 * @param scenario
	 * @return asset.id+"^ID-'RISK_PROFILE'-ID^"+scenario.id
	 */
	public static String key(Asset asset, Scenario scenario) {
		return asset.getId() + "^ID-'RISK_PROFILE'-ID^" + scenario.getId();
	}

	/**
	 * 
	 * @param asset
	 * @param scenario
	 * @return asset.getName()+"^NAME-'RISK_PROFILE'-NAME^"+scenario.getName()
	 */
	public static String keyName(Asset asset, Scenario scenario) {
		return asset.getName() + "^NAME-'RISK_PROFILE'-NAME^" + scenario.getName();
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

	public RiskProfile duplicate() throws CloneNotSupportedException {
		RiskProfile riskProfile = (RiskProfile) super.clone();
		riskProfile.id = 0;
		return riskProfile;
	}

	public RiskProfile duplicate(Map<Integer, Asset> assets, Map<Integer, Scenario> scenarios, Map<String, Parameter> parameters) throws CloneNotSupportedException {
		RiskProfile riskProfile = (RiskProfile) super.clone();
		riskProfile.updateData(assets, scenarios, parameters);
		riskProfile.id = 0;
		return riskProfile;
	}

	public void updateData(Map<Integer, Asset> assets, Map<Integer, Scenario> scenarios, Map<String, Parameter> parameters) throws CloneNotSupportedException {
		if (rawProbaImpact != null)
			rawProbaImpact = rawProbaImpact.duplicate(parameters);
		if (expProbaImpact != null)
			expProbaImpact = expProbaImpact.duplicate(parameters);
		this.asset = assets.get(this.asset.getId());
		this.scenario = scenarios.get(scenario.getId());
	}

	public Boolean isSelected() {
		return asset.isSelected() && scenario.isSelected();
	}

}
