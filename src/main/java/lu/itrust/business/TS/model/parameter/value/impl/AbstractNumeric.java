/**
 * 
 */
package lu.itrust.business.TS.model.parameter.value.impl;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import lu.itrust.business.TS.model.parameter.ILevelParameter;

/**
 * @author eomar
 *
 */
@MappedSuperclass
public abstract class AbstractNumeric extends ParameterValue {

	@Transient
	protected Number number;

	/**
	 * 
	 */
	public AbstractNumeric() {
	}

	/**
	 * @param value
	 * @param parameter
	 */
	public AbstractNumeric(String name, Number value, ILevelParameter parameter) {
		super(name, parameter);
		this.number = value;
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
