/**
 * 
 */
package lu.itrust.business.TS.model.cssf;

import java.util.Map;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.model.parameter.ExtendedParameter;
import lu.itrust.business.TS.model.parameter.Parameter;

/**
 * @author eomar
 *
 */
@Embeddable
public class RiskProbaImpact implements Cloneable {

	@ManyToOne
	@Cascade(CascadeType.SAVE_UPDATE)
	private ExtendedParameter probability;

	@ManyToOne
	@Cascade(CascadeType.SAVE_UPDATE)
	private ExtendedParameter impactRep;

	@ManyToOne
	@Cascade(CascadeType.SAVE_UPDATE)
	private ExtendedParameter impactOp;

	@ManyToOne
	@Cascade(CascadeType.SAVE_UPDATE)
	private ExtendedParameter impactLeg;

	@ManyToOne
	@Cascade(CascadeType.SAVE_UPDATE)
	private ExtendedParameter impactFin;

	/**
	 * 
	 */
	public RiskProbaImpact() {
	}

	/**
	 * @param probability
	 * @param impactFin
	 * @param impactLeg
	 * @param impactOp
	 * @param impactRep
	 */
	public RiskProbaImpact(ExtendedParameter probability, ExtendedParameter impactFin, ExtendedParameter impactLeg, ExtendedParameter impactOp, ExtendedParameter impactRep) {
		this.probability = probability;
		this.impactFin = impactFin;
		this.impactLeg = impactLeg;
		this.impactOp = impactOp;
		this.impactRep = impactRep;
	}

	/**
	 * @return the probability
	 */
	public ExtendedParameter getProbability() {
		return probability;
	}

	/**
	 * @param probability
	 *            the probability to set
	 */
	public void setProbability(ExtendedParameter probabitity) {
		this.probability = probabitity;
	}

	/**
	 * @return the impactRep
	 */
	public ExtendedParameter getImpactRep() {
		return impactRep;
	}

	/**
	 * @param impactRep
	 *            the impactRep to set
	 */
	public void setImpactRep(ExtendedParameter impactRep) {
		this.impactRep = impactRep;
	}

	/**
	 * @return the impactOp
	 */
	public ExtendedParameter getImpactOp() {
		return impactOp;
	}

	/**
	 * @param impactOp
	 *            the impactOp to set
	 */
	public void setImpactOp(ExtendedParameter impactOp) {
		this.impactOp = impactOp;
	}

	/**
	 * @return the impactLeg
	 */
	public ExtendedParameter getImpactLeg() {
		return impactLeg;
	}

	/**
	 * @param impactLeg
	 *            the impactLeg to set
	 */
	public void setImpactLeg(ExtendedParameter impactLeg) {
		this.impactLeg = impactLeg;
	}

	/**
	 * @return the impactFin
	 */
	public ExtendedParameter getImpactFin() {
		return impactFin;
	}

	/**
	 * @param impactFin
	 *            the impactFin to set
	 */
	public void setImpactFin(ExtendedParameter impactFin) {
		this.impactFin = impactFin;
	}

	public int getImportance() {
		return getImpactLevel() * getProbabilityLevel();
	}

	/**
	 * @return Max of impact level
	 */
	public int getImpactLevel() {
		int max = impactFin == null ? 0 : impactFin.getLevel();
		if (impactRep != null)
			max = Math.max(max, impactRep.getLevel());
		if (impactOp != null)
			max = Math.max(max, impactOp.getLevel());
		if (impactLeg != null)
			max = Math.max(max, impactLeg.getLevel());
		return max;
	}

	public int getProbabilityLevel() {
		return probability == null ? 0 : probability.getLevel();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public RiskProbaImpact clone() throws CloneNotSupportedException {
		RiskProbaImpact probaImpact = (RiskProbaImpact) super.clone();
		if (probability != null)
			probaImpact.probability = probability.clone();
		if (impactFin != null)
			probaImpact.impactFin = impactFin.clone();
		if (impactRep != null)
			probaImpact.impactRep = impactRep.clone();
		if (impactOp != null)
			probaImpact.impactOp = impactOp.clone();
		if (impactLeg != null)
			probaImpact.impactLeg = impactLeg.clone();
		return probaImpact;
	}

	public RiskProbaImpact duplicate() throws CloneNotSupportedException {
		RiskProbaImpact probaImpact = (RiskProbaImpact) super.clone();
		return probaImpact;
	}

	/**
	 * @param parameters
	 *            Map< Acronym, Parameter >
	 * @return copy
	 * @throws CloneNotSupportedException
	 */
	public RiskProbaImpact duplicate(Map<String, Parameter> parameters) throws CloneNotSupportedException {
		RiskProbaImpact probaImpact = (RiskProbaImpact) super.clone();
		probaImpact.updateData(parameters);
		return probaImpact;
	}

	/**
	 * Replace parameters
	 * 
	 * @param parameters
	 *            Map< Acronym, Parameter >
	 */
	public void updateData(Map<String, Parameter> parameters) {
		if (probability != null)
			probability = (ExtendedParameter) parameters.get(probability.getKey());
		if (impactFin != null)
			impactFin = (ExtendedParameter) parameters.get(impactFin.getKey());
		if (impactRep != null)
			impactRep = (ExtendedParameter) parameters.get(ExtendedParameter.key(Constant.PARAMETERTYPE_TYPE_IMPACT_REP_NAME, impactRep.getAcronym()));
		if (impactOp != null)
			impactOp = (ExtendedParameter) parameters.get(ExtendedParameter.key(Constant.PARAMETERTYPE_TYPE_IMPACT_OPE_NAME, impactOp.getAcronym()));
		if (impactLeg != null)
			impactLeg = (ExtendedParameter) parameters.get(ExtendedParameter.key(Constant.PARAMETERTYPE_TYPE_IMPACT_LEG_NAME, impactLeg.getAcronym()));
	}

	protected ExtendedParameter getValueOrDefault(ExtendedParameter value, ExtendedParameter defaultValue) {
		return value == null ? defaultValue : value;
	}

	public ExtendedParameter getImpactFin(ExtendedParameter defaultImpact) {
		return getValueOrDefault(impactFin, defaultImpact);
	}

	public ExtendedParameter getImpactOp(ExtendedParameter defaultImpact) {
		return getValueOrDefault(impactOp, defaultImpact);
	}

	public ExtendedParameter getImpactLeg(ExtendedParameter defaultImpact) {
		return getValueOrDefault(impactLeg, defaultImpact);
	}

	public ExtendedParameter getImpactRep(ExtendedParameter defaultImpact) {
		return getValueOrDefault(impactRep, defaultImpact);
	}

	public ExtendedParameter getProbability(ExtendedParameter defaultValue) {
		return getValueOrDefault(probability, defaultValue);
	}

}
