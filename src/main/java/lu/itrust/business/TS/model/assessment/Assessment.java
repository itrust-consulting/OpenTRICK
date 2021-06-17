package lu.itrust.business.TS.model.assessment;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Any;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.ManyToAny;

import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.asset.Asset;
import lu.itrust.business.TS.model.parameter.IAcronymParameter;
import lu.itrust.business.TS.model.parameter.value.IParameterValue;
import lu.itrust.business.TS.model.parameter.value.IValue;
import lu.itrust.business.TS.model.scale.ScaleType;
import lu.itrust.business.TS.model.scenario.Scenario;

/**
 * Assessment: <br>
 * This class represents an assessments and all its data.
 * 
 * This class is used to store assessments data for assets, scenarios, and their
 * ALE (normal, optimistic, pessimistic)
 * 
 * This class is used for the action plan calculation.
 * 
 * @author itrust consulting s.à r.l. - SME,BJA
 * @version 0.1
 * @since 2012-08-21
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "fiAsset", "fiScenario" }), @UniqueConstraint(columnNames = { "dtLikelihoodType", "fiLikelihood" }) })
public class Assessment implements Cloneable {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** The Annual Loss Expectancy - Normal */
	@Column(name = "dtALE", nullable = false)
	private double ALE = 0;

	/** The Annual Loss Expectancy - Optimistic */
	@Column(name = "dtALEO", nullable = false)
	private double ALEO = 0;

	/** The Annual Loss Expectancy - Pessimistic */
	@Column(name = "dtALEP", nullable = false)
	private double ALEP = 0;

	/** The asset object reference */
	@ManyToOne
	@JoinColumn(name = "fiAsset", nullable = false)
	@Access(AccessType.FIELD)
	@Cascade(CascadeType.SAVE_UPDATE)
	private Asset asset = null;

	/** A comment on this assessment */
	@Column(name = "dtComment", nullable = false, length = 16777216)
	private String comment = "";

	/** hidden assessment comment */
	@Column(name = "dtHiddenComment", nullable = false, length = 16777216)
	private String hiddenComment = "";

	/** identifier from the database */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idAssessment")
	private int id = -1;

	@Transient
	private Map<String, IValue> impactMapper = null;

	/** The impactFin value of this assessment */
	@Column(name = "dtImpactReal", nullable = false)
	private double impactReal = 0;

	@ManyToAny(metaDef = "VALUE_META_DEF", metaColumn = @Column(name = "dtValueType"))
	@Cascade(CascadeType.ALL)
	@JoinTable(name = "AssessmentImpacts", joinColumns = @JoinColumn(name = "fiAssessment"), inverseJoinColumns = @JoinColumn(name = "fiValue"), uniqueConstraints = @UniqueConstraint(columnNames = {
			"dtValueType", "fiValue" }))
	private List<IValue> impacts = new LinkedList<>();

	/** The likelihood value of this assessment */
	@Any(metaDef = "VALUE_META_DEF", metaColumn = @Column(name = "dtLikelihoodType"))
	@Cascade(CascadeType.ALL)
	@JoinColumn(name = "fiLikelihood")
	private IValue likelihood = null;

	/** The likelihood value of this assessment */
	@Column(name = "dtLikelihoodReal", nullable = false)
	private double likelihoodReal = 0;

	/** hidden assessment comment */
	@Column(name = "dtOwner")
	private String owner = "";

	/** The scenario object reference */
	@ManyToOne
	@JoinColumn(name = "fiScenario", nullable = false)
	@Access(AccessType.FIELD)
	@Cascade(CascadeType.SAVE_UPDATE)
	private Scenario scenario = null;

	/** Assessment selected flag */
	@Column(name = "dtSelected", nullable = false)
	private boolean selected = false;

	/** The uncertainty value of this assessment */
	@Column(name = "dtUncertainty", nullable = false)
	private double uncertainty = 2; // 1 + 1e-7;

	public Assessment() {
	}

	/***********************************************************************************************
	 * Getters and Setters
	 * 
	 * @throws TrickException
	 **********************************************************************************************/

	public Assessment(Asset asset, Scenario scenario) throws TrickException {
		setAsset(asset);
		setScenario(scenario);
		setSelected(asset.isSelected() && scenario.isSelected() && scenario.hasInfluenceOnAsset(asset));
	}

	/**
	 * clone: <br>
	 * Description
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Assessment clone() throws CloneNotSupportedException {
		Assessment assessment = (Assessment) super.clone();
		assessment.asset = (Asset) asset.clone();
		assessment.scenario = (Scenario) scenario.clone();
		return assessment;
	}

	/**
	 * duplicate: <br>
	 * Description
	 * 
	 * @return
	 * @throws CloneNotSupportedException
	 * @throws TrickException
	 */
	public Assessment duplicate() throws CloneNotSupportedException, TrickException {
		Assessment assessment = (Assessment) super.clone();
		assessment.id = -1;
		return assessment;
	}

	/**
	 * equals: <br>
	 * Checks this Assessment to other to find out if they are equal or not. Equal
	 * means: ID, Asset and Scenario are equal.<br>
	 * <br>
	 * 
	 * <b>NOTE:</b>This Method is auto generated
	 * 
	 * @see This method is auto generated
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Assessment other = (Assessment) obj;

		if (id > 0 && other.id > 0)
			return Double.doubleToLongBits(id) == Double.doubleToLongBits(other.id);

		if (asset == null) {
			if (other.asset != null) {
				return false;
			}
		} else if (!asset.equals(other.asset)) {
			return false;
		}

		if (scenario == null) {
			if (other.scenario != null) {
				return false;
			}
		} else if (!scenario.equals(other.scenario)) {
			return false;
		}
		return true;
	}

	/**
	 * getALE: <br>
	 * Returns the field "ALE"
	 * 
	 * @return The "Annual Loss Expectancy Normal" value
	 */
	public double getALE() {
		return this.ALE;
	}

	/**
	 * getALEO: <br>
	 * Returns the field "ALEO"
	 * 
	 * @return The "Annual Loss Expectancy Optimistic" value
	 */
	public double getALEO() {
		return ALEO;
	}

	/**
	 * getALEP: <br>
	 * Returns the field "ALEP"
	 * 
	 * @return The "Annual Loss Expectancy Pessimistic" value
	 */
	public double getALEP() {
		return ALEP;
	}

	/**
	 * getAsset: <br>
	 * Returns the field "asset"
	 * 
	 * @return The Asset object
	 */
	public Asset getAsset() {
		return asset;
	}

	/**
	 * getComment: <br>
	 * Returns the field "comment"
	 * 
	 * @return The Assessment Comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * getHiddenComment: <br>
	 * Returns the hiddenComment field value.
	 * 
	 * @return The value of the hiddenComment field
	 */
	public String getHiddenComment() {
		return hiddenComment;
	}

	/**
	 * getId: <br>
	 * Returns the field "id"
	 * 
	 * @return The Assessment ID
	 */
	public int getId() {
		return id;
	}

	public IValue getImpact(String name) {
		return getImpactMapper().get(name);
	}

	public Optional<IValue> findImpact(String name) {
		final IValue value = getImpactMapper().get(name);
		return value == null ? Optional.empty() : Optional.of(value);
	}

	public String getImpactAcronym(String name) {
		IValue value = getImpact(name);
		return value == null ? null : value.getVariable();
	}

	public int getImpactLevel(String name) {
		IValue value = getImpact(name);
		return value == null ? 0 : value.getLevel();
	}

	public IAcronymParameter getImpactParameter(String name) {
		IValue value = getImpact(name);
		return value == null || !(value instanceof IParameterValue) ? null : ((IParameterValue) value).getParameter();
	}

	public double getImpactReal() {
		return impactReal;
	}

	/**
	 * @return the impacts
	 */
	public List<IValue> getImpacts() {
		return impacts;
	}

	public double getImpactValue(String name) {
		IValue value = getImpact(name);
		return value == null ? 0D : value.getReal();
	}

	@Transient
	public String getKey() {
		return key(asset, scenario);
	}

	@Transient
	public String getKeyName() {
		return keyName(asset, scenario);
	}

	/**
	 * getLikelihood: <br>
	 * Returns the field "likelihood"
	 * 
	 * @return The Likelihood value
	 */
	public IValue getLikelihood() {
		return likelihood;
	}

	/**
	 * getLikelihoodReal: <br>
	 * Returns the field "likelihoodReal"
	 * 
	 * @return The Real Likelihood value
	 */
	public double getLikelihoodReal() {
		return likelihoodReal;
	}

	/**
	 * @return the owner
	 */
	public String getOwner() {
		return owner;
	}

	/**
	 * getScenario: <br>
	 * Returns the field "scenario"
	 * 
	 * @return The Scenario object
	 */
	public Scenario getScenario() {
		return scenario;
	}

	/**
	 * getUncertainty: <br>
	 * Returns the field "uncertainty"
	 * 
	 * @return The Uncertainty value
	 */
	public double getUncertainty() {
		return uncertainty;
	}

	/**
	 * hashCode: <br>
	 * Used in method equals(). <br>
	 * <br>
	 * <b>NOTE:</b>This Method is auto generated
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((asset == null) ? 0 : asset.hashCode());
		long temp;
		temp = Double.doubleToLongBits(id);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((scenario == null) ? 0 : scenario.hashCode());
		return result;
	}

	public boolean hasImpact(String name) {
		return getImpactMapper().containsKey(name);
	}

	public boolean is(int idAsset, int idScenario) {
		return asset.getId() == idAsset && scenario.getId() == idScenario;
	}

	/**
	 * isSelected: <br>
	 * Returns the selected field value.
	 * 
	 * @return The value of the selected field
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * isUsable: <br>
	 * Checks if the Asset and the Scenario are Selected and ALE is not 0. Checks if
	 * this Assessment can be used for Action Plan calculation.
	 * 
	 * @return True: if the Assessment is usable; False: if Assessment is not usable
	 */
	public boolean isUsable() {
		return (this.getAsset() == null) || (this.getScenario() == null) ? false
				: (this.isSelected() && this.getAsset().isSelected() && this.getScenario().isSelected() && this.getALE() > 0);
	}

	public IValue remove(ScaleType scaleType) {
		IValue value = impacts.stream().filter(impact -> impact.getName().equals(scaleType.getName())).findAny().orElse(null);
		if (value != null)
			impacts.remove(value);
		return value;
	}

	/**
	 * setALE: <br>
	 * Sets the field "ALE" with a value
	 * 
	 * @param ALE The value to set "ALE"
	 * @throws TrickException
	 */
	public void setALE(double ALE) throws TrickException {
		if (ALE < 0)
			throw new TrickException("error.assessment.ale", "ALE value cannot be negative");
		this.ALE = ALE;
	}

	/**
	 * setALEO: <br>
	 * Sets the field "ALEO" with a value
	 * 
	 * @param ALEO The value to set "ALEO"
	 * @throws TrickException
	 */
	public void setALEO(double ALEO) throws TrickException {
		if (ALEO < 0)
			throw new TrickException("error.assessment.aleo", "ALEO value cannot be negative");
		this.ALEO = ALEO;
	}

	/**
	 * setALEP: <br>
	 * Sets the field "ALEP" with a value
	 * 
	 * @param ALEP The value to set "ALEP"
	 * @throws TrickException
	 */
	public void setALEP(double ALEP) throws TrickException {
		if (ALEP < 0)
			throw new TrickException("error.assessment.alep", "ALEP value cannot be negative");
		this.ALEP = ALEP;
	}

	/**
	 * setAsset: <br>
	 * Sets the field "asset" with a value
	 * 
	 * @param asset The Asset object to set
	 * @throws TrickException
	 */
	public void setAsset(Asset asset) throws TrickException {
		if (asset == null)
			throw new TrickException("error.assessment.asset", "Asset cannot be empty");
		this.asset = asset;
	}

	/**
	 * setComment: <br>
	 * Sets the field "comment" with a value
	 * 
	 * @param comment The value to set "comment"
	 */
	public void setComment(String comment) {
		if (comment == null)
			this.comment = "";
		else
			this.comment = comment;
	}

	/**
	 * setHiddenComment: <br>
	 * Sets the Field "hiddenComment" with a value.
	 * 
	 * @param hideComment The Value to set the hiddenComment field
	 */
	public void setHiddenComment(String hiddenComment) {
		if (hiddenComment == null)
			this.hiddenComment = "";
		else
			this.hiddenComment = hiddenComment;
	}

	/**
	 * setId: <br>
	 * Sets the field "id" with a value
	 * 
	 * @param id The value to set "id"
	 * @throws TrickException
	 */
	public void setId(int id) throws TrickException {
		if (id < 1)
			throw new TrickException("error.assessment.id", "Assessment id should be greater than 0");
		this.id = id;
	}

	public void setImpact(IValue impact) {
		synchronized (this) {
			IValue old = getImpactMapper().get(impact.getName());
			if (old == null) {
				impacts.add(impact);
				getImpactMapper().put(impact.getName(), impact);
			} else if (!old.equals(impact)) {
				impacts.remove(old);
				impacts.add(impact);
				getImpactMapper().put(impact.getName(), impact);
			}
		}
	}

	public void setImpactReal(double impactReal) throws TrickException {
		if (impactReal < 0)
			throw new TrickException("error.assessment.impact_value", "Impact value cannot be negative");
		this.impactReal = impactReal;
	}

	/**
	 * @param impacts the impacts to set
	 */
	public void setImpacts(List<IValue> impacts) {
		if (impactMapper != null)
			impactMapper = null;
		this.impacts = impacts;
	}

	/**
	 * setLikelihood: <br>
	 * Sets the field "likelihood" with a value
	 * 
	 * @param likelihood The value to set "likelihood"
	 */
	public void setLikelihood(IValue likelihood) {
		this.likelihood = likelihood;
	}

	/**
	 * setLikelihoodReal: <br>
	 * Sets the field "likelihood" with a value
	 * 
	 * @param likelihood The value to set "likelihood"
	 * @throws TrickException
	 */
	public void setLikelihoodReal(double likelihood) throws TrickException {
		if (likelihood < 0)
			throw new TrickException("error.assessment.likelihood", "Probabilty value cannot be negative");
		this.likelihoodReal = likelihood;
	}

	/**
	 * @param owner the owner to set
	 */
	public void setOwner(String owner) {
		this.owner = owner;
	}

	/**
	 * setScenario: <br>
	 * Sets the field "scenario" with a value
	 * 
	 * @param scenario The Scenario object to set
	 * @throws TrickException
	 */
	public void setScenario(Scenario scenario) throws TrickException {
		if (scenario == null)
			throw new TrickException("error.assessment.scenario", "Scenario cannot be empty");
		this.scenario = scenario;
	}

	/**
	 * setSelected: <br>
	 * Sets the Field "selected" with a value.
	 * 
	 * @param selected The Value to set the selected field
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	/**
	 * setUncertainty: <br>
	 * Sets the field "uncertainty" with a value
	 * 
	 * @param uncertainty The value to set "uncertainty"
	 * @throws TrickException
	 */
	public void setUncertainty(double uncertainty) throws TrickException {
		if (uncertainty <= 1.0)
			throw new TrickException("error.assessment.uncertainty", "Uncertainty value should be greater than 1");
		this.uncertainty = uncertainty;
	}

	protected String checkImpact(String impact) {
		return impact == null ? "0" : impact.toLowerCase();
	}

	/**
	 * @return the impactMapper
	 */
	protected synchronized Map<String, IValue> getImpactMapper() {
		if (impacts == null || impacts.isEmpty())
			return Collections.emptyMap();
		if (impactMapper == null)
			setImpactMapper(impacts.stream().collect(Collectors.toMap(IValue::getName, Function.identity())));
		return impactMapper;
	}

	/**
	 * @param impactMapper the impactMapper to set
	 */
	protected void setImpactMapper(Map<String, IValue> impactMapper) {
		this.impactMapper = impactMapper;
	}

	@Transient
	public static String key(Asset asset, Scenario scenario) {
		return key(asset.getId(), scenario.getId());
	}

	@Transient
	public static String keyName(Asset asset, Scenario scenario) {
		return keyName(asset.getName(), scenario.getName());
	}

	@Transient
	public static String key(int assetId, int scenarioId) {
		return assetId + "^-'ASSESSMENT'-^" + scenarioId;
	}

	@Transient
	public static String keyName(String assetName, String scenarioName) {
		return assetName + "^NAME-'ASSESSMENT'-NAME^" + scenarioName;
	}

}