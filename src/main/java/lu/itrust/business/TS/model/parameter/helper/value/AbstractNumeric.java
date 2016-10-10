/**
 * 
 */
package lu.itrust.business.TS.model.parameter.helper.value;

import lu.itrust.business.TS.model.parameter.AcronymParameter;

/**
 * @author eomar
 *
 */
public abstract  class AbstractNumeric implements IValue {
	
	private Number number;
	
	private AcronymParameter parameter;
	
	/**
	 * @param value
	 * @param parameter
	 */
	public AbstractNumeric(Number value, AcronymParameter parameter) {
		this.number = value;
		this.parameter = parameter;
	}

	/**
	 * @return the parameter
	 */
	public AcronymParameter getParameter() {
		return parameter;
	}

	/**
	 * @param parameter the parameter to set
	 */
	public void setParameter(AcronymParameter parameter) {
		this.parameter = parameter;
	}

	@Override
	public String getVariable() {
		return getParameter().getAcronym();
	}

	/**
	 * @return the number
	 */
	protected Number getNumber() {
		return number;
	}

	/**
	 * @param number the number to set
	 */
	protected void setNumber(Number number) {
		this.number = number;
	}
}
