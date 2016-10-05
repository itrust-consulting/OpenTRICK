/**
 * 
 */
package lu.itrust.business.TS.model.assessment.value;

import lu.itrust.business.TS.model.parameter.ExtendedParameter;

/**
 * @author eomar
 *
 */
public class ExtendedValue extends AcronymValue {

	public ExtendedValue(ExtendedParameter value) {
		super(value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.model.assessment.value.IValue#getLevel()
	 */
	@Override
	public Integer getLevel() {
		return getParameter().getLevel();
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.model.assessment.value.AcronymValue#getParameter()
	 */
	@Override
	public ExtendedParameter getParameter() {
		return (ExtendedParameter) super.getParameter();
	}

	

}
