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
import lu.itrust.business.TS.model.parameter.ILevelParameter;
import lu.itrust.business.TS.model.parameter.IParameter;
import lu.itrust.business.TS.model.parameter.impl.ImpactParameter;
import lu.itrust.business.TS.model.parameter.impl.SimpleParameter;

/**
 * @author eomar
 *
 */
@Deprecated
public class ParameterConvertor {

	private List<ImpactParameter> impactsParameters;

	private List<ImpactParameter> probabilityParameters;

	private Map<String, ImpactParameter> mapProbabilities;

	private Map<String, ImpactParameter> mapImpacts;

	public ParameterConvertor(List<ImpactParameter> impacts, List<ImpactParameter> probabilities) {
		setImpactsParameters(impacts);
		setProbabilityParameters(probabilities);
	}

	public ParameterConvertor(List<? extends SimpleParameter> simpleParameters) {
		setImpactsParameters(new ArrayList<ImpactParameter>(11));
		setProbabilityParameters(new ArrayList<ImpactParameter>(11));
		for (IParameter parameter : simpleParameters) {
			if (parameter instanceof ImpactParameter) {
				ImpactParameter impactParameter = (ImpactParameter) parameter;
				if (impactParameter.getType().getName().equals(Constant.PARAMETERTYPE_TYPE_PROPABILITY_NAME))
					probabilityParameters.add(impactParameter);
				else if (impactParameter.getType().getName().equals(Constant.PARAMETERTYPE_TYPE_IMPACT_NAME))
					impactsParameters.add(impactParameter);
			}
		}
		impactsParameters.sort(Comporator());
		probabilityParameters.sort(Comporator());
	}

	private Comparator<? super ImpactParameter> Comporator() {
		return (P1, P2) -> Integer.compare(P1.getLevel(), P2.getLevel());
	}

	public List<ImpactParameter> getImpactsParameters() {
		return impactsParameters;
	}

	protected synchronized void initialiseMapProbrabilities() {
		if (probabilityParameters == null)
			throw new TrickException("error.data.not_initialise", "Data does not initialise");
		if (mapProbabilities == null || mapProbabilities.size() != probabilityParameters.size())
			mapProbabilities = probabilityParameters.stream().collect(Collectors.toMap(ImpactParameter::getAcronym, Function.identity()));
	}

	protected synchronized void initialiseMapImpact() {
		if (impactsParameters == null)
			throw new TrickException("error.data.not_initialise", "Data does not initialise");
		if (mapImpacts == null || mapImpacts.size() != impactsParameters.size())
			mapImpacts = impactsParameters.stream().collect(Collectors.toMap(ImpactParameter::getAcronym, Function.identity()));
	}

	protected void setImpactsParameters(List<ImpactParameter> impactsParameters) {
		this.impactsParameters = impactsParameters;
	}

	public List<ImpactParameter> getProbabilityParameters() {
		return probabilityParameters;
	}

	protected void setProbabilityParameters(List<ImpactParameter> probabilityParameters) {
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

	private ImpactParameter findByValue(double value, List<ImpactParameter> parameters) {
		int mid = parameters.size() / 2;
		if (parameters.get(mid).getBounds().isInRange(value))
			return parameters.get(mid);
		else if (parameters.get(mid).getBounds().getFrom() > value)
			return findByValue(value, parameters.subList(0, mid));
		else
			return findByValue(value, parameters.subList(mid, parameters.size()));
	}

	private ImpactParameter findByLevel(int level, List<ImpactParameter> parameters) {
		int mid = parameters.size() / 2;
		if (parameters.get(mid).getLevel() == level)
			return parameters.get(mid);
		else if (parameters.get(mid).getLevel() > level)
			return findByLevel(level, parameters.subList(0, mid));
		else
			return findByLevel(level, parameters.subList(mid, parameters.size()));
	}

	private ImpactParameter findByAcronym(String acronym, Map<String, ImpactParameter> parameters) throws TrickException {
		ImpactParameter parameter = parameters.get(acronym);
		if (parameter == null)
			throw new TrickException("error.acronym.not_found", String.format("Acronym (%s) cannot be resolve", acronym), acronym);
		return parameter;
	}

	public ILevelParameter getImpact(String impact) {
		try {
			return findByValue(Double.parseDouble(impact), impactsParameters);
		} catch (NumberFormatException e) {
			return findByAcronym(impact, getMapImpacts());
		}
	}

	public ILevelParameter getImpact(double impact) {
		return findByValue(impact, impactsParameters);
	}

	public ILevelParameter getProbability(String likelihood) {
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

	public ILevelParameter getProbability(double likelihood) {
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
	public Map<String, ImpactParameter> getMapProbabilities() {
		if (mapProbabilities == null)
			initialiseMapProbrabilities();
		return mapProbabilities;
	}

	/**
	 * @param mapProbabilities
	 *            the mapProbabilities to set
	 */
	public void setMapProbabilities(Map<String, ImpactParameter> mapProbabilities) {
		this.mapProbabilities = mapProbabilities;
	}

	/**
	 * @return the mapImpacts
	 */
	public Map<String, ImpactParameter> getMapImpacts() {
		if (mapImpacts == null)
			initialiseMapImpact();
		return mapImpacts;
	}

	/**
	 * @param mapImpacts
	 *            the mapImpacts to set
	 */
	public void setMapImpacts(Map<String, ImpactParameter> mapImpacts) {
		this.mapImpacts = mapImpacts;
	}

}
