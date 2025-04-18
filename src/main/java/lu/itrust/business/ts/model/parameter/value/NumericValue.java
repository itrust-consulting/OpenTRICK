package lu.itrust.business.ts.model.parameter.value;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;

import lu.itrust.business.ts.model.parameter.ILevelParameter;


/**
 * The `NumericValue` class is an abstract class that represents a numeric value in the system.
 * It extends the `AbstractValue` class and provides functionality for storing and retrieving a numeric value.
 * 
 * This class is intended to be extended by concrete numeric value classes.
 * 
 * @see AbstractValue
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
