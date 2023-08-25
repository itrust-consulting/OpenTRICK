/**
 * 
 */
package lu.itrust.business.ts.model.parameter.value;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;

import lu.itrust.business.ts.model.parameter.ILevelParameter;

/**
 * @author eomar
 *
 */
@MappedSuperclass
public abstract class NumericValue extends AbstractValue {

	@Transient
	protected Number number;

	/**
	 * 
	 */
	protected NumericValue() {
	}

	/**
	 * @param value
	 * @param parameter
	 */
	protected NumericValue(Number value, ILevelParameter parameter) {
		super(parameter);
		this.number = value;
	}

	/**
	 * @return the number
	 */
	protected Number getNumber() {
		return number;
	}

	/**
	 * @param number
	 *            the number to set
	 */
	protected void setNumber(Number number) {
		this.number = number;
	}


}
