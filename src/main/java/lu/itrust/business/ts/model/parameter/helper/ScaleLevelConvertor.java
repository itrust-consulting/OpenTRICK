/**
 * 
 */
package lu.itrust.business.ts.model.parameter.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.helper.NaturalOrderComparator;
import lu.itrust.business.ts.model.parameter.IBoundedParameter;
import lu.itrust.business.ts.model.parameter.impl.ImpactParameter;
import lu.itrust.business.ts.model.parameter.impl.LikelihoodParameter;
import lu.itrust.business.ts.model.scale.ScaleType;

/**
 * The ScaleLevelConvertor class is responsible for converting and mapping scale levels and parameters.
 * It provides methods to set up likelihood parameters and impact parameters based on given mappers and lists.
 * The class also includes methods to compute likelihoods and impacts based on maximum values.
 * It maintains mappings between parameters, acronyms, and levels using LinkedHashMaps.
 * The class implements the IBoundedParameter interface and provides methods to find parameters based on various criteria.
 */
public class ScaleLevelConvertor {

	private Map<String, IBoundedParameter> acronymMappers = Collections.emptyMap();

	private Map<String, IBoundedParameter> levelMappers = Collections.emptyMap();

	private Map<IBoundedParameter, IBoundedParameter> parameterMapper = Collections.emptyMap();

	private List<IBoundedParameter> parameters = new LinkedList<>();

	public ScaleLevelConvertor(Map<Integer, List<Integer>> mappers, List<ImpactParameter> impactParameters,
			List<LikelihoodParameter> likelihoods) {
		try {
			initialise(impactParameters.size() + likelihoods.size());
			mappers.values().forEach(levels -> levels.sort((l1, l2) -> l1.compareTo(l2)));
			setUpImpacts(mappers, impactParameters);
			setUpLikelihood(mappers, likelihoods);
			parameters.sort((p1, p2) -> {
				int result = NaturalOrderComparator.compareTo(p1.getTypeName(), p2.getTypeName());
				return result == 0 ? p1.getLevel().compareTo(p2.getLevel()) : result;
			});
		} catch (Exception e) {
			throw new TrickException("error.scale.level.migrate.convertor.initialise", "Scale level cannot be migrated",
					e);
		}

	}

	/**
	 * Sets up the likelihood parameters based on the given mappers and likelihoods.
	 *
	 * @param mappers     the map of levels and matching levels
	 * @param likelihoods the list of likelihood parameters
	 */
	private void setUpLikelihood(Map<Integer, List<Integer>> mappers, List<LikelihoodParameter> likelihoods) {
		final Map<Integer, LikelihoodParameter> levelMapping = likelihoods.stream()
				.collect(Collectors.toMap(LikelihoodParameter::getLevel, Function.identity()));
		final List<LikelihoodParameter> parameters = new ArrayList<>(mappers.size());
		mappers.forEach((level, matchingLevels) -> {
			if (matchingLevels.isEmpty()) {
				final LikelihoodParameter parameter = new LikelihoodParameter(level, "p" + level);
				parameters.add(parameter);
				this.parameters.add(parameter);
			} else {
				final LikelihoodParameter parameter = levelMapping.get(matchingLevels.get(0)).duplicate();
				parameter.setLevel(level);
				parameter.setAcronym("p" + level);
				matchingLevels.forEach(lvl -> {
					final LikelihoodParameter likelihoodParameter = levelMapping.get(lvl);
					parameterMapper.put(likelihoodParameter, parameter);
					acronymMappers.put(likelihoodParameter.getAcronym(), parameter);
					levelMappers.put(likelihoodParameter.getTypeName() + "-+-" + likelihoodParameter.getLevel(),
							parameter);
				});
				parameters.add(parameter);
				this.parameters.add(parameter);
			}
		});

		parameters.sort((p1, p2) -> p1.getLevel().compareTo(p2.getLevel()));
		if (IntStream.range(0, parameters.size() - 1)
				.anyMatch(i -> parameters.get(i).getValue() >= parameters.get(i + 1).getValue())) {
			final double maxValue = parameters.stream().mapToDouble(LikelihoodParameter::getValue).max().orElse(12);
			computeLikelihoods(maxValue, parameters);
			LikelihoodParameter.ComputeScales(parameters);
		}
	}

	/**
	 * Sets up the impacts by mapping impact parameters to their corresponding levels.
	 * 
	 * @param mappers           a map of levels and matching levels
	 * @param impactParameters  a list of impact parameters
	 */
	private void setUpImpacts(Map<Integer, List<Integer>> mappers, List<ImpactParameter> impactParameters) {
		final Map<ScaleType, List<ImpactParameter>> mappedImpacts = new LinkedHashMap<>();
		impactParameters.stream().collect(Collectors.groupingBy(ImpactParameter::getType)).forEach((type, impacts) -> {
			Map<Integer, ImpactParameter> levelMapping = impacts.stream()
					.collect(Collectors.toMap(ImpactParameter::getLevel, Function.identity()));
			mappedImpacts.put(type, new ArrayList<>(mappers.size()));
			mappers.forEach((level, matchingLevels) -> {
				if (matchingLevels.isEmpty()) {
					ImpactParameter parameter = new ImpactParameter(type, level, type.getAcronym() + level);
					parameters.add(parameter);
					mappedImpacts.get(type).add(parameter);
				} else {
					ImpactParameter parameter = levelMapping.get(matchingLevels.get(0)).duplicate();
					parameter.setAcronym(type.getAcronym() + level);
					parameter.setLevel(level);
					matchingLevels.forEach(lvl -> {
						ImpactParameter impactParameter = levelMapping.get(lvl);
						parameterMapper.put(impactParameter, parameter);
						acronymMappers.put(impactParameter.getAcronym(), parameter);
						levelMappers.put(impactParameter.getTypeName() + "-+-" + impactParameter.getLevel(), parameter);
					});
					parameters.add(parameter);
					mappedImpacts.get(type).add(parameter);
				}
			});
		});

		final double maxValue = impactParameters.stream().mapToDouble(i -> i.getValue().doubleValue()).max()
				.orElse(300000);
		mappedImpacts.forEach((type, impacts) -> {
			impacts.sort((p1, p2) -> p1.getLevel().compareTo(p2.getLevel()));
			if (IntStream.range(0, impacts.size() - 1)
					.anyMatch(i -> impacts.get(i).getValue() >= impacts.get(i + 1).getValue())) {
				computeImpacts(maxValue, impacts);
				ImpactParameter.ComputeScales(impacts);
			}
		});

	}

	/**
	 * Computes the likelihoods based on the given maximum value and a list of LikelihoodParameter objects.
	 *
	 * @param maxValue    the maximum value to be used for computing the likelihoods
	 * @param likelihoods the list of LikelihoodParameter objects to be updated with computed values
	 */
	public static void computeLikelihoods(double maxValue, List<LikelihoodParameter> likelihoods) {
		final int maxLevel = likelihoods.size();
		double currentValue = maxValue;
		// if (maxLevel % 2 == 0) {
		for (int level = maxLevel - 1; level >= 0; level--) {
			if (level == (maxLevel - 1))
				likelihoods.get(level).setValue(currentValue);
			else
				likelihoods.get(level).setValue(currentValue *= 0.5);
		}
		/*
		 * } else {
		 * LikelihoodParameter prev = null;
		 * for (int level = maxLevel - 2; level > 0; level -= 2) {
		 * LikelihoodParameter current = likelihoods.get(level),
		 * next = prev == null ? likelihoods.get(level + 1) : prev;
		 * prev = likelihoods.get(level - 1);
		 * if (prev.getLevel() == maxLevel)
		 * prev.setValue(currentValue);
		 * else
		 * prev.setValue(currentValue *= 0.5);
		 * current.setValue(Math.sqrt(next.getValue() * prev.getValue()));
		 * }
		 * }
		 */

	}

	/**
	 * Computes the impacts based on the given maximum value and list of impact parameters.
	 *
	 * @param maxValue The maximum value to compute impacts for.
	 * @param impacts  The list of impact parameters.
	 */
	public static void computeImpacts(double maxValue, List<ImpactParameter> impacts) {
		final int maxLevel = impacts.size();
		double currentValue = maxValue;
		// if (maxLevel % 2 == 0) {
		for (int level = maxLevel - 1; level >= 0; level--) {
			if (level == (maxLevel - 1))
				impacts.get(level).setValue(currentValue);
			else
				impacts.get(level).setValue(currentValue *= 0.5);
		}
		/*
		 * } else {
		 * ImpactParameter prev = null;
		 * for (int level = maxLevel - 2; level > 0; level -= 2) {
		 * ImpactParameter current = impacts.get(level), next = prev == null ?
		 * impacts.get(level + 1) : prev;
		 * prev = impacts.get(level - 1);
		 * if (prev.getLevel() == maxLevel)
		 * prev.setValue(currentValue);
		 * else
		 * prev.setValue(currentValue *= 0.5);
		 * current.setValue(Math.sqrt(next.getValue() * prev.getValue()));
		 * }
		 * }
		 */

	}

	/**
	 * Initializes the ScaleLevelConvertor with the specified capacity.
	 *
	 * @param i the initial capacity of the LinkedHashMaps
	 */
	private void initialise(int i) {
		acronymMappers = new LinkedHashMap<>(i);
		levelMappers = new LinkedHashMap<>(i);
		parameterMapper = new LinkedHashMap<>(i);
	}

	/**
	 * Returns the map of acronym mappers.
	 *
	 * @return The map of acronym mappers.
	 */
	protected Map<String, IBoundedParameter> getAcronymMappers() {
		return acronymMappers;
	}

	/**
	 * Sets the acronym mappers for the ScaleLevelConvertor.
	 *
	 * @param acronymMappers the map of acronym mappers to set
	 */
	protected void setAcronymMappers(Map<String, IBoundedParameter> acronymMappers) {
		this.acronymMappers = acronymMappers;
	}

	/**
	 * Returns the level mappers map.
	 *
	 * @return the level mappers map
	 */
	protected Map<String, IBoundedParameter> getLevelMappers() {
		return levelMappers;
	}

	/**
	 * Sets the level mappers for the ScaleLevelConvertor.
	 *
	 * @param levelMappers a map of level mappers to be set
	 */
	protected void setLevelMappers(Map<String, IBoundedParameter> levelMappers) {
		this.levelMappers = levelMappers;
	}

	/**
	 * Returns the parameter mapper, which is a map of bounded parameters.
	 *
	 * @return the parameter mapper
	 */
	protected Map<IBoundedParameter, IBoundedParameter> getParameterMapper() {
		return parameterMapper;
	}

	/**
	 * Sets the parameter mapper for this ScaleLevelConvertor.
	 *
	 * @param parameterMapper the parameter mapper to be set
	 */
	protected void setParameterMapper(Map<IBoundedParameter, IBoundedParameter> parameterMapper) {
		this.parameterMapper = parameterMapper;
	}

	/**
	 * Returns the list of bounded parameters.
	 *
	 * @return the list of bounded parameters
	 */
	public List<IBoundedParameter> getParameters() {
		return parameters;
	}

	/**
	 * Sets the list of parameters for the ScaleLevelConvertor.
	 *
	 * @param parameters the list of IBoundedParameter objects to set
	 */
	protected void setParameters(List<IBoundedParameter> parameters) {
		this.parameters = parameters;
	}

	/**
	 * finds the parameters corresponding to likelihood parameter
	 * */
	public IBoundedParameter find(IBoundedParameter parameter) {
		return parameterMapper.get(parameter);
	}

	public IBoundedParameter find(Integer level, String type) {
		return levelMappers.get(type + "-+-" + level);
	}

	public IBoundedParameter find(String acronym) {
		return acronymMappers.get(acronym);
	}

	/**
	 * Clears all the mappings and parameters in the ScaleLevelConvertor.
	 */
	public void clear() {
		this.acronymMappers.clear();
		this.levelMappers.clear();
		this.parameterMapper.clear();
		this.parameters.clear();
	}

	/**
	 * Returns a collection of deletable IBoundedParameters.
	 *
	 * @return a collection of deletable IBoundedParameters
	 */
	public Collection<IBoundedParameter> getDeletables() {
		return parameterMapper.keySet();
	}

}
