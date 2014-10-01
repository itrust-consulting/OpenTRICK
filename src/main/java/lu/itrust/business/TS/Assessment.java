package lu.itrust.business.TS;

import java.io.Serializable;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import lu.itrust.business.exception.TrickException;

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
@Table(uniqueConstraints=@UniqueConstraint(columnNames = { "fiAsset", "fiScenario" }))
public class Assessment implements Serializable, Cloneable {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** serialVersionUID */
	@Transient
	private static final long serialVersionUID = 1L;

	/** identifier from the database */
	@Id @GeneratedValue 
	@Column(name="idAssessment")
	private int id = -1;

	/** Assessment selected flag */
	@Column(name="dtSelected", nullable=false, columnDefinition="TINYINT(1)")
	private boolean selected = false;

	/** A comment on this assessment */
	@Column(name="dtComment", columnDefinition="LONGTEXT", nullable=false)
	private String comment = "";

	/** hidden assessment comment */
	@Column(name="dtHiddenComment", columnDefinition="LONGTEXT", nullable=false)
	private String hiddenComment = "";

	/** The impactFin value of this assessment */
	@Column(name="dtImpactRep", nullable=false)
	private String impactRep = "0";

	/** The impactOp value of this assessment */
	@Column(name="dtImpactOp", nullable=false)
	private String impactOp = "0";

	/** The impactLeg value of this assessment */
	@Column(name="dtImpactLeg", nullable=false)
	private String impactLeg = "0";

	/** The impactFin value of this assessment */
	@Column(name="dtImpactFin", nullable=false)
	private String impactFin = "0";

	/** The impactFin value of this assessment */
	@Column(name="dtImpactReal", nullable=false)
	private double impactReal = 0;

	/** The likelihood value of this assessment */
	@Column(name="dtLikelihood", nullable=false)
	private String likelihood = "0";

	/** The likelihood value of this assessment */
	@Column(name="dtLikelihoodReal", nullable=false)
	private double likelihoodReal = 0;

	/** The uncertainty value of this assessment */
	@Column(name="dtUncertainty", nullable=false)
	private double uncertainty = 2; // 1 + 1e-7;

	/** The Annual Loss Expectancy - Pessimistic */
	@Column(name="dtALEP", nullable=false)
	private double ALEP = 0;

	/** The Annual Loss Expectancy - Normal */
	@Column(name="dtALE", nullable=false)
	private double ALE = 0;

	/** The Annual Loss Expectancy - Optimistic */
	@Column(name="dtALEO", nullable=false)
	private double ALEO = 0;

	/** The asset object reference */
	@ManyToOne
	@JoinColumn(name="fiAsset", nullable=false)
	@Access(AccessType.FIELD)
	private Asset asset = null;

	/** The scenario object reference */
	@ManyToOne
	@JoinColumn(name="fiScenario", nullable=false)
	@Access(AccessType.FIELD)
	private Scenario scenario = null;

	/***********************************************************************************************
	 * Getters and Setters
	 * 
	 * @throws TrickException
	 **********************************************************************************************/

	public Assessment(Asset asset, Scenario scenario) throws TrickException {
		setAsset(asset);
		setScenario(scenario);
		setSelected(asset.isSelected() && scenario.isSelected() && scenario.hasInfluenceOnAsset(asset.getAssetType()));
	}

	public Assessment() {
	}

	public boolean isCSSF() {
		return !((impactLeg == null || impactLeg.trim().equals("0") || impactLeg.trim().equals("0.0"))
				&& (impactOp == null || impactOp.trim().equals("0") || impactOp.trim().equals("0.0")) && (impactRep == null || impactRep.trim().equals("0") || impactRep.trim()
				.equals("0.0")));
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

	/**
	 * setId: <br>
	 * Sets the field "id" with a value
	 * 
	 * @param id
	 *            The value to set "id"
	 * @throws TrickException
	 */
	public void setId(int id) throws TrickException {
		if (id < 1)
			throw new TrickException("error.assessment.id", "Assessment id should be greater than 0");
		this.id = id;
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
	 * setComment: <br>
	 * Sets the field "comment" with a value
	 * 
	 * @param comment
	 *            The value to set "comment"
	 */
	public void setComment(String comment) {
		this.comment = comment;
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
	 * setHiddenComment: <br>
	 * Sets the Field "hiddenComment" with a value.
	 * 
	 * @param hideComment
	 *            The Value to set the hiddenComment field
	 */
	public void setHiddenComment(String hiddenComment) {
		this.hiddenComment = hiddenComment;
	}

	/**
	 * getImpactRep: <br>
	 * Returns the field "impactRep"
	 * 
	 * @return The impactRep value
	 */
	public String getImpactRep() {
		return impactRep;
	}

	/**
	 * setImpactRep: <br>
	 * Sets the field "impactRep" with a value
	 * 
	 * @param impactRep
	 *            The value to set "impactRep"
	 */
	public void setImpactRep(String impactRep) {
		this.impactRep = checkImpact(impactRep);
	}

	/**
	 * getImpactOp: <br>
	 * Returns the field "impactOp"
	 * 
	 * @return The impactOp value
	 */
	public String getImpactOp() {
		return impactOp;
	}

	/**
	 * setImpactOp: <br>
	 * Sets the field "ImpactOp" with a value
	 * 
	 * @param impactOp
	 *            The value to set "impactOp"
	 */
	public void setImpactOp(String impactOp) {
		this.impactOp = checkImpact(impactOp);
	}

	/**
	 * getImpactLeg: <br>
	 * Returns the field "impactLeg"
	 * 
	 * @return The impactLeg value
	 */
	public String getImpactLeg() {
		return impactLeg;
	}

	/**
	 * setImpactLeg: <br>
	 * Sets the field "impactLeg" with a value
	 * 
	 * @param impactLeg
	 *            The value to set "impactLeg"
	 */
	public void setImpactLeg(String impactLeg) {
		this.impactLeg = checkImpact(impactLeg);
	}

	/**
	 * getImpactFin: <br>
	 * Returns the field "impactFin"
	 * 
	 * @return The Impact value
	 */
	public String getImpactFin() {
		return impactFin;
	}

	protected String checkImpact(String impact) {
		/*
		 * if (impact == null) throw new
		 * IllegalArgumentException("Impact value is null"); else if
		 * (impact.trim().isEmpty()) impact = "0"; else if
		 * (!impact.matches(Constant.REGEXP_VALID_IMPACT)) throw new
		 * IllegalArgumentException(
		 * "Impact does not meet the regular expression: " +
		 * Constant.REGEXP_VALID_IMPACT);
		 */
		return impact.toLowerCase();
	}

	/**
	 * setImpact: <br>
	 * Sets the field "impactFin" with a value
	 * 
	 * @param impactFin
	 *            The value to set "impactFin"
	 */
	public void setImpactFin(String impact) {

		this.impactFin = checkImpact(impact);
	}

	public double getImpactReal() {
		return impactReal;
	}

	public void setImpactReal(double impactReal) throws TrickException {
		if (impactReal < 0)
			throw new TrickException("error.assessment.impact_value", "Impact value cannot be negative");
		this.impactReal = impactReal;
	}

	/**
	 * getLikelihood: <br>
	 * Returns the field "likelihood"
	 * 
	 * @return The Likelihood value
	 */
	public String getLikelihood() {
		return likelihood;
	}

	/**
	 * setLikelihood: <br>
	 * Sets the field "likelihood" with a value
	 * 
	 * @param likelihood
	 *            The value to set "likelihood"
	 */
	public void setLikelihood(String likelihood) {
		/*
		 * if ((likelihood == null) ||
		 * (!likelihood.matches(Constant.REGEXP_VALID_LIKELIHOOD))) { throw new
		 * IllegalArgumentException(
		 * "Likelihood value is null or it does not meet the regular expression: "
		 * + Constant.REGEXP_VALID_LIKELIHOOD); }
		 */
		this.likelihood = likelihood;
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
	 * setLikelihoodReal: <br>
	 * Sets the field "likelihood" with a value
	 * 
	 * @param likelihood
	 *            The value to set "likelihood"
	 * @throws TrickException
	 */
	public void setLikelihoodReal(double likelihood) throws TrickException {
		if (likelihood < 0)
			throw new TrickException("error.assessment.likelihood", "Probabilty value cannot be negative");
		this.likelihoodReal = likelihood;
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
	 * setUncertainty: <br>
	 * Sets the field "uncertainty" with a value
	 * 
	 * @param uncertainty
	 *            The value to set "uncertainty"
	 * @throws TrickException
	 */
	public void setUncertainty(double uncertainty) throws TrickException {
		if (uncertainty <= 1.0)
			throw new TrickException("error.assessment.uncertainty", "Uncertainty value should be greater than 1");
		this.uncertainty = uncertainty;
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
	 * setALEP: <br>
	 * Sets the field "ALEP" with a value
	 * 
	 * @param ALEP
	 *            The value to set "ALEP"
	 * @throws TrickException
	 */
	public void setALEP(double ALEP) throws TrickException {
		if (ALEP < 0)
			throw new TrickException("error.assessment.alep", "ALEP value cannot be negative");
		this.ALEP = ALEP;
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
	 * setALE: <br>
	 * Sets the field "ALE" with a value
	 * 
	 * @param ALE
	 *            The value to set "ALE"
	 * @throws TrickException
	 */
	public void setALE(double ALE) throws TrickException {
		if (ALE < 0)
			throw new TrickException("error.assessment.ale", "ALE value cannot be negative");
		this.ALE = ALE;
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
	 * setALEO: <br>
	 * Sets the field "ALEO" with a value
	 * 
	 * @param ALEO
	 *            The value to set "ALEO"
	 * @throws TrickException
	 */
	public void setALEO(double ALEO) throws TrickException {
		if (ALEO < 0)
			throw new TrickException("error.assessment.aleo", "ALEO value cannot be negative");
		this.ALEO = ALEO;
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
	 * setAsset: <br>
	 * Sets the field "asset" with a value
	 * 
	 * @param asset
	 *            The Asset object to set
	 * @throws TrickException
	 */
	public void setAsset(Asset asset) throws TrickException {
		if (asset == null)
			throw new TrickException("error.assessment.asset", "Asset cannot be empty");
		this.asset = asset;
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
	 * setScenario: <br>
	 * Sets the field "scenario" with a value
	 * 
	 * @param scenario
	 *            The Scenario object to set
	 * @throws TrickException
	 */
	public void setScenario(Scenario scenario) throws TrickException {
		if (scenario == null)
			throw new TrickException("error.assessment.scenario", "Scenario cannot be empty");
		this.scenario = scenario;
	}

	/**
	 * isUsable: <br>
	 * Checks if the Asset and the Scenario are Selected and ALE is not 0.
	 * Checks if this Assessment can be used for Action Plan calculation.
	 * 
	 * @return True: if the Assessment is usable; False: if Assessment is not
	 *         usable
	 */
	public boolean isUsable() {
		return (this.getAsset() == null) || (this.getScenario() == null) ? false : (this.isSelected() && this.getAsset().isSelected() && this.getScenario().isSelected() && this
				.getALE() > 0);
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

	/**
	 * equals: <br>
	 * Checks this Assessment to other to find out if they are equal or not.
	 * Equal means: ID, Asset and Scenario are equal.<br>
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
		if (asset == null) {
			if (other.asset != null) {
				return false;
			}
		} else if (!asset.equals(other.asset)) {
			return false;
		}
		if (Double.doubleToLongBits(id) != Double.doubleToLongBits(other.id)) {
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

	public Assessment duplicate() throws CloneNotSupportedException {
		Assessment assessment = (Assessment) super.clone();
		assessment.id = -1;
		return assessment;
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
	 * setSelected: <br>
	 * Sets the Field "selected" with a value.
	 * 
	 * @param selected
	 *            The Value to set the selected field
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}