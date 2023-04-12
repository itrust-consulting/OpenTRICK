/**
 * 
 */
package lu.itrust.business.ts.model.parameter.helper;

import static lu.itrust.business.ts.constants.Constant.PARAMETERTYPE_TYPE_DYNAMIC_NAME;
import static lu.itrust.business.ts.constants.Constant.PARAMETER_TYPE_PROPABILITY_NAME;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import lu.itrust.business.ts.component.TrickLogManager;
import lu.itrust.business.ts.constants.Constant;
import lu.itrust.business.ts.model.assessment.Assessment;
import lu.itrust.business.ts.model.parameter.IAcronymParameter;
import lu.itrust.business.ts.model.parameter.IBoundedParameter;
import lu.itrust.business.ts.model.parameter.IImpactParameter;
import lu.itrust.business.ts.model.parameter.ILevelParameter;
import lu.itrust.business.ts.model.parameter.IParameter;
import lu.itrust.business.ts.model.parameter.IProbabilityParameter;
import lu.itrust.business.ts.model.parameter.impl.LikelihoodParameter;
import lu.itrust.business.ts.model.parameter.value.IParameterValue;
import lu.itrust.business.ts.model.parameter.value.IValue;
import lu.itrust.business.ts.model.parameter.value.impl.FormulaValue;
import lu.itrust.business.ts.model.parameter.value.impl.LevelValue;
import lu.itrust.business.ts.model.parameter.value.impl.RealValue;
import lu.itrust.business.ts.model.parameter.value.impl.Value;
import lu.itrust.business.expressions.StringExpressionParser;

/**
 * @author eomar
 *
 */
public class ValueFactory {

	private Map<String, List<IImpactParameter>> impacts;

	private Map<String, List<IProbabilityParameter>> probabilities;

	private Map<String, List<IAcronymParameter>> dynamics;

	private Map<String, IProbabilityParameter> probabilityMapper;

	private Map<String, IAcronymParameter> dynamicMapper;

	private Map<String, Map<String, IImpactParameter>> impactMapper;

	public ValueFactory() {
	}

	public ValueFactory(Collection<? extends IParameter> parameters) {
		add(parameters);
	}

	public ValueFactory(Map<String, List<? extends IParameter>> parameters) {
		parameters.entrySet().stream()
				.filter(entry -> entry.getKey().equals(Constant.PARAMETER_CATEGORY_IMPACT)
						|| entry.getKey().equals(Constant.PARAMETER_CATEGORY_DYNAMIC)
						|| entry.getKey().equals(Constant.PARAMETER_CATEGORY_PROBABILITY_LIKELIHOOD))
				.flatMap(entry -> entry.getValue().stream()).forEach(this::add);
	}

	private void add(IParameter parameter) {
		if (parameter instanceof IProbabilityParameter)
			add((IProbabilityParameter) parameter);
		else if (parameter instanceof IImpactParameter)
			add((IImpactParameter) parameter);
		else if (parameter instanceof IAcronymParameter) {
			add((IAcronymParameter) parameter);
		}
	}

	public double findDyn(Object value) {
		if (value == null || dynamics == null)
			return 0.0;
		if (value instanceof String) {
			final List<IAcronymParameter> dynamicParameters = dynamics.get(PARAMETERTYPE_TYPE_DYNAMIC_NAME);
			if (dynamicParameters != null) {
				final IAcronymParameter parameter = (IAcronymParameter) getParameterMapper(
						PARAMETERTYPE_TYPE_DYNAMIC_NAME).get(value.toString());
				if (parameter != null)
					return parameter.getValue();
			}
		}
		return (value instanceof Double) ? (Double) value : toDouble(value.toString(), 0.0);
	}

	public Integer findProbaExpLevel(Object value) {
		IValue iValue = findProb(value);
		return iValue == null ? 0 : iValue.getLevel();
	}

	public Double findProbaExpValue(Object value) {
		if (value == null)
			return 0d;
		else if (value instanceof IValue)
			return ((IValue) value).getReal();
		else {
			IValue iValue = findProb(value);
			return iValue == null ? 0D : iValue.getReal();
		}
	}

	public IValue findProb(Object value) {
		return findValue(value, PARAMETER_TYPE_PROPABILITY_NAME);
	}

	public Integer findProbLevel(Object value) {
		if (value == null)
			return 0;
		else if (value instanceof IValue)
			return ((IValue) value).getLevel();
		else {
			IValue impact = findProb(value);
			return impact == null ? 0 : impact.getLevel();
		}
	}

	public IProbabilityParameter findProbParameter(Object value) {
		IValue impact = findProb(value);
		return impact == null || !(impact instanceof IParameterValue) ? null
				: (IProbabilityParameter) ((IParameterValue) impact).getParameter();
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

	private void add(IAcronymParameter parameter) {
		if (dynamics == null)
			dynamics = new LinkedHashMap<>();
		List<IAcronymParameter> parameters = dynamics.get(parameter.getTypeName());
		if (parameters == null)
			dynamics.put(parameter.getTypeName(), parameters = new ArrayList<>());
		parameters.add(parameter);
	}

	private IValue findByLevel(Integer level, List<? extends ILevelParameter> parameters) {
		int mid = parameters.size() / 2;
		ILevelParameter parameter = (ILevelParameter) parameters.get(mid);
		if (parameter.getLevel().equals(level) )
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
			return !parameter.getValue().equals(value) || parameter.isMatch(Constant.DEFAULT_IMPACT_NAME)
					? new RealValue(value, parameter)
					: new Value(parameter);
		else if (mid == 0)
			return new RealValue(value, parameter);
		else if (parameter.getBounds().getFrom() > value)
			return findByValue(value, parameters.subList(0, mid));
		else
			return findByValue(value, parameters.subList(mid, parameters.size()));
	}

	public IValue findValue(Object value, String type) {
		return findValue(value, type, true);
	}

	public IValue findValue(Object value, String type, boolean includExpression) {
		try {
			if (value == null)
				return null;
			final List<? extends ILevelParameter> parameters = getParameters(type);
			if (parameters == null)
				return null;
			if (value instanceof Integer)
				return findByLevel((Integer) value, parameters);
			if (value instanceof String) {
				ILevelParameter parameter = (ILevelParameter) getParameterMapper(type).get(value.toString());
				if (parameter != null)
					return new Value(parameter);

				if ("na".equalsIgnoreCase((String) value))
					value = "0";
				else {
					final String myValue = (String) value;
					parameter = parameters.stream()
							.filter(e -> e instanceof IBoundedParameter && ((IBoundedParameter) e).getLabel() != null
									&& ((IBoundedParameter) e).getLabel().equalsIgnoreCase(myValue))
							.findAny().orElse(null);

					if (parameter != null)
						return new Value(parameter);
				}
			}

			final Double doubleValue = (value instanceof Double) ? (Double) value : toDouble(value.toString(), null);
			if (doubleValue == null) {
				if (includExpression && value instanceof String) {
					final IValue aux = findDynValue((String) value, type, parameters);
					if (aux != null)
						return aux;
				}
				return null;
			}
			return findByValue(doubleValue, parameters);
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			return null;
		}
	}

	public IValue findDynValue(String value, String type) {
		return findDynValue(value, type, Collections.emptyList());
	}

	private IValue findDynValue(String value, String type, final List<? extends ILevelParameter> parameters) {
		final int family = Constant.DEFAULT_IMPACT_NAME.equals(type) ? StringExpressionParser.IMPACT
				: Constant.PARAMETER_TYPE_PROPABILITY_NAME.equals(type) ? StringExpressionParser.PROBABILITY : -1;
		if (family != -1) {
			final List<? extends ILevelParameter> myParameters = parameters == null || parameters.isEmpty()
					? getParameters(type)
					: parameters;
			final Double result = new StringExpressionParser(value, family).evaluate(this, null);
			if (result != null) {
				final FormulaValue formulaValue = new FormulaValue(value, result);
				if (!(parameters == null || parameters.isEmpty())) {
					IValue aux = findByValue(result, myParameters);
					if (aux != null)
						formulaValue.setLevel(aux.getLevel());

				}
				return formulaValue;
			}
		}
		return null;
	}

	public boolean hasAcronym(String value, String type) {
		if (value == null || type == null)
			return false;
		return getParameterMapper(type).containsKey(value);
	}

	private List<? extends ILevelParameter> getParameters(String type) {
		switch (type) {
			case PARAMETER_TYPE_PROPABILITY_NAME:
				return probabilities == null ? null : probabilities.get(type);
			default:
				return impacts == null ? null : impacts.get(type);
		}
	}

	private Map<String, ? extends IAcronymParameter> getParameterMapper(String type) {
		switch (type) {
			case PARAMETERTYPE_TYPE_DYNAMIC_NAME:
				if (dynamics == null)
					return Collections.emptyMap();
				if (dynamicMapper == null) {
					final List<IAcronymParameter> parameters = dynamics.get(type);
					if (parameters == null)
						return Collections.emptyMap();
					dynamicMapper = parameters.stream()
							.collect(Collectors.toMap(IAcronymParameter::getAcronym, Function.identity()));
				}
				return dynamicMapper;

			case PARAMETER_TYPE_PROPABILITY_NAME:
				if (probabilities == null)
					return Collections.emptyMap();
				if (probabilityMapper == null) {
					final List<IProbabilityParameter> parameters = probabilities.get(type);
					if (parameters == null)
						return Collections.emptyMap();
					else
						probabilityMapper = parameters.stream()
								.collect(Collectors.toMap(IProbabilityParameter::getAcronym, Function.identity()));
				}
				return probabilityMapper;
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
					impactMapper.put(type, acronymImpacts = parameters.stream()
							.collect(Collectors.toMap(IImpactParameter::getAcronym, Function.identity())));
				}
				return acronymImpacts;
		}

	}

	public static Double toDouble(String value, Double defaultValue) {
		try {
			return value == null ? defaultValue : Double.parseDouble(value);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	public static Integer toInt(String value, Integer defaultValue) {
		try {
			return value == null ? defaultValue : Integer.parseInt(value);
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
		return findImportance(assessment.getLikelihood(), assessment.getImpacts()) * assessment.getVulnerability();
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
	public int findImportance(IValue proba, List<? extends IValue> impacts) {
		return findImpactLevel(impacts) * (proba == null ? 0 : proba.getLevel());
	}

	public int findImpactLevel(List<? extends IValue> impacts) {
		return impacts == null ? 0
				: impacts.stream().filter(value -> !value.getName().equals(Constant.DEFAULT_IMPACT_NAME))
						.max((v1, v2) -> IValue.compareByLevel(v1, v2)).map(IValue::getLevel)
						.orElse(0);
	}

	/**
	 * Gets quantitative impact value
	 * 
	 * @param valuye
	 * @return
	 */
	public IValue findImpact(Object value) {
		return findValue(value, Constant.DEFAULT_IMPACT_NAME);
	}

	public int findImpactLevel(IValue... impacts) {
		if (impacts == null || impacts.length == 0)
			return 0;
		IValue max = impacts[0];
		for (int i = 1; i < impacts.length; i++) {
			if (IValue.compareByLevel(max, impacts[i]) < 0)
				max = impacts[i];
		}
		return max.getLevel();

	}

	public IValue findMaxImpactByLevel(Map<String, Value> impacts) {
		return impacts == null ? null : findMaxImpactByLevel(impacts.values());
	}

	public IValue findMaxImpactByLevel(Collection<IValue> impacts) {
		return impacts.stream().max((v1, v2) -> IValue.compareByLevel(v1, v2)).orElse(null);
	}

	public IValue findMaxImpactByLevel(Object value) {
		return impacts == null ? null
				: impacts.keySet().stream().filter(type -> !type.equals(Constant.DEFAULT_IMPACT_NAME))
						.map(type -> findValue(value, type))
						.max((v1, v2) -> IValue.compareByLevel(v1, v2)).orElse(null);
	}

	public IValue findMinImpactByLevel(Object value) {
		return impacts == null ? null
				: impacts.keySet().stream().filter(type -> !type.equals(Constant.DEFAULT_IMPACT_NAME))
						.map(type -> findValue(value, type))
						.min((v1, v2) -> IValue.compareByLevel(v1, v2)).orElse(null);
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
		return impacts == null ? null
				: impacts.stream().filter(value -> value.getName().equals(Constant.DEFAULT_IMPACT_NAME))
						.max(IValue::compareByReal).orElse(null);
	}

	public Double findRealValue(List<? extends IValue> impacts) {
		IValue value = findMaxImpactByReal(impacts);
		return value == null ? 0d : value.getReal();
	}

	/**
	 * @return the probabilityMapper
	 */
	public Map<String, IProbabilityParameter> getProbabilityMapper() {
		return probabilityMapper;
	}

	/**
	 * @param probabilityMapper the probabilityMapper to set
	 */
	public void setProbabilityMapper(Map<String, IProbabilityParameter> probabilityMapper) {
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
	 * @param impactMapper the impactMapper to set
	 */
	public void setImpactMapper(Map<String, Map<String, IImpactParameter>> impactMapper) {
		this.impactMapper = impactMapper;
	}

	public Double computeALE(Assessment assessment) {
		IValue value = findMaxImpactByReal(assessment.getImpacts());
		return value == null ? 0 : value.getReal() * findProbaExpValue(assessment.getLikelihood());
	}

	public IProbabilityParameter findExpParameter(String likelihood) {
		IValue value = findProb(likelihood);
		return !(value instanceof IParameterValue) ? null
				: (IProbabilityParameter) ((IParameterValue) value).getParameter();
	}

	public void add(Collection<? extends IParameter> parameters) {
		parameters.forEach(parameter -> add(parameter));
	}

	public Collection<String> findAcronyms(String type) {
		return getParameterMapper(type).keySet();
	}

	/**
	 * @return the impacts
	 */
	public Map<String, List<IImpactParameter>> getImpacts() {
		return impacts;
	}

	/**
	 * @param impacts the impacts to set
	 */
	public void setImpacts(Map<String, List<IImpactParameter>> impacts) {
		this.impacts = impacts;
	}

	/**
	 * @return the probabilities
	 */
	public Map<String, List<IProbabilityParameter>> getProbabilities() {
		return probabilities;
	}

	/**
	 * @param probabilities the probabilities to set
	 */
	public void setProbabilities(Map<String, List<IProbabilityParameter>> probabilities) {
		this.probabilities = probabilities;
	}

	public double findImpactValue(IValue... impacts) {
		if (impacts == null || impacts.length == 0)
			return 0.0;
		IValue max = impacts[0];
		for (int i = 1; i < impacts.length; i++) {
			if (IValue.compareByReal(max, impacts[i]) < 0)
				max = impacts[i];
		}
		return max.getReal();
	}

	public ILevelParameter findParameter(Integer value, String type) {
		IValue result = findValue(value, type);
		return !(result instanceof IParameterValue) ? null
				: ((IParameterValue) result).getParameter();
	}

	public ILevelParameter findParameter(Double value, String type) {
		IValue result = findValue(value, type);
		return !(result instanceof IParameterValue) ? null
				: ((IParameterValue) result).getParameter();
	}

	public ILevelParameter findParameter(Object value, String type) {
		IValue result = findValue(value, type);
		return !(result instanceof IParameterValue) ? null
				: ((IParameterValue) result).getParameter();
	}

	public int findILRLevel(IValue value) {
		if (value == null)
			return 0;
		else if (value instanceof IParameterValue) {
			ILevelParameter parameter = ((IParameterValue) value).getParameter();
			if (parameter instanceof LikelihoodParameter)
				return ((LikelihoodParameter) parameter).getIlrLevel();
			else {
				parameter = findParameter(value.getLevel(), PARAMETER_TYPE_PROPABILITY_NAME);
				if (!(parameter instanceof LikelihoodParameter))
					return 0;
				else
					return ((LikelihoodParameter) parameter).getIlrLevel();
			}
		} else {
			final ILevelParameter parameter = findParameter(value.getLevel(), PARAMETER_TYPE_PROPABILITY_NAME);
			if (!(parameter instanceof LikelihoodParameter))
				return 0;
			else
				return ((LikelihoodParameter) parameter).getIlrLevel();
		}
	}
}
