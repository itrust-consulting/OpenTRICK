/**
 * 
 */
package lu.itrust.business.TS.model.parameter.helper.value;

import lu.itrust.business.TS.model.parameter.AcronymParameter;

/**
 * @author eomar
 *
 */
public class DefaultRealValue extends AbstractNumeric {

	public DefaultRealValue(Double value, AcronymParameter parameter) {
		super(value, parameter);
		
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.model.assessment.value.IValue#getLevel()
	 */
	@Override
	public Integer getLevel() {
		return getParameter().getLevel();
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.model.assessment.value.IValue#getReal()
	 */
	@Override
	public Double getReal() {
		return getNumber().doubleValue();
	}

}
