/**
 * 
 */
package lu.itrust.business.TS.model.parameter.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.helper.NaturalOrderComparator;
import lu.itrust.business.TS.model.parameter.IBoundedParameter;
import lu.itrust.business.TS.model.parameter.impl.ImpactParameter;
import lu.itrust.business.TS.model.parameter.impl.LikelihoodParameter;
import lu.itrust.business.TS.model.scale.ScaleType;

/**
 * @author eomar
 *
 */
public class ScaleLevelConvertor {

	private Map<String, IBoundedParameter> acronymMappers = Collections.emptyMap();

	private Map<String, IBoundedParameter> levelMappers = Collections.emptyMap();

	private Map<IBoundedParameter, IBoundedParameter> parameterMapper = Collections.emptyMap();

	private List<IBoundedParameter> parameters = new LinkedList<>();

	public ScaleLevelConvertor(Map<Integer, List<Integer>> mappers, List<ImpactParameter> impactParameters, List<LikelihoodParameter> likelihoods) {
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
			throw new TrickException("error.scale.level.migrate.convertor.initialise", "Scale level cannot be migrated", e);
		}

	}

	private void setUpLikelihood(Map<Integer, List<Integer>> mappers, List<LikelihoodParameter> likelihoods) {
		Map<Integer, LikelihoodParameter> levelMapping = likelihoods.stream().collect(Collectors.toMap(LikelihoodParameter::getLevel, Function.identity()));
		List<LikelihoodParameter> parameters = new ArrayList<>(mappers.size());
		mappers.forEach((level, matchingLevels) -> {
			if (matchingLevels.isEmpty()) {
				LikelihoodParameter parameter = new LikelihoodParameter(level, "p" + level);
				parameters.add(parameter);
				this.parameters.add(parameter);
			} else {
				LikelihoodParameter parameter = levelMapping.get(matchingLevels.get(0)).duplicate();
				parameter.setLevel(level);
				parameter.setAcronym("p" + level);
				matchingLevels.forEach(lvl -> {
					LikelihoodParameter likelihoodParameter = levelMapping.get(lvl);
					parameterMapper.put(likelihoodParameter, parameter);
					acronymMappers.put(likelihoodParameter.getAcronym(), parameter);
					levelMappers.put(likelihoodParameter.getTypeName() + "-+-" + likelihoodParameter.getLevel(), parameter);
				});
				parameters.add(parameter);
				this.parameters.add(parameter);
			}
		});
		double maxValue = likelihoods.stream().mapToDouble(LikelihoodParameter::getValue).max().orElse(12);
		computeLikelihoods(mappers.size(), maxValue, parameters);
		LikelihoodParameter.ComputeScales(parameters);
	}

	private void setUpImpacts(Map<Integer, List<Integer>> mappers, List<ImpactParameter> impactParameters) {
		Map<ScaleType, List<ImpactParameter>> mappedImpacts = new LinkedHashMap<>();
		impactParameters.stream().collect(Collectors.groupingBy(ImpactParameter::getType)).forEach((type, impacts) -> {
			Map<Integer, ImpactParameter> levelMapping = impacts.stream().collect(Collectors.toMap(ImpactParameter::getLevel, Function.identity()));
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
		double maxValue = impactParameters.stream().mapToDouble(i -> i.getValue().doubleValue()).max().orElse(300000);
		mappedImpacts.forEach((type, impacts) -> {
			impacts.sort((p1, p2) -> p1.getValue().compareTo(p2.getValue()));
			computeImpacts(mappers.size(), maxValue, impacts);
			ImpactParameter.ComputeScales(impacts);
		});

	}

	private void computeLikelihoods(int maxLevel, double maxValue, List<LikelihoodParameter> likelihoods) {
		double currentValue = maxValue;
		if (maxLevel % 2 == 0) {
			for (int level = maxLevel - 1; level >= 0; level--) {
				if (level == (maxLevel - 1))
					likelihoods.get(level).setValue(currentValue);
				else
					likelihoods.get(level).setValue(currentValue *= 0.5);
			}
		} else {
			LikelihoodParameter prev = null;
			for (int level = maxLevel - 2; level > 0; level -= 2) {
				LikelihoodParameter current = likelihoods.get(level), next = prev == null ? likelihoods.get(level + 1) : prev;
				prev = likelihoods.get(level - 1);
				if (prev.getLevel() == maxLevel)
					prev.setValue(currentValue);
				else
					prev.setValue(currentValue *= 0.5);
				current.setValue(Math.sqrt(next.getValue() * prev.getValue()));
			}
		}

	}

	private void computeImpacts(int maxLevel, double maxValue, List<ImpactParameter> impacts) {
		double currentValue = maxValue;
		if (maxLevel % 2 == 0) {
			for (int level = maxLevel - 1; level >= 0; level--) {
				if (level == (maxLevel - 1))
					impacts.get(level).setValue(currentValue);
				else
					impacts.get(level).setValue(currentValue *= 0.5);
			}
		} else {
			ImpactParameter prev = null;
			for (int level = maxLevel - 2; level > 0; level -= 2) {
				ImpactParameter current = impacts.get(level), next = prev == null ? impacts.get(level + 1) : prev;
				prev = impacts.get(level - 1);
				if (prev.getLevel() == maxLevel)
					prev.setValue(currentValue);
				else
					prev.setValue(currentValue *= 0.5);
				current.setValue(Math.sqrt(next.getValue() * prev.getValue()));
			}
		}

	}

	private void initialise(int i) {
		acronymMappers = new LinkedHashMap<>(i);
		levelMappers = new LinkedHashMap<>(i);
		parameterMapper = new LinkedHashMap<>(i);
	}

	protected Map<String, IBoundedParameter> getAcronymMappers() {
		return acronymMappers;
	}

	protected void setAcronymMappers(Map<String, IBoundedParameter> acronymMappers) {
		this.acronymMappers = acronymMappers;
	}

	protected Map<String, IBoundedParameter> getLevelMappers() {
		return levelMappers;
	}

	protected void setLevelMappers(Map<String, IBoundedParameter> levelMappers) {
		this.levelMappers = levelMappers;
	}

	protected Map<IBoundedParameter, IBoundedParameter> getParameterMapper() {
		return parameterMapper;
	}

	protected void setParameterMapper(Map<IBoundedParameter, IBoundedParameter> parameterMapper) {
		this.parameterMapper = parameterMapper;
	}

	public List<IBoundedParameter> getParameters() {
		return parameters;
	}

	protected void setParameters(List<IBoundedParameter> parameters) {
		this.parameters = parameters;
	}

	public IBoundedParameter find(IBoundedParameter parameter) {
		return parameterMapper.get(parameter);
	}

	public IBoundedParameter find(Integer level, String type) {
		return levelMappers.get(type + "-+-" + level);
	}

	public IBoundedParameter find(String acronym) {
		return acronymMappers.get(acronym);
	}

	public void clear() {
		this.acronymMappers.clear();
		this.levelMappers.clear();
		this.parameterMapper.clear();
		this.parameters.clear();
	}

	public Collection<IBoundedParameter> getDeletables() {
		return parameterMapper.keySet();
	}

}
