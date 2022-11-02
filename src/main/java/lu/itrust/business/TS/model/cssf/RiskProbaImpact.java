/**
 * 
 */
package lu.itrust.business.TS.model.cssf;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import lu.itrust.business.TS.model.parameter.IImpactParameter;
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

	@ManyToMany
	@Cascade(CascadeType.SAVE_UPDATE)
	private List<ImpactParameter> impacts = new LinkedList<>();

	private Integer vulnerability = 1;

	@Transient
	private Map<String, IImpactParameter> impactMapper;

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
	public RiskProbaImpact(LikelihoodParameter probability, ImpactParameter... impacts) {
		this.probability = probability;
		this.impacts = Arrays.asList(impacts);
	}

	public RiskProbaImpact(LikelihoodParameter probability, List<ImpactParameter> impacts) {
		this.probability = probability;
		this.impacts = impacts;
	}

	/**
	 * @return the probability
	 */
	public LikelihoodParameter getProbability() {
		return probability;
	}

	/**
	 * @param probability
	 *                    the probability to set
	 */
	public void setProbability(LikelihoodParameter probabitity) {
		this.probability = probabitity;
	}

	/**
	 * @return the impacts
	 */
	public List<? extends IImpactParameter> getImpacts() {
		return impacts;
	}

	/**
	 * @param impacts
	 *                the impacts to set
	 */
	public void setImpacts(List<ImpactParameter> impacts) {
		this.impacts = impacts;
	}

	public int getVulnerability() {
		return vulnerability == null ? 1 : vulnerability;
	}

	public void setVulnerability(Integer vulnerability) {

		this.vulnerability = vulnerability == null ? 1 : vulnerability;
	}

	/**
	 * @return the impactMapper
	 */
	protected Map<String, IImpactParameter> getImpactMapper() {
		if (impactMapper == null)
			setImpactMapper(
					impacts.stream().collect(Collectors.toMap(ImpactParameter::getTypeName, Function.identity())));
		return impactMapper;
	}

	/**
	 * @param impactMapper
	 *                     the impactMapper to set
	 */
	protected void setImpactMapper(Map<String, IImpactParameter> impactMapper) {
		this.impactMapper = impactMapper;
	}

	/**
	 * @param key
	 * @return
	 * @see java.util.Map#get(java.lang.Object)
	 */
	public IImpactParameter get(String key) {
		return getImpactMapper().get(key);
	}

	public IImpactParameter add(ImpactParameter impact) {
		if (impact == null)
			return null;
		IImpactParameter parameter = getImpactMapper().get(impact.getTypeName());
		if (parameter != null)
			remove(parameter);
		impacts.add(impact);
		return getImpactMapper().put(impact.getTypeName(), impact);
	}

	private void remove(IImpactParameter impact) {
		if (impacts.remove(impact) && impactMapper != null)
			impactMapper.remove(impact.getTypeName());
	}

	public int getImportance() {
		return getImpactLevel() * getProbabilityLevel();
	}

	/**
	 * @return Max of impact level
	 */
	public int getImpactLevel() {
		return impacts.stream().mapToInt(IImpactParameter::getLevel).max().orElse(0);
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
			probaImpact.probability =  probability.clone();
		return probaImpact;
	}

	public RiskProbaImpact duplicate() throws CloneNotSupportedException {
		RiskProbaImpact probaImpact = (RiskProbaImpact) super.clone();
		return probaImpact;
	}

	/**
	 * @param parameters
	 *                   Map< Acronym, SimpleParameter >
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
	 *                   Map< Acronym, SimpleParameter >
	 */
	public void updateData(Map<String, IParameter> parameters) {
		if (probability != null)
			probability = (LikelihoodParameter) parameters.get(probability.getKey());
		this.impactMapper = null;
		setImpacts(this.impacts.stream().map(impact -> (ImpactParameter) parameters.get(impact.getKey()))
				.collect(Collectors.toList()));
	}

	protected ILevelParameter getValueOrDefault(ILevelParameter value, ILevelParameter defaultValue) {
		return value == null ? defaultValue : value;
	}

	public ILevelParameter getProbability(ILevelParameter defaultValue) {
		return getValueOrDefault(probability, defaultValue);
	}

}
