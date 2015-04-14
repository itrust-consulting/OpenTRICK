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

	public ParameterConvertor(List<Parameter> parameters) {
		setImpactsParameters(new ArrayList<ExtendedParameter>());
		setProbabilityParameters(new ArrayList<ExtendedParameter>());
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
		return findLevel(value, impactsParameters);
	}

	public int getImpactLevel(String acronym) throws TrickException {
		return findLevel(acronym, impactsParameters);
	}

	public String getImpactAcronym(double value) {
		return findAcronym(value, impactsParameters);
	}

	public String getImpactAcronym(int level) {
		return findAcronymByLevel(level, impactsParameters);
	}

	public double getImpactValue(int level) {
		return findValue(level, impactsParameters);
	}

	public double getImpactValue(String acronym) throws TrickException {
		return findValue(acronym, impactsParameters);
	}

	public int getProbabiltyLevel(double value) {
		return findLevel(value, probabilityParameters);
	}

	public int getProbabiltyLevel(String acronym) throws TrickException {
		return findLevel(acronym, probabilityParameters);
	}

	public String getProbabiltyAcronym(double value) {
		return findAcronym(value, probabilityParameters);
	}

	public String getProbabiltyAcronym(int level) {
		return findAcronymByLevel(level, probabilityParameters);
	}

	public double getProbabiltyValue(int level) {
		return findValue(level, probabilityParameters);
	}

	public double getProbabiltyValue(String acronym) throws TrickException {
		return findValue(acronym, probabilityParameters);
	}

	private int findLevel(double value, List<ExtendedParameter> parameters) {
		int mid = parameters.size() / 2;
		if (parameters.get(mid).getBounds().isInRange(value))
			return parameters.get(mid).getLevel();
		else if (parameters.get(mid).getBounds().getFrom() > value)
			return findLevel(value, parameters.subList(0, mid));
		else
			return findLevel(value, parameters.subList(mid, parameters.size()));
	}

	private int findLevel(String acronym, List<ExtendedParameter> parameters) throws TrickException {
		for (ExtendedParameter extendedParameter : parameters)
			if (extendedParameter.getAcronym().equalsIgnoreCase(acronym))
				return extendedParameter.getLevel();
		throw new TrickException("error.acronym.not_found", String.format("Acronym (%s) cannot be resolve", acronym), acronym);
	}

	private String findAcronymByLevel(int level, List<ExtendedParameter> parameters) {
		int mid = parameters.size() / 2;
		if (parameters.get(mid).getLevel() == level)
			return parameters.get(mid).getAcronym();
		else if (parameters.get(mid).getLevel() > level)
			return findAcronymByLevel(level, parameters.subList(0, mid));
		else
			return findAcronymByLevel(level, parameters.subList(mid, parameters.size()));
	}

	private String findAcronym(double value, List<ExtendedParameter> parameters) {
		int mid = parameters.size() / 2;
		if (parameters.get(mid).getBounds().isInRange(value))
			return parameters.get(mid).getAcronym();
		else if (parameters.get(mid).getBounds().getFrom() > value)
			return findAcronym(value, parameters.subList(0, mid));
		else
			return findAcronym(value, parameters.subList(mid, parameters.size()));
	}

	private double findValue(int level, List<ExtendedParameter> parameters) {
		int mid = parameters.size() / 2;
		if (parameters.get(mid).getLevel() == level)
			return parameters.get(mid).getValue();
		else if (parameters.get(mid).getLevel() > level)
			return findValue(level, parameters.subList(0, mid));
		else
			return findValue(level, parameters.subList(mid, parameters.size()));
	}

	private double findValue(String acronym, List<ExtendedParameter> parameters) throws TrickException {
		for (ExtendedParameter extendedParameter : parameters)
			if (extendedParameter.getAcronym().equalsIgnoreCase(acronym))
				return extendedParameter.getValue();
		throw new TrickException("error.acronym.not_found", String.format("Acronym (%s) cannot be resolve", acronym), acronym);
	}

}
