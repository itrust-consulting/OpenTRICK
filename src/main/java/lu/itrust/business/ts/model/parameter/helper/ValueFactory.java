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
 * The ValueFactory class is responsible for managing and manipulating various
 * types of parameters, such as impacts, probabilities, and dynamics.
 * It provides methods for adding parameters, finding values based on different
 * criteria, and retrieving parameter information.
 * 
 * This class is used to create instances of ValueFactory that can be used to
 * handle parameter-related operations.
 * 
 * @param impacts           A map of impact parameters categorized by type.
 * @param probabilities     A map of probability parameters categorized by type.
 * @param dynamics          A map of acronym parameters categorized by type.
 * @param probabilityMapper A map that maps probability parameter acronyms to
 *                          their corresponding parameters.
 * @param dynamicMapper     A map that maps acronym parameters to their
 *                          corresponding parameters.
 * @param impactMapper      A map that maps impact parameters to their
 *                          corresponding parameters.
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

	/**
	 * Adds the given parameter to the value factory.
	 *
	 * @param parameter the parameter to be added
	 */
	private void add(IParameter parameter) {
		if (parameter instanceof IProbabilityParameter)
			add((IProbabilityParameter) parameter);
		else if (parameter instanceof IImpactParameter)
			add((IImpactParameter) parameter);
		else if (parameter instanceof IAcronymParameter) {
			add((IAcronymParameter) parameter);
		}
	}

	/**
	 * Finds the dynamic value based on the given input value.
	 * 
	 * @param value the input value to find the dynamic value for
	 * @return the dynamic value corresponding to the input value, or 0.0 if the
	 *         input value is null or dynamics is null
	 */
	public double findDyn(Object value) {
		if (value == null || dynamics == null)
			return 0.0;
		if (value instanceof String) {
			final List<IAcronymParameter> dynamicParameters = dynamics.get(PARAMETERTYPE_TYPE_DYNAMIC_NAME);
			if (dynamicParameters != null) {
				final IAcronymParameter parameter = getParameterMapper(
						PARAMETERTYPE_TYPE_DYNAMIC_NAME).get(value.toString());
				if (parameter != null)
					return parameter.getValue();
			}
		}
		return (value instanceof Double) ? (Double) value : toDouble(value.toString(), 0.0);
	}

	/**
	 * Finds the probability experience level for the given value.
	 *
	 * @param value the value to find the probability experience level for
	 * @return the probability experience level as an Integer
	 */
	public Integer findProbaExpLevel(Object value) {
		IValue iValue = findProb(value);
		return iValue == null ? 0 : iValue.getLevel();
	}

	/**
	 * Finds the probability expected value for the given value.
	 *
	 * @param value the value to find the probability expected value for
	 * @return the probability expected value as a Double
	 */
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

	/**
	 * This interface represents a value in the system.
	 */
	public IValue findProb(Object value) {
		return findValue(value, PARAMETER_TYPE_PROPABILITY_NAME);
	}

	/**
	 * Finds the probability level of a given value.
	 *
	 * @param value the value to find the probability level for
	 * @return the probability level of the value
	 */
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

	/**
	 * Finds the probability parameter based on the given value.
	 *
	 * @param value The value used to find the probability parameter.
	 * @return The probability parameter found, or null if not found.
	 */
	public IProbabilityParameter findProbParameter(Object value) {
		IValue impact = findProb(value);
		return !(impact instanceof IParameterValue) ? null
				: (IProbabilityParameter) ((IParameterValue) impact).getParameter();
	}

	/**
	 * Finds the probability value for the given object.
	 *
	 * @param value the object for which to find the probability value
	 * @return the probability value as a {@code Double}, or 0 if not found
	 */
	public Double findProbValue(Object value) {
		IValue impact = findProb(value);
		return impact == null ? 0D : impact.getReal();
	}

	/**
	 * Adds the given probability parameter to the collection of probabilities.
	 * If the collection is null, a new LinkedHashMap is created.
	 * If the list of parameters for the given type name is null, a new ArrayList is
	 * created.
	 * The parameter is then added to the list of parameters for the given type
	 * name.
	 *
	 * @param parameter the probability parameter to be added
	 */
	private void add(IProbabilityParameter parameter) {
		if (probabilities == null)
			probabilities = new LinkedHashMap<>();
		List<IProbabilityParameter> parameters = probabilities.get(parameter.getTypeName());
		if (parameters == null)
			probabilities.put(parameter.getTypeName(), parameters = new ArrayList<>());
		parameters.add(parameter);

	}

	/**
	 * Adds an IImpactParameter to the list of impacts.
	 * If the impacts map is null, it initializes it as a new LinkedHashMap.
	 * If the list of parameters for the given type name is null, it initializes it
	 * as a new ArrayList.
	 * Finally, it adds the IImpactParameter to the list of parameters for the given
	 * type name.
	 *
	 * @param parameter the IImpactParameter to be added
	 */
	private void add(IImpactParameter parameter) {
		if (impacts == null)
			impacts = new LinkedHashMap<>();
		List<IImpactParameter> parameters = impacts.get(parameter.getTypeName());
		if (parameters == null)
			impacts.put(parameter.getTypeName(), parameters = new ArrayList<>());
		parameters.add(parameter);
	}

	/**
	 * Adds an acronym parameter to the dynamics map.
	 * If the dynamics map is null, it initializes a new LinkedHashMap.
	 * If the list of parameters for the given type name is null, it initializes a
	 * new ArrayList.
	 * Then, it adds the parameter to the list of parameters for the given type
	 * name.
	 *
	 * @param parameter the acronym parameter to be added
	 */
	private void add(IAcronymParameter parameter) {
		if (dynamics == null)
			dynamics = new LinkedHashMap<>();
		List<IAcronymParameter> parameters = dynamics.get(parameter.getTypeName());
		if (parameters == null)
			dynamics.put(parameter.getTypeName(), parameters = new ArrayList<>());
		parameters.add(parameter);
	}

	/**
	 * Represents a value in the system.
	 */
	private IValue findByLevel(Integer level, List<? extends ILevelParameter> parameters) {
		int mid = parameters.size() / 2;
		ILevelParameter parameter = parameters.get(mid);
		if (parameter.getLevel().equals(level))
			return new Value(parameter);
		else if (mid == 0)
			return new LevelValue(level, parameter);
		else if (parameter.getLevel() > level)
			return findByLevel(level, parameters.subList(0, mid));
		else
			return findByLevel(level, parameters.subList(mid, parameters.size()));
	}

	/**
	 * Represents a value in the system.
	 */
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

	/**
	 * Represents a value in the system.
	 */
	public IValue findValue(Object value, String type) {
		return findValue(value, type, true);
	}

	/**
	 * Represents a value in the system.
	 * Implementations of this interface provide methods to manipulate and retrieve
	 * values.
	 */
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
			TrickLogManager.persist(e);
			return null;
		}
	}

	/**
	 * Represents a value in the system.
	 */
	public IValue findDynValue(String value, String type) {
		return findDynValue(value, type, Collections.emptyList());
	}

	private IValue findDynValue(String value, String type, final List<? extends ILevelParameter> parameters) {
		final int family = findParameterFamily(type);
		if (family != -1) {
			final List<? extends ILevelParameter> myParameters = parameters == null || parameters.isEmpty()
					? getParameters(type)
					: parameters;
			final Double result = new StringExpressionParser(value, family).evaluate(this, null);
			if (result != null) {
				final FormulaValue formulaValue = new FormulaValue(value, result);
				if (!(myParameters == null || myParameters.isEmpty())) {
					IValue aux = findByValue(result, myParameters);
					if (aux != null)
						formulaValue.setLevel(aux.getLevel());

				}
				return formulaValue;
			}
		}
		return null;
	}

	/**
	 * Finds the parameter family based on the given type.
	 * 
	 * @param type the type of the parameter
	 * @return the parameter family constant value, or -1 if the type is not
	 *         recognized
	 */
	private int findParameterFamily(String type) {
		if (Constant.DEFAULT_IMPACT_NAME.equals(type))
			return StringExpressionParser.IMPACT;
		return Constant.PARAMETER_TYPE_PROPABILITY_NAME.equals(type) ? StringExpressionParser.PROBABILITY : -1;
	}

	/**
	 * Checks if the given value has an acronym for the specified type.
	 *
	 * @param value the value to check
	 * @param type  the type to check against
	 * @return true if the value has an acronym for the specified type, false
	 *         otherwise
	 */
	public boolean hasAcronym(String value, String type) {
		if (value == null || type == null)
			return false;
		return getParameterMapper(type).containsKey(value);
	}

	/**
	 * Retrieves the list of parameters based on the given type.
	 *
	 * @param type the type of parameters to retrieve
	 * @return the list of parameters for the given type, or null if the type is not
	 *         recognized
	 */
	private List<? extends ILevelParameter> getParameters(String type) {
		if (PARAMETER_TYPE_PROPABILITY_NAME.equals(type))
			return probabilities == null ? null : probabilities.get(type);

		return impacts == null ? null : impacts.get(type);

	}

	/**
	 * Retrieves the parameter mapper based on the given type.
	 *
	 * @param type The type of the parameter mapper to retrieve.
	 * @return The parameter mapper as a map.
	 */
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

	/**
	 * Converts a string value to a Double.
	 * If the value is null or cannot be parsed as a Double, the defaultValue is
	 * returned.
	 *
	 * @param value        the string value to convert
	 * @param defaultValue the default value to return if the conversion fails
	 * @return the converted Double value or the defaultValue if the conversion
	 *         fails
	 */
	public static Double toDouble(String value, Double defaultValue) {
		try {
			return value == null ? defaultValue : Double.parseDouble(value);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	/**
	 * Converts a string value to an integer.
	 * If the value is null or cannot be parsed as an integer, the default value is
	 * returned.
	 *
	 * @param value        the string value to convert
	 * @param defaultValue the default value to return if the conversion fails
	 * @return the converted integer value, or the default value if the conversion
	 *         fails
	 */
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
	public static int findImportance(Assessment assessment) {
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
	public static int findImportance(IValue proba, List<? extends IValue> impacts) {
		return findImpactLevel(impacts) * (proba == null ? 0 : proba.getLevel());
	}

	/**
	 * Finds the impact level based on the given list of impacts.
	 *
	 * @param impacts the list of impacts
	 * @return the impact level, or 0 if the list is null or empty
	 */
	public static int findImpactLevel(List<? extends IValue> impacts) {
		return impacts == null ? 0
				: impacts.stream().filter(value -> !value.getName().equals(Constant.DEFAULT_IMPACT_NAME))
						.max(IValue::compareByLevel).map(IValue::getLevel)
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

	/**
	 * Finds the maximum impact level from the given array of impacts.
	 *
	 * @param impacts the array of impacts to search for the maximum impact level
	 * @return the maximum impact level found in the array of impacts, or 0 if the
	 *         array is empty or null
	 */
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
		return impacts.stream().max(IValue::compareByLevel).orElse(null);
	}

	public IValue findMaxImpactByLevel(Object value) {
		return impacts == null ? null
				: impacts.keySet().stream().filter(type -> !type.equals(Constant.DEFAULT_IMPACT_NAME))
						.map(type -> findValue(value, type))
						.max(IValue::compareByLevel).orElse(null);
	}

	public IValue findMinImpactByLevel(Object value) {
		return impacts == null ? null
				: impacts.keySet().stream().filter(type -> !type.equals(Constant.DEFAULT_IMPACT_NAME))
						.map(type -> findValue(value, type))
						.min(IValue::compareByLevel).orElse(null);
	}

	/**
	 * Finds the impact level based on the maximum level for a given value.
	 * 
	 * @param value the value to find the impact level for
	 * @return the impact level corresponding to the maximum level for the given
	 *         value,
	 *         or 0 if no impact level is found
	 */
	public int findImpactLevelByMaxLevel(double value) {
		IValue iValue = findMaxImpactByLevel(value);
		return iValue == null ? 0 : iValue.getLevel();
	}

	/**
	 * Finds the real impact by the maximum level.
	 * 
	 * @param level the maximum level
	 * @return the real impact value
	 */
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

	/**
	 * Returns a collection of impact names.
	 *
	 * @return a collection of impact names
	 */
	public Collection<String> getImpactNames() {
		return impacts == null ? Collections.emptyList() : impacts.keySet();
	}

	/**
	 * @param impactMapper the impactMapper to set
	 */
	public void setImpactMapper(Map<String, Map<String, IImpactParameter>> impactMapper) {
		this.impactMapper = impactMapper;
	}

	/**
	 * Computes the Annual Loss Expectancy (ALE) based on the given assessment.
	 *
	 * @param assessment the assessment object containing the impacts and likelihood
	 * @return the computed ALE value
	 */
	public Double computeALE(Assessment assessment) {
		IValue value = findMaxImpactByReal(assessment.getImpacts());
		return value == null ? 0 : value.getReal() * findProbaExpValue(assessment.getLikelihood());
	}

	/**
	 * Finds the exponential parameter based on the given likelihood.
	 *
	 * @param likelihood the likelihood value used to find the parameter
	 * @return the exponential parameter found, or null if not found
	 */
	public IProbabilityParameter findExpParameter(String likelihood) {
		IValue value = findProb(likelihood);
		return !(value instanceof IParameterValue) ? null
				: (IProbabilityParameter) ((IParameterValue) value).getParameter();
	}

	/**
	 * Adds a collection of parameters to the value factory.
	 *
	 * @param parameters the collection of parameters to be added
	 */
	public void add(Collection<? extends IParameter> parameters) {
		parameters.forEach(this::add);
	}

	/**
	 * Finds the acronyms for the given type.
	 *
	 * @param type the type for which to find the acronyms
	 * @return a collection of acronyms for the given type
	 */
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

	/**
	 * Finds the maximum impact value from the given array of IValue objects.
	 *
	 * @param impacts The array of IValue objects representing the impact values.
	 * @return The maximum impact value as a double.
	 */
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

	/**
	 * Finds the parameter based on the given value and type.
	 *
	 * @param value the value to search for
	 * @param type  the type of the parameter
	 * @return the found parameter, or null if not found
	 */
	public ILevelParameter findParameter(Integer value, String type) {
		IValue result = findValue(value, type);
		return !(result instanceof IParameterValue) ? null
				: ((IParameterValue) result).getParameter();
	}

	/**
	 * Finds the level parameter based on the given value and type.
	 *
	 * @param value the value to search for
	 * @param type  the type of the parameter
	 * @return the level parameter if found, otherwise null
	 */
	public ILevelParameter findParameter(Double value, String type) {
		IValue result = findValue(value, type);
		return !(result instanceof IParameterValue) ? null
				: ((IParameterValue) result).getParameter();
	}

	/**
	 * Finds the parameter based on the given value and type.
	 *
	 * @param value the value to search for
	 * @param type  the type of the value
	 * @return the found ILevelParameter, or null if not found
	 */
	public ILevelParameter findParameter(Object value, String type) {
		IValue result = findValue(value, type);
		return !(result instanceof IParameterValue) ? null
				: ((IParameterValue) result).getParameter();
	}

	/**
	 * Finds the ILR (Information Loss Rate) level for the given value.
	 * 
	 * @param value The value for which to find the ILR level.
	 * @return The ILR level of the value. Returns 0 if the value is null or if the
	 *         ILR level cannot be determined.
	 */
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
