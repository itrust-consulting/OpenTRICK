/**
 * 
 */
package lu.itrust.business.TS.model.parameter.helper.value;

import static lu.itrust.business.TS.constants.Constant.PARAMETERTYPE_TYPE_DYNAMIC;
import static lu.itrust.business.TS.constants.Constant.PARAMETERTYPE_TYPE_IMPACT;
import static lu.itrust.business.TS.constants.Constant.PARAMETERTYPE_TYPE_IMPACT_LEG;
import static lu.itrust.business.TS.constants.Constant.PARAMETERTYPE_TYPE_IMPACT_LEG_NAME;
import static lu.itrust.business.TS.constants.Constant.PARAMETERTYPE_TYPE_IMPACT_NAME;
import static lu.itrust.business.TS.constants.Constant.PARAMETERTYPE_TYPE_IMPACT_OPE;
import static lu.itrust.business.TS.constants.Constant.PARAMETERTYPE_TYPE_IMPACT_OPE_NAME;
import static lu.itrust.business.TS.constants.Constant.PARAMETERTYPE_TYPE_IMPACT_REP;
import static lu.itrust.business.TS.constants.Constant.PARAMETERTYPE_TYPE_IMPACT_REP_NAME;
import static lu.itrust.business.TS.constants.Constant.PARAMETERTYPE_TYPE_PROPABILITY;
import static lu.itrust.business.TS.constants.Constant.PARAMETERTYPE_TYPE_PROPABILITY_NAME;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.assessment.Assessment;
import lu.itrust.business.TS.model.parameter.AcronymParameter;
import lu.itrust.business.TS.model.parameter.DynamicParameter;
import lu.itrust.business.TS.model.parameter.ExtendedParameter;
import lu.itrust.business.TS.model.parameter.Parameter;

/**
 * @author eomar
 *
 */
public class ValueFactory {

	private Map<String, DynamicParameter> dynamicMapper;

	private List<DynamicParameter> dynamicParameters;

	private List<ExtendedParameter> financialImpacts;

	private Map<String, ExtendedParameter> financialMapper;

	private List<ExtendedParameter> legalImpacts;

	private Map<String, ExtendedParameter> LegalMapper;

	private List<ExtendedParameter> operationalImpacts;

	private Map<String, ExtendedParameter> operationalMapper;

	private List<ExtendedParameter> probabilities;

	private Map<String, ExtendedParameter> probabilityMapper;

	private List<ExtendedParameter> reputationImpacts;

	private Map<String, ExtendedParameter> reputationMapper;

	public ValueFactory(List<? extends Parameter> parameters) {
		for (Parameter parameter : parameters) {
			if (parameter instanceof DynamicParameter)
				add((DynamicParameter) parameter);
			else if (parameter instanceof ExtendedParameter)
				add((ExtendedParameter) parameter);
		}
	}

	public IValue findDyn(Object value) {
		if (value == null || dynamicParameters == null)
			return null;
		if (value instanceof Integer) {
			int index = (int) value, last = dynamicParameters.size() - 1;
			return new AcronymValue(dynamicParameters.get(index < 0 ? 0 : index > last ? last : index));
		}

		if (value instanceof String) {
			DynamicParameter parameter = getDynamicMapper().get(value.toString());
			if (parameter != null)
				return new AcronymValue(parameter);
		}
		Double doubleValue = (value instanceof Double) ? (Double) value : ToDouble(value.toString(), null);
		if (doubleValue == null)
			return null;
		return findDynamicByValue(doubleValue, dynamicParameters);
	}

	public Integer findDynLevel(Object value) {
		IValue iValue = findDyn(value);
		return iValue == null ? 0 : iValue.getLevel();
	}

	public Double findDynValue(Object value) {
		IValue iValue = findDyn(value);
		return iValue == null ? 0D : iValue.getReal();
	}

	public IValue findExp(Object value) {
		IValue iValue = findProb(value);
		if (iValue == null)
			iValue = findDyn(value);
		return iValue;
	}

	public Integer findExpLevel(Object value) {
		IValue iValue = findExp(value);
		return iValue == null ? 0 : iValue.getLevel();
	}

	public Double findExpValue(Object value) {
		IValue iValue = findExp(value);
		return iValue == null ? 0D : iValue.getReal();
	}

	public IValue findImpactFin(Object value) {
		return findValue(value, financialImpacts, PARAMETERTYPE_TYPE_IMPACT);
	}

	public Integer findImpactFinLevel(Object value) {
		IValue impact = findImpactFin(value);
		return impact == null ? 0 : impact.getLevel();
	}

	public ExtendedParameter findImpactFinParameter(Object value) {
		IValue impact = findImpactFin(value);
		return impact == null ? null : (ExtendedParameter) impact.getParameter();
	}

	public Double findImpactFinValue(Object value) {
		IValue impact = findImpactFin(value);
		return impact == null ? 0D : impact.getReal();
	}

	public IValue findImpactLeg(Object value) {
		return findValue(value, legalImpacts, PARAMETERTYPE_TYPE_IMPACT_LEG);
	}

	public Integer findImpactLegLevel(Object value) {
		IValue impact = findImpactFin(value);
		return impact == null ? 0 : impact.getLevel();
	}

	public ExtendedParameter findImpactLegParameter(Object value) {
		IValue impact = findImpactLeg(value);
		return impact == null ? null : (ExtendedParameter) impact.getParameter();
	}

	public Double findImpactLegValue(Object value) {
		IValue impact = findImpactLeg(value);
		return impact == null ? 0D : impact.getReal();
	}

	public IValue findImpactOp(Object value) {
		return findValue(value, operationalImpacts, PARAMETERTYPE_TYPE_IMPACT_OPE);
	}

	public Integer findImpactOpeLevel(Object value) {
		IValue impact = findImpactOp(value);
		return impact == null ? 0 : impact.getLevel();
	}

	public ExtendedParameter findImpactOpParameter(Object value) {
		IValue impact = findImpactOp(value);
		return impact == null ? null : (ExtendedParameter) impact.getParameter();
	}

	public Double findImpactOpValue(Object value) {
		IValue impact = findImpactOp(value);
		return impact == null ? 0D : impact.getReal();
	}

	public IValue findImpactRep(Object value) {
		return findValue(value, reputationImpacts, PARAMETERTYPE_TYPE_IMPACT_REP);
	}

	public Integer findImpactRepLevel(Object value) {
		IValue impact = findImpactRep(value);
		return impact == null ? 0 : impact.getLevel();
	}

	public ExtendedParameter findImpactRepParameter(Object value) {
		IValue impact = findImpactRep(value);
		return impact == null ? null : (ExtendedParameter) impact.getParameter();
	}

	public Double findImpactRepValue(Object value) {
		IValue impact = findImpactRep(value);
		return impact == null ? 0D : impact.getReal();
	}

	public IValue findProb(Object value) {
		return findValue(value, probabilities, PARAMETERTYPE_TYPE_PROPABILITY);
	}

	public Integer findProbLevel(Object value) {
		IValue impact = findProb(value);
		return impact == null ? 0 : impact.getLevel();
	}

	public ExtendedParameter findProbParameter(Object value) {
		IValue impact = findProb(value);
		return impact == null ? null : (ExtendedParameter) impact.getParameter();
	}

	public Double findProbValue(Object value) {
		IValue impact = findProb(value);
		return impact == null ? 0D : impact.getReal();
	}

	/**
	 * @return the dynamicMapper
	 */
	public Map<String, DynamicParameter> getDynamicMapper() {
		if (dynamicMapper == null) {
			if (dynamicParameters == null)
				return null;
			setDynamicMapper(dynamicParameters.stream().collect(Collectors.toMap(DynamicParameter::getAcronym, Function.identity())));
		}
		return Collections.unmodifiableMap(dynamicMapper);
	}

	/**
	 * @return the dynamicParameters
	 */
	public List<DynamicParameter> getDynamicParameters() {
		return dynamicParameters == null ? dynamicParameters : Collections.unmodifiableList(dynamicParameters);
	}

	/**
	 * @return the financialImpacts
	 */
	public List<ExtendedParameter> getFinancialImpacts() {
		return financialImpacts == null ? financialImpacts : Collections.unmodifiableList(financialImpacts);
	}

	/**
	 * @return the financialMapper
	 */
	public Map<String, ExtendedParameter> getFinancialMapper() {
		if (financialMapper == null) {
			if (financialImpacts == null)
				return null;
			setFinancialMapper(financialImpacts.stream().collect(Collectors.toMap(ExtendedParameter::getAcronym, Function.identity())));
		}
		return Collections.unmodifiableMap(financialMapper);
	}

	/**
	 * @return the legalImpacts
	 */
	public List<ExtendedParameter> getLegalImpacts() {
		return legalImpacts == null ? legalImpacts : Collections.unmodifiableList(legalImpacts);
	}

	/**
	 * @return the legalMapper
	 */
	public Map<String, ExtendedParameter> getLegalMapper() {
		if (LegalMapper == null) {
			if (legalImpacts == null)
				return null;
			setLegalMapper(legalImpacts.stream().collect(Collectors.toMap(ExtendedParameter::getAcronym, Function.identity())));
		}
		return Collections.unmodifiableMap(LegalMapper);
	}

	/**
	 * @return the operationalImpacts
	 */
	public List<ExtendedParameter> getOperationalImpacts() {
		return operationalImpacts == null ? operationalImpacts : Collections.unmodifiableList(operationalImpacts);
	}

	/**
	 * @return the operationalMapper
	 */
	public Map<String, ExtendedParameter> getOperationalMapper() {
		if (operationalMapper == null) {
			if (operationalImpacts == null)
				return null;
			setOperationalMapper(operationalImpacts.stream().collect(Collectors.toMap(ExtendedParameter::getAcronym, Function.identity())));
		}
		return Collections.unmodifiableMap(operationalMapper);
	}

	/**
	 * @return the probabilities
	 */
	public List<ExtendedParameter> getProbabilities() {
		return probabilities == null ? probabilities : Collections.unmodifiableList(probabilities);
	}

	/**
	 * @return the probabilityMapper
	 */
	public Map<String, ExtendedParameter> getProbabilityMapper() {
		if (probabilityMapper == null) {
			if (probabilities == null)
				return null;
			setProbabilityMapper(probabilities.stream().collect(Collectors.toMap(ExtendedParameter::getAcronym, Function.identity())));
		}
		return Collections.unmodifiableMap(probabilityMapper);
	}

	/**
	 * @return the reputationImpacts
	 */
	public List<ExtendedParameter> getReputationImpacts() {
		return reputationImpacts == null ? reputationImpacts : Collections.unmodifiableList(reputationImpacts);
	}

	/**
	 * @return the reputationMapper
	 */
	public Map<String, ExtendedParameter> getReputationMapper() {
		if (reputationMapper == null) {
			if (reputationImpacts == null)
				return null;
			setReputationMapper(reputationImpacts.stream().collect(Collectors.toMap(ExtendedParameter::getAcronym, Function.identity())));
		}
		return Collections.unmodifiableMap(reputationMapper);
	}

	/**
	 * @param operationalImpacts
	 *            the operationalImpacts to set
	 */
	public void setOperationalImpacts(List<ExtendedParameter> operationalImpacts) {
		this.operationalImpacts = operationalImpacts;
	}

	/**
	 * @param reputationImpacts
	 *            the reputationImpacts to set
	 */
	public void setReputationImpacts(List<ExtendedParameter> reputationImpacts) {
		this.reputationImpacts = reputationImpacts;
	}

	/**
	 * @param dynamicMapper
	 *            the dynamicMapper to set
	 */
	protected void setDynamicMapper(Map<String, DynamicParameter> dynamicMapper) {
		this.dynamicMapper = dynamicMapper;
	}

	/**
	 * @param dynamicParameters
	 *            the dynamicParameters to set
	 */
	protected void setDynamicParameters(List<DynamicParameter> dynamicParameters) {
		this.dynamicParameters = dynamicParameters;
	}

	/**
	 * @param financialImpacts
	 *            the financialImpacts to set
	 */
	protected void setFinancialImpacts(List<ExtendedParameter> financialImpacts) {
		this.financialImpacts = financialImpacts;
	}

	/**
	 * @param financialMapper
	 *            the financialMapper to set
	 */
	protected void setFinancialMapper(Map<String, ExtendedParameter> financialMapper) {
		this.financialMapper = financialMapper;
	}

	/**
	 * @param legalImpacts
	 *            the legalImpacts to set
	 */
	protected void setLegalImpacts(List<ExtendedParameter> legalImpacts) {
		this.legalImpacts = legalImpacts;
	}

	/**
	 * @param legalMapper
	 *            the legalMapper to set
	 */
	protected void setLegalMapper(Map<String, ExtendedParameter> legalMapper) {
		LegalMapper = legalMapper;
	}

	/**
	 * @param operationalMapper
	 *            the operationalMapper to set
	 */
	protected void setOperationalMapper(Map<String, ExtendedParameter> operationalMapper) {
		this.operationalMapper = operationalMapper;
	}

	/**
	 * @param probabilities
	 *            the probabilities to set
	 */
	protected void setProbabilities(List<ExtendedParameter> probabilities) {
		this.probabilities = probabilities;
	}

	/**
	 * @param probabilityMapper
	 *            the probabilityMapper to set
	 */
	protected void setProbabilityMapper(Map<String, ExtendedParameter> probabilityMapper) {
		this.probabilityMapper = probabilityMapper;
	}

	/**
	 * @param reputationMapper
	 *            the reputationMapper to set
	 */
	protected void setReputationMapper(Map<String, ExtendedParameter> reputationMapper) {
		this.reputationMapper = reputationMapper;
	}

	private void add(DynamicParameter parameter) {
		if (dynamicParameters == null)
			setDynamicParameters(new ArrayList<>());
		synchronized (dynamicParameters) {
			parameter.setLevel(dynamicParameters.size());
			dynamicParameters.add(parameter);
		}
	}

	private void add(ExtendedParameter parameter) {
		switch (parameter.getType().getLabel()) {
		case PARAMETERTYPE_TYPE_IMPACT_NAME:
			if (financialImpacts == null)
				setFinancialImpacts(new ArrayList<>(11));
			financialImpacts.add(parameter);
			break;
		case PARAMETERTYPE_TYPE_PROPABILITY_NAME:
			if (probabilities == null)
				setProbabilities(new ArrayList<>(11));
			probabilities.add(parameter);
			break;
		case PARAMETERTYPE_TYPE_IMPACT_REP_NAME:
			if (reputationImpacts == null)
				setReputationImpacts(new ArrayList<>(11));
			reputationImpacts.add(parameter);
			break;
		case PARAMETERTYPE_TYPE_IMPACT_LEG_NAME:
			if (legalImpacts == null)
				setLegalImpacts(new ArrayList<>(11));
			legalImpacts.add(parameter);
			break;
		case PARAMETERTYPE_TYPE_IMPACT_OPE_NAME:
			if (operationalImpacts == null)
				setOperationalImpacts(new ArrayList<>(11));
			operationalImpacts.add(parameter);
			break;
		}
	}

	private IValue findByLevel(Integer level, List<ExtendedParameter> parameters) {
		int mid = parameters.size() / 2;
		AcronymParameter parameter = parameters.get(mid);
		if (parameter.getLevel() == level)
			return new AcronymValue(parameter);
		else if (mid == 0)
			return new DefaultLevelValue(level, parameter);
		else if (parameter.getLevel() > level)
			return findByLevel(level, parameters.subList(0, mid));
		else
			return findByLevel(level, parameters.subList(mid, parameters.size()));
	}

	private IValue findByValue(Double value, List<ExtendedParameter> parameters) {
		int mid = parameters.size() / 2;
		ExtendedParameter parameter = parameters.get(mid);
		if (parameter.getBounds().isInRange(value))
			return value == parameter.getValue() ? new AcronymValue(parameter) : new DefaultRealValue(value, parameter);
		else if (mid == 0)
			return new DefaultRealValue(value, parameter);
		else if (parameter.getBounds().getFrom() > value)
			return findByValue(value, parameters.subList(0, mid));
		else
			return findByValue(value, parameters.subList(mid, parameters.size()));
	}

	private IValue findDynamicByValue(Double doubleValue, List<DynamicParameter> dynamicParameters) {
		DynamicParameter minParameter = dynamicParameters.get(0), midParameter = dynamicParameters.get(dynamicParameters.size() / 2),
				maxParameter = dynamicParameters.get(dynamicParameters.size() - 1);
		if (minParameter.getValue() == doubleValue)
			return new AcronymValue(minParameter);
		else if (midParameter.getValue() == doubleValue)
			return new AcronymValue(midParameter);
		else if (maxParameter.getValue() == doubleValue)
			return new AcronymValue(maxParameter);
		else if (doubleValue < minParameter.getValue())
			return new DefaultRealValue(doubleValue, minParameter);
		else if (doubleValue > maxParameter.getValue() || dynamicParameters.size() < 2)
			return new DefaultRealValue(doubleValue, maxParameter);
		else if (doubleValue > midParameter.getValue())
			return findDynamicByValue(doubleValue, dynamicParameters.subList(dynamicParameters.size() / 2, dynamicParameters.size()));
		else
			return findDynamicByValue(doubleValue, dynamicParameters.subList(0, dynamicParameters.size() / 2));
	}

	private IValue findValue(Object value, List<ExtendedParameter> extendedParameters, int type) {
		if (value == null || extendedParameters == null)
			return null;
		if (value instanceof Integer)
			return findByLevel((Integer) value, extendedParameters);
		if (value instanceof String) {
			ExtendedParameter parameter = (ExtendedParameter) getParameterMapper(type).get(value.toString());
			if (parameter != null)
				return new AcronymValue(parameter);
		}
		Double doubleValue = (value instanceof Double) ? (Double) value : ToDouble(value.toString(), null);
		if (doubleValue == null)
			return null;
		return findByValue(doubleValue, extendedParameters);
	}

	private Map<String, ? extends AcronymParameter> getParameterMapper(int type) {
		switch (type) {
		case PARAMETERTYPE_TYPE_IMPACT:
			return getFinancialMapper();
		case PARAMETERTYPE_TYPE_PROPABILITY:
			return getProbabilityMapper();
		case PARAMETERTYPE_TYPE_IMPACT_REP:
			return getReputationMapper();
		case PARAMETERTYPE_TYPE_IMPACT_LEG:
			return getLegalMapper();
		case PARAMETERTYPE_TYPE_IMPACT_OPE:
			return getOperationalMapper();
		case PARAMETERTYPE_TYPE_DYNAMIC:
			return getDynamicMapper();
		default:
			throw new TrickException("error.parameter.type.not.supported", "Parameter type does not supported");
		}
	}

	private Double ToDouble(String value, Double defaultValue) {
		try {
			return Double.parseDouble(value);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	/**
	 * @param assessment
	 * @return importance
	 * @see ValueFactory#findImportance(String, String, String, String, String)
	 */
	public int findImportance(Assessment assessment) {
		return findImportance(assessment.getLikelihood(), assessment.getImpactFin(), assessment.getImpactLeg(), assessment.getImpactOp(), assessment.getImpactRep());
	}

	/**
	 * Compute importance
	 * probaLvl * MaxLevel(impacts)
	 * @param proba
	 * @param impactFin
	 * @param impactLeg
	 * @param impactOp
	 * @param impactRep
	 * @return importance
	 * @see IValue#maxByLevel(IValue, IValue)
	 */
	public int findImportance(String proba, String impactFin, String impactLeg, String impactOp, String impactRep) {
		IValue impact = findMaxImpactByLevel(impactFin, impactLeg, impactOp, impactRep);
		return impact == null ? 0 : impact.getLevel() * findExpLevel(proba);
	}

	public IValue findMaxImpactByLevel(String impactFin, String impactLeg, String impactOp, String impactRep) {
		return IValue.maxByLevel(findImpactFin(impactFin), IValue.maxByLevel(findImpactLeg(impactLeg), IValue.maxByLevel(findImpactOp(impactOp), findImpactRep(impactRep))));
	}

	public IValue findMaxImpactByLevel(Object value) {
		return IValue.maxByLevel(findImpactFin(value), IValue.maxByLevel(findImpactLeg(value), IValue.maxByLevel(findImpactOp(value), findImpactRep(value))));
	}
	
	public IValue findMinImpactByLevel(Object value) {
		return IValue.minByLevel(findImpactFin(value), IValue.minByLevel(findImpactLeg(value), IValue.minByLevel(findImpactOp(value), findImpactRep(value))));
	}

	public int findImpactLevelByMaxLevel(double value) {
		IValue iValue = findMaxImpactByLevel(value);
		return iValue == null ? 0 : iValue.getLevel();
	}
	
	public double findRealImpactByMaxLevel(int level) {
		IValue iValue = findMaxImpactByLevel(level);
		return iValue == null ? 0 : iValue.getReal();
	}

}
