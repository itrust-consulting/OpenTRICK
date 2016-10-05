/**
 * 
 */
package lu.itrust.business.TS.model.assessment.value;

import lu.itrust.business.TS.model.parameter.AcronymParameter;
import lu.itrust.business.TS.model.parameter.DynamicParameter;

/**
 * @author eomar
 *
 */
public class DefaultDynamicValue implements IValue {

	private int level;

	private double value;

	private DynamicParameter parameter;

	/**
	 * @param level
	 * @param value
	 * @param variable
	 */
	public DefaultDynamicValue(int level, double value, DynamicParameter parameter) {
		this.level = level;
		this.value = value;
		this.parameter = parameter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.model.assessment.value.IValue#getLevel()
	 */
	@Override
	public Integer getLevel() {
		return level;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.model.assessment.value.IValue#getVariable()
	 */
	@Override
	public String getVariable() {
		return getParameter().getAcronym();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.model.assessment.value.IValue#getNumeric()
	 */
	@Override
	public Double getNumeric() {
		return value;
	}

	@Override
	public AcronymParameter getParameter() {
		return parameter;
	}

}
