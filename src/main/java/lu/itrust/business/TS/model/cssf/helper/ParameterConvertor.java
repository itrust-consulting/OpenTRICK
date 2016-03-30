/**
 * 
 */
package lu.itrust.business.TS.model.cssf.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.parameter.ExtendedParameter;
import lu.itrust.business.TS.model.parameter.Parameter;
import lu.itrust.business.TS.model.parameter.helper.ExtendedParameterComparator;

/**
 * @author eomar
 *
 */
public class ParameterConvertor {

	private List<ExtendedParameter> impactsParameters;

	private List<ExtendedParameter> probabilityParameters;

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
		Collections.sort(impactsParameters, new ExtendedParameterComparator());
		Collections.sort(probabilityParameters, new ExtendedParameterComparator());
	}

	public List<ExtendedParameter> getImpactsParameters() {
		return impactsParameters;
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
		return findByAcronym(acronym, impactsParameters).getLevel();
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
		return findByAcronym(acronym, impactsParameters).getValue();
	}

	public int getProbabiltyLevel(double value) {
		return findByValue(value, probabilityParameters).getLevel();
	}

	public int getProbabiltyLevel(String acronym) throws TrickException {
		return findByAcronym(acronym, probabilityParameters).getLevel();
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
		return findByAcronym(acronym, probabilityParameters).getValue();
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

	private ExtendedParameter findByAcronym(String acronym, List<ExtendedParameter> parameters) throws TrickException {
		for (ExtendedParameter extendedParameter : parameters)
			if (extendedParameter.getAcronym().equalsIgnoreCase(acronym))
				return extendedParameter;
		throw new TrickException("error.acronym.not_found", String.format("Acronym (%s) cannot be resolve", acronym), acronym);
	}

	public ExtendedParameter getImpact(String impact) {
		try {
			return findByValue(Double.parseDouble(impact), impactsParameters);
		} catch (NumberFormatException e) {
			return findByAcronym(impact, impactsParameters);
		}
	}

	public ExtendedParameter getProbability(String likelihood) {
		try {
			return findByAcronym(likelihood, probabilityParameters);
		} catch (TrickException e) {
			try {
				return findByValue(Double.parseDouble(likelihood), probabilityParameters);
			} catch (NumberFormatException e1) {
				throw e;
			}
		}
	}

}
