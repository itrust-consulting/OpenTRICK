/**
 * 
 */
package lu.itrust.business.TS.model.parameter.helper;

import static lu.itrust.business.TS.constants.Constant.PARAMETERTYPE_TYPE_DYNAMIC_NAME;
import static lu.itrust.business.TS.constants.Constant.PARAMETERTYPE_TYPE_PROPABILITY_NAME;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.model.assessment.Assessment;
import lu.itrust.business.TS.model.parameter.IBoundedParameter;
import lu.itrust.business.TS.model.parameter.IImpactParameter;
import lu.itrust.business.TS.model.parameter.ILevelParameter;
import lu.itrust.business.TS.model.parameter.IParameter;
import lu.itrust.business.TS.model.parameter.IProbabilityParameter;
import lu.itrust.business.TS.model.parameter.impl.DynamicParameter;
import lu.itrust.business.TS.model.parameter.impl.ImpactParameter;
import lu.itrust.business.TS.model.parameter.value.IValue;
import lu.itrust.business.TS.model.parameter.value.impl.LevelValue;
import lu.itrust.business.TS.model.parameter.value.impl.RealValue;
import lu.itrust.business.TS.model.parameter.value.impl.Value;

/**
 * @author eomar
 *
 */
public class ValueFactory {

	private Map<String, List<IImpactParameter>> impacts;

	private Map<String, List<IProbabilityParameter>> probabilities;

	private Map<String, Map<String, IProbabilityParameter>> probabilityMapper;

	private Map<String, Map<String, IImpactParameter>> impactMapper;

	public ValueFactory(List<? extends IParameter> parameters) {
		add(parameters);
	}

	public ValueFactory(Map<String, List<? extends IParameter>> parameters) {
		parameters.entrySet().stream()
				.filter(entry -> entry.getKey().equals(Constant.PARAMETER_CATEGORY_IMPACT) || entry.getKey().equals(Constant.PARAMETER_CATEGORY_PROBABILITY_DYNAMIC)
						|| entry.getKey().equals(Constant.PARAMETER_CATEGORY_PROBABILITY_LIKELIHOOD))
				.flatMap(entry -> entry.getValue().stream()).forEach(parameter -> add(parameter));
	}

	private void add(IParameter parameter) {
		if (parameter instanceof IProbabilityParameter)
			add((IProbabilityParameter) parameter);
		else if (parameter instanceof ImpactParameter)
			add((IImpactParameter) parameter);
	}

	public IValue findDyn(Object value) {
		if (value == null || probabilities == null)
			return null;
		List<IProbabilityParameter> dynamicParameters = probabilities.get(PARAMETERTYPE_TYPE_DYNAMIC_NAME);
		if (dynamicParameters == null)
			return null;
		if (value instanceof Integer) {
			int index = (int) value, last = dynamicParameters.size() - 1;
			return new Value(dynamicParameters.get(index < 0 ? 0 : index > last ? last : index));
		}

		if (value instanceof String) {
			IProbabilityParameter parameter = (IProbabilityParameter) getParameterMapper(PARAMETERTYPE_TYPE_DYNAMIC_NAME).get(value.toString());
			if (parameter != null)
				return new Value(parameter);
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

	public IValue findProb(Object value) {
		return findValue(value, PARAMETERTYPE_TYPE_PROPABILITY_NAME);
	}

	public Integer findProbLevel(Object value) {
		IValue impact = findProb(value);
		return impact == null ? 0 : impact.getLevel();
	}

	public IProbabilityParameter findProbParameter(Object value) {
		IValue impact = findProb(value);
		return impact == null ? null : (IProbabilityParameter) impact.getParameter();
	}

	public Double findProbValue(Object value) {
		IValue impact = findProb(value);
		return impact == null ? 0D : impact.getReal();
	}

	private void add(IProbabilityParameter parameter) {
		if (probabilities == null)
			probabilities = new LinkedHashMap<>();
		List<IProbabilityParameter> parameters = probabilities.get(parameter.getTypeName());
		if (parameters == null)
			probabilities.put(parameter.getTypeName(), parameters = new ArrayList<>());
		parameters.add(parameter);

	}

	private void add(IImpactParameter parameter) {
		if (impacts == null)
			impacts = new LinkedHashMap<>();
		List<IImpactParameter> parameters = impacts.get(parameter.getTypeName());
		if (parameters == null)
			impacts.put(parameter.getTypeName(), parameters = new ArrayList<>());
		parameters.add(parameter);
	}

	private IValue findByLevel(Integer level, List<? extends ILevelParameter> parameters) {
		int mid = parameters.size() / 2;
		ILevelParameter parameter = (ILevelParameter) parameters.get(mid);
		if (parameter.getLevel() == level)
			return new Value(parameter);
		else if (mid == 0)
			return new LevelValue(level, parameter);
		else if (parameter.getLevel() > level)
			return findByLevel(level, parameters.subList(0, mid));
		else
			return findByLevel(level, parameters.subList(mid, parameters.size()));
	}

	private IValue findByValue(Double value, List<? extends ILevelParameter> parameters) {
		int mid = parameters.size() / 2;
		IBoundedParameter parameter = (IBoundedParameter) parameters.get(mid);
		if (parameter.getBounds().isInRange(value))
			return value == parameter.getValue() ? new Value(parameter) : new RealValue(value, parameter);
		else if (mid == 0)
			return new RealValue(value, parameter);
		else if (parameter.getBounds().getFrom() > value)
			return findByValue(value, parameters.subList(0, mid));
		else
			return findByValue(value, parameters.subList(mid, parameters.size()));
	}

	private IValue findDynamicByValue(Double doubleValue, List<? extends IProbabilityParameter> dynamicParameters) {
		DynamicParameter minParameter = (DynamicParameter) dynamicParameters.get(0), midParameter = (DynamicParameter) dynamicParameters.get(dynamicParameters.size() / 2),
				maxParameter = (DynamicParameter) dynamicParameters.get(dynamicParameters.size() - 1);
		if (minParameter.getValue() == doubleValue)
			return new Value(minParameter);
		else if (midParameter.getValue() == doubleValue)
			return new Value(midParameter);
		else if (maxParameter.getValue() == doubleValue)
			return new Value(maxParameter);
		else if (doubleValue < minParameter.getValue())
			return new RealValue(doubleValue, minParameter);
		else if (doubleValue > maxParameter.getValue() || dynamicParameters.size() < 2)
			return new RealValue(doubleValue, maxParameter);
		else if (doubleValue > midParameter.getValue())
			return findDynamicByValue(doubleValue, dynamicParameters.subList(dynamicParameters.size() / 2, dynamicParameters.size()));
		else
			return findDynamicByValue(doubleValue, dynamicParameters.subList(0, dynamicParameters.size() / 2));
	}

	public IValue findValue(Object value, String type) {
		if (value == null)
			return null;
		List<? extends ILevelParameter> parameters = getParameters(type);
		if (parameters == null)
			return null;
		if (value instanceof Integer)
			return findByLevel((Integer) value, parameters);
		if (value instanceof String) {
			ILevelParameter parameter = (ILevelParameter) getParameterMapper(type).get(value.toString());
			if (parameter != null)
				return new Value(parameter);
		}
		Double doubleValue = (value instanceof Double) ? (Double) value : ToDouble(value.toString(), null);
		if (doubleValue == null)
			return null;
		return findByValue(doubleValue, parameters);
	}

	public boolean hasAcronym(String value, String type) {
		if (value == null || type == null)
			return false;
		return getParameterMapper(type).containsKey(value);
	}

	private List<? extends ILevelParameter> getParameters(String type) {
		switch (type) {
		case PARAMETERTYPE_TYPE_DYNAMIC_NAME:
		case PARAMETERTYPE_TYPE_PROPABILITY_NAME:
			return probabilities == null ? null : probabilities.get(type);
		default:
			return impacts == null ? null : impacts.get(type);
		}
	}

	private Map<String, ? extends ILevelParameter> getParameterMapper(String type) {
		switch (type) {
		case PARAMETERTYPE_TYPE_DYNAMIC_NAME:
		case PARAMETERTYPE_TYPE_PROPABILITY_NAME:
			if (probabilities == null)
				return Collections.emptyMap();
			if (probabilityMapper == null)
				probabilityMapper = new HashMap<>();
			Map<String, IProbabilityParameter> values = probabilityMapper.get(type);
			if (values == null) {
				List<IProbabilityParameter> parameters = probabilities.get(type);
				if (parameters == null)
					return Collections.emptyMap();
				probabilityMapper.put(type, values = parameters.stream().collect(Collectors.toMap(IProbabilityParameter::getAcronym, Function.identity())));
			}
			return values;
		default:
			if (impacts == null)
				return Collections.emptyMap();
			if (impactMapper == null)
				impactMapper = new HashMap<>();
			Map<String, IImpactParameter> acronymImpacts = impactMapper.get(type);
			if (acronymImpacts == null) {
				List<IImpactParameter> parameters = impacts.get(type);
				if (parameters == null)
					return Collections.emptyMap();
				impactMapper.put(type, acronymImpacts = parameters.stream().collect(Collectors.toMap(IImpactParameter::getAcronym, Function.identity())));
			}
			return acronymImpacts;
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
		return findImportance(assessment.getLikelihood(), assessment.getImpacts());
	}

	/**
	 * Compute importance probaLvl * MaxLevel(impacts)
	 * 
	 * @param proba
	 * @param impactFin
	 * @param impactLeg
	 * @param impactOp
	 * @param impactRep
	 * @return importance
	 * @see IValue#maxByLevel(IValue, IValue)
	 */
	public int findImportance(String proba, List<? extends IValue> impacts) {
		IValue impact = impacts == null ? null : impacts.stream().max((v1, v2) -> IValue.compareByLevel(v1, v2)).orElse(null);
		return impact == null ? 0 : impact.getLevel() * findExpLevel(proba);
	}

	public IValue findMaxImpactByLevel(Map<String, Value> impacts) {
		return impacts == null ? null : findMaxImpactByLevel(impacts.values());
	}

	public IValue findMaxImpactByLevel(Collection<IValue> impacts) {
		return impacts.stream().max((v1, v2) -> IValue.compareByLevel(v1, v2)).orElse(null);
	}

	public IValue findMaxImpactByLevel(Object value) {
		return impacts == null ? null : impacts.keySet().stream().map(type -> findValue(value, type)).max((v1, v2) -> IValue.compareByLevel(v1, v2)).orElse(null);
	}

	public IValue findMinImpactByLevel(Object value) {
		return impacts == null ? null : impacts.keySet().stream().map(type -> findValue(value, type)).min((v1, v2) -> IValue.compareByLevel(v1, v2)).orElse(null);
	}

	public int findImpactLevelByMaxLevel(double value) {
		IValue iValue = findMaxImpactByLevel(value);
		return iValue == null ? 0 : iValue.getLevel();
	}

	public double findRealImpactByMaxLevel(int level) {
		IValue iValue = findMaxImpactByLevel(level);
		return iValue == null ? 0 : iValue.getReal();
	}

	public IValue findMaxImpactByReal(List<? extends IValue> impacts) {
		return impacts == null ? null : impacts.stream().max((v1, v2) -> IValue.compareByReal(v1, v2)).orElse(null);
	}

	/**
	 * @return the probabilityMapper
	 */
	public Map<String, Map<String, IProbabilityParameter>> getProbabilityMapper() {
		return probabilityMapper;
	}

	/**
	 * @param probabilityMapper
	 *            the probabilityMapper to set
	 */
	public void setProbabilityMapper(Map<String, Map<String, IProbabilityParameter>> probabilityMapper) {
		this.probabilityMapper = probabilityMapper;
	}

	/**
	 * @return the impactMapper
	 */
	public Map<String, Map<String, IImpactParameter>> getImpactMapper() {
		return impactMapper;
	}

	public Collection<String> getImpactNames() {
		return impacts == null ? Collections.emptyList() : impacts.keySet();
	}

	/**
	 * @param impactMapper
	 *            the impactMapper to set
	 */
	public void setImpactMapper(Map<String, Map<String, IImpactParameter>> impactMapper) {
		this.impactMapper = impactMapper;
	}

	public Double computeALEByLevel(Assessment assessment) {
		IValue value = findMaxImpactByLevel(assessment.getImpacts());
		return value == null ? 0 : value.getReal() * findExpValue(assessment.getLikelihood());
	}

	public IProbabilityParameter findExpParameter(String likelihood) {
		IValue value = findExp(likelihood);
		return value == null ? null : (IProbabilityParameter) value.getParameter();
	}

	public void add(List<? extends IParameter> parameters) {
		parameters.forEach(parameter -> add(parameter));
	}

	public Collection<String> findAcronyms(String type) {
		return getParameterMapper(type).keySet();
	}
}
