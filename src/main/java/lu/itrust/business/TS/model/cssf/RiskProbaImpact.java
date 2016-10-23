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
import lu.itrust.business.TS.model.parameter.ILevelParameter;
import lu.itrust.business.TS.model.parameter.IParameter;
import lu.itrust.business.TS.model.parameter.impl.ImpactParameter;
import lu.itrust.business.TS.model.parameter.impl.LikelihoodParameter;

/**
 * @author eomar
 *
 */
@Embeddable
public class RiskProbaImpact implements Cloneable {

	@ManyToOne
	@Cascade(CascadeType.SAVE_UPDATE)
	private LikelihoodParameter probability;

	@ManyToOne
	@Cascade(CascadeType.SAVE_UPDATE)
	private ImpactParameter impactRep;

	@ManyToOne
	@Cascade(CascadeType.SAVE_UPDATE)
	private ImpactParameter impactOp;

	@ManyToOne
	@Cascade(CascadeType.SAVE_UPDATE)
	private ImpactParameter impactLeg;

	@ManyToOne
	@Cascade(CascadeType.SAVE_UPDATE)
	private ImpactParameter impactFin;

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
	public RiskProbaImpact(LikelihoodParameter probability, ImpactParameter impactFin, ImpactParameter impactLeg, ImpactParameter impactOp, ImpactParameter impactRep) {
		this.probability = probability;
		this.impactFin = impactFin;
		this.impactLeg = impactLeg;
		this.impactOp = impactOp;
		this.impactRep = impactRep;
	}

	/**
	 * @return the probability
	 */
	public ILevelParameter getProbability() {
		return probability;
	}

	/**
	 * @param probability
	 *            the probability to set
	 */
	public void setProbability(LikelihoodParameter probabitity) {
		this.probability = probabitity;
	}

	/**
	 * @return the impactRep
	 */
	public ILevelParameter getImpactRep() {
		return impactRep;
	}

	/**
	 * @param impactRep
	 *            the impactRep to set
	 */
	public void setImpactRep(ImpactParameter impactRep) {
		this.impactRep = impactRep;
	}

	/**
	 * @return the impactOp
	 */
	public ILevelParameter getImpactOp() {
		return impactOp;
	}

	/**
	 * @param impactOp
	 *            the impactOp to set
	 */
	public void setImpactOp(ImpactParameter impactOp) {
		this.impactOp = impactOp;
	}

	/**
	 * @return the impactLeg
	 */
	public ILevelParameter getImpactLeg() {
		return impactLeg;
	}

	/**
	 * @param impactLeg
	 *            the impactLeg to set
	 */
	public void setImpactLeg(ImpactParameter impactLeg) {
		this.impactLeg = impactLeg;
	}

	/**
	 * @return the impactFin
	 */
	public ILevelParameter getImpactFin() {
		return impactFin;
	}

	/**
	 * @param impactFin
	 *            the impactFin to set
	 */
	public void setImpactFin(ImpactParameter impactFin) {
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
			probaImpact.probability = (LikelihoodParameter) probability.clone();
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
	 *            Map< Acronym, SimpleParameter >
	 * @return copy
	 * @throws CloneNotSupportedException
	 */
	public RiskProbaImpact duplicate(Map<String, IParameter> parameters) throws CloneNotSupportedException {
		RiskProbaImpact probaImpact = (RiskProbaImpact) super.clone();
		probaImpact.updateData(parameters);
		return probaImpact;
	}

	/**
	 * Replace parameters
	 * 
	 * @param parameters
	 *            Map< Acronym, SimpleParameter >
	 */
	public void updateData(Map<String, IParameter> parameters) {
		if (probability != null)
			probability = (LikelihoodParameter) parameters.get(probability.getKey());
		if (impactFin != null)
			impactFin = (ImpactParameter) parameters.get(impactFin.getKey());
		if (impactRep != null)
			impactRep = (ImpactParameter) parameters.get(ImpactParameter.key(Constant.PARAMETERTYPE_TYPE_IMPACT_REP_NAME, impactRep.getAcronym()));
		if (impactOp != null)
			impactOp = (ImpactParameter) parameters.get(ImpactParameter.key(Constant.PARAMETERTYPE_TYPE_IMPACT_OPE_NAME, impactOp.getAcronym()));
		if (impactLeg != null)
			impactLeg = (ImpactParameter) parameters.get(ImpactParameter.key(Constant.PARAMETERTYPE_TYPE_IMPACT_LEG_NAME, impactLeg.getAcronym()));
	}

	protected ILevelParameter getValueOrDefault(ILevelParameter value, ILevelParameter defaultValue) {
		return value == null ? defaultValue : value;
	}

	public ILevelParameter getImpactFin(ILevelParameter defaultImpact) {
		return getValueOrDefault(impactFin, defaultImpact);
	}

	public ILevelParameter getImpactOp(ILevelParameter defaultImpact) {
		return getValueOrDefault(impactOp, defaultImpact);
	}

	public ILevelParameter getImpactLeg(ILevelParameter defaultImpact) {
		return getValueOrDefault(impactLeg, defaultImpact);
	}

	public ILevelParameter getImpactRep(ILevelParameter defaultImpact) {
		return getValueOrDefault(impactRep, defaultImpact);
	}

	public ILevelParameter getProbability(ILevelParameter defaultValue) {
		return getValueOrDefault(probability, defaultValue);
	}

}
