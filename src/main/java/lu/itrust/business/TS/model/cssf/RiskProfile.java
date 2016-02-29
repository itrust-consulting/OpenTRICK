/**
 * 
 */
package lu.itrust.business.TS.model.cssf;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import lu.itrust.business.TS.model.asset.Asset;
import lu.itrust.business.TS.model.scenario.Scenario;

/**
 * @author eomar
 *
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "fiAsset", "fiScenario" }) )
public class RiskProfile {

	@Id
	@GeneratedValue
	@Column(name = "idRiskProfile")
	private int id;

	@ManyToOne
	@JoinColumn(name = "fiAsset")
	@Cascade(CascadeType.SAVE_UPDATE)
	private Asset asset;

	@ManyToOne
	@JoinColumn(name = "fiScenario")
	@Cascade(CascadeType.SAVE_UPDATE)
	private Scenario scenario;

	@Column(name = "dtOwner")
	private String owner;

	@Enumerated(EnumType.STRING)
	@Column(name = "dtStrategy")
	private RiskStrategy riskStrategy;

	@Column(name = "dtTreatment", length = 1024)
	private String riskTreatment;

	@Column(name = "dtActionPlan", length = 1024)
	private String actionPlan;

	@Embedded
	@AssociationOverrides({ @AssociationOverride(name = "probabitity", joinColumns = @JoinColumn(name = "fiRawProbabitity") ),
			@AssociationOverride(name = "impactRep", joinColumns = @JoinColumn(name = "fiRawImpactRep") ),
			@AssociationOverride(name = "impactOp", joinColumns = @JoinColumn(name = "fiRawImpactOp") ),
			@AssociationOverride(name = "impactLeg", joinColumns = @JoinColumn(name = "fiRawImpactLeg") ),
			@AssociationOverride(name = "impactFin", joinColumns = @JoinColumn(name = "fiRawImpactFin") ) })
	private RiskProbaImpact rawProbaImpact;

	@Embedded
	@AssociationOverrides({ @AssociationOverride(name = "probabitity", joinColumns = @JoinColumn(name = "fiExpProbabitity") ),
			@AssociationOverride(name = "impactRep", joinColumns = @JoinColumn(name = "fiExpImpactRep") ),
			@AssociationOverride(name = "impactOp", joinColumns = @JoinColumn(name = "fiExpImpactOp") ),
			@AssociationOverride(name = "impactLeg", joinColumns = @JoinColumn(name = "fiExpImpactLeg") ),
			@AssociationOverride(name = "impactFin", joinColumns = @JoinColumn(name = "fiExpImpactFin") ) })
	private RiskProbaImpact expProbaImpact;

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
	 * @return the owner
	 */
	public String getOwner() {
		return owner;
	}

	/**
	 * @param owner
	 *            the owner to set
	 */
	public void setOwner(String owner) {
		this.owner = owner;
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
	 * 
	 * @param asset
	 * @param scenario
	 * @return asset.id+"-_-"+scenario.id
	 */
	public static String key(Asset asset, Scenario scenario) {
		return asset.getId() + "-_-" + scenario.getId();
	}

	public Boolean is(int idAsset, int idScenario) {
		return asset.getId() == idAsset && scenario.getId() == idScenario;
	}

}
