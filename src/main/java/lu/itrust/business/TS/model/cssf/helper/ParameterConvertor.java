/**
 * 
 */
package lu.itrust.business.TS.model.cssf.helper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.parameter.ExtendedParameter;
import lu.itrust.business.TS.model.parameter.Parameter;

/**
 * @author eomar
 *
 */
public class ParameterConvertor {

	private List<ExtendedParameter> impactsParameters;

	private List<ExtendedParameter> probabilityParameters;

	private Map<String, ExtendedParameter> mapProbabilities;

	private Map<String, ExtendedParameter> mapImpacts;

	public ParameterConvertor(List<ExtendedParameter> impacts, List<ExtendedParameter> probabilities) {
		setImpactsParameters(impacts);
		setProbabilityParameters(probabilities);
	}

	public ParameterConvertor(List<? extends Parameter> parameters) {
		setImpactsParameters(new ArrayList<ExtendedParameter>(11));
		setProbabilityParameters(new ArrayList<ExtendedParameter>(11));
		for (Parameter parameter : parameters) {
			if (parameter instanceof ExtendedParameter) {
				ExtendedParameter extendedParameter = (ExtendedParameter) parameter;
				if (extendedParameter.getType().getLabel().equals(Constant.PARAMETERTYPE_TYPE_PROPABILITY_NAME))
					probabilityParameters.add(extendedParameter);
				else
					impactsParameters.add(extendedParameter);
			}
		}
		impactsParameters.sort(Comporator());
		probabilityParameters.sort(Comporator());
	}

	private Comparator<? super ExtendedParameter> Comporator() {
		return (P1, P2) -> Integer.compare(P1.getLevel(), P2.getLevel());
	}

	public List<ExtendedParameter> getImpactsParameters() {
		return impactsParameters;
	}

	protected synchronized void initialiseMapProbrabilities() {
		if (probabilityParameters == null)
			throw new TrickException("error.data.not_initialise", "Data does not initialise");
		if (mapProbabilities == null || mapProbabilities.size() != probabilityParameters.size())
			mapProbabilities = probabilityParameters.stream().collect(Collectors.toMap(ExtendedParameter::getAcronym, Function.identity()));
	}

	protected synchronized void initialiseMapImpact() {
		if (impactsParameters == null)
			throw new TrickException("error.data.not_initialise", "Data does not initialise");
		if (mapImpacts == null || mapImpacts.size() != impactsParameters.size())
			mapImpacts = impactsParameters.stream().collect(Collectors.toMap(ExtendedParameter::getAcronym, Function.identity()));
	}

	protected void setImpactsParameters(List<ExtendedParameter> impactsParameters) {
		this.impactsParameters = impactsParameters;
	}

	public List<ExtendedParameter> getProbabilityParameters() {
		return probabilityParameters;
	}

	protected void setProbabilityParameters(List<ExtendedParameter> probabilityParameters) {
		this.probabilityParameters = probabilityParameters;
	}

	public int getImpactLevel(double value) {
		return findByValue(value, impactsParameters).getLevel();
	}

	public int getImpactLevel(String acronym) throws TrickException {
		return findByAcronym(acronym, getMapImpacts()).getLevel();
	}

	public String getImpactAcronym(double value) {
		return findByValue(value, impactsParameters).getAcronym();
	}

	public String getImpactAcronym(int level) {
		return findByLevel(level, impactsParameters).getAcronym();
	}

	public double getImpactValue(int level) {
		return findByLevel(level, impactsParameters).getValue();
	}

	public double getImpactValue(String acronym) throws TrickException {
		return findByAcronym(acronym, getMapImpacts()).getValue();
	}

	public int getProbabiltyLevel(double value) {
		return findByValue(value, probabilityParameters).getLevel();
	}

	public int getProbabiltyLevel(String acronym) throws TrickException {
		return findByAcronym(acronym, getMapProbabilities()).getLevel();
	}

	public String getProbabiltyAcronym(double value) {
		return findByValue(value, probabilityParameters).getAcronym();
	}

	public String getProbabiltyAcronym(int level) {
		return findByLevel(level, probabilityParameters).getAcronym();
	}

	public double getProbabiltyValue(int level) {
		return findByLevel(level, probabilityParameters).getValue();
	}

	public double getProbabiltyValue(String acronym) throws TrickException {
		return findByAcronym(acronym, getMapProbabilities()).getValue();
	}

	private ExtendedParameter findByValue(double value, List<ExtendedParameter> parameters) {
		int mid = parameters.size() / 2;
		if (parameters.get(mid).getBounds().isInRange(value))
			return parameters.get(mid);
		else if (parameters.get(mid).getBounds().getFrom() > value)
			return findByValue(value, parameters.subList(0, mid));
		else
			return findByValue(value, parameters.subList(mid, parameters.size()));
	}

	private ExtendedParameter findByLevel(int level, List<ExtendedParameter> parameters) {
		int mid = parameters.size() / 2;
		if (parameters.get(mid).getLevel() == level)
			return parameters.get(mid);
		else if (parameters.get(mid).getLevel() > level)
			return findByLevel(level, parameters.subList(0, mid));
		else
			return findByLevel(level, parameters.subList(mid, parameters.size()));
	}

	private ExtendedParameter findByAcronym(String acronym, Map<String, ExtendedParameter> parameters) throws TrickException {
		ExtendedParameter parameter = parameters.get(acronym);
		if (parameter == null)
			throw new TrickException("error.acronym.not_found", String.format("Acronym (%s) cannot be resolve", acronym), acronym);
		return parameter;
	}

	public ExtendedParameter getImpact(String impact) {
		try {
			return findByValue(Double.parseDouble(impact), impactsParameters);
		} catch (NumberFormatException e) {
			return findByAcronym(impact, getMapImpacts());
		}
	}

	public ExtendedParameter getImpact(double impact) {
		return findByValue(impact, impactsParameters);
	}

	public ExtendedParameter getProbability(String likelihood) {
		try {
			return findByAcronym(likelihood, getMapProbabilities());
		} catch (TrickException e) {
			try {
				return findByValue(Double.parseDouble(likelihood), probabilityParameters);
			} catch (NumberFormatException e1) {
				throw e;
			}
		}
	}

	public ExtendedParameter getProbability(double likelihood) {
		return findByValue(likelihood, probabilityParameters);
	}

	public double findImpact(String impact) {
		try {
			return findByValue(Double.parseDouble(impact), impactsParameters).getValue();
		} catch (NumberFormatException e) {
			return findByAcronym(impact, getMapImpacts()).getValue();
		}
	}

	public double findProbability(String likelihood) {
		try {
			return findByAcronym(likelihood, getMapProbabilities()).getValue();
		} catch (TrickException e) {
			try {
				return findByValue(Double.parseDouble(likelihood), probabilityParameters).getValue();
			} catch (NumberFormatException e1) {
				throw e;
			}
		}
	}

	/**
	 * @return the mapProbabilities
	 */
	public Map<String, ExtendedParameter> getMapProbabilities() {
		if (mapProbabilities == null)
			initialiseMapProbrabilities();
		return mapProbabilities;
	}

	/**
	 * @param mapProbabilities
	 *            the mapProbabilities to set
	 */
	public void setMapProbabilities(Map<String, ExtendedParameter> mapProbabilities) {
		this.mapProbabilities = mapProbabilities;
	}

	/**
	 * @return the mapImpacts
	 */
	public Map<String, ExtendedParameter> getMapImpacts() {
		if (mapImpacts == null)
			initialiseMapImpact();
		return mapImpacts;
	}

	/**
	 * @param mapImpacts
	 *            the mapImpacts to set
	 */
	public void setMapImpacts(Map<String, ExtendedParameter> mapImpacts) {
		this.mapImpacts = mapImpacts;
	}

}
