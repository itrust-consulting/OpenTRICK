/**
 * 
 */
package lu.itrust.business.TS.model.assessment.value;

import lu.itrust.business.TS.model.parameter.ExtendedParameter;

/**
 * @author eomar
 *
 */
public class DefaultExtendedValue implements IValue {
	
	private double value;
	
	private ExtendedParameter parameter;
	
	/**
	 * @param value
	 * @param parameter
	 */
	public DefaultExtendedValue(double value, ExtendedParameter parameter) {
		this.value = value;
		this.parameter = parameter;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.model.assessment.value.IValue#getLevel()
	 */
	@Override
	public Integer getLevel() {
		return getParameter().getLevel();
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.model.assessment.value.IValue#getVariable()
	 */
	@Override
	public String getVariable() {
		return getParameter().getAcronym();
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.model.assessment.value.IValue#getNumeric()
	 */
	@Override
	public Double getNumeric() {
		return value;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.model.assessment.value.IValue#getParameter()
	 */
	@Override
	public ExtendedParameter getParameter() {
		return parameter;
	}

}
