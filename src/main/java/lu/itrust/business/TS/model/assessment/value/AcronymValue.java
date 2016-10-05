/**
 * 
 */
package lu.itrust.business.TS.model.assessment.value;

import lu.itrust.business.TS.model.parameter.AcronymParameter;

/**
 * @author eomar
 *
 */
public abstract class AcronymValue implements IValue {

	private AcronymParameter parameter;

	/**
	 * @param value
	 */
	public AcronymValue(AcronymParameter value) {
		this.parameter = value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.model.assessment.helper.IValue#getVariable()
	 */
	@Override
	public String getVariable() {
		return parameter.getAcronym();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.model.assessment.helper.IValue#getNumeric()
	 */
	@Override
	public Double getNumeric() {
		return parameter.getValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.model.assessment.value.IValue#getParameter()
	 */
	@Override
	public AcronymParameter getParameter() {
		return parameter;
	}

}
