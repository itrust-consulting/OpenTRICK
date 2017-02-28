/**
 * 
 */
package lu.itrust.business.TS.model.parameter.value.impl;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;

import lu.itrust.business.TS.model.parameter.ILevelParameter;
import lu.itrust.business.TS.model.parameter.value.IValue;
import lu.itrust.business.TS.model.parameter.value.NumericValue;

/**
 * @author eomar
 *
 */
@Entity
@AttributeOverride(name = "id", column = @Column(name="idRealValue"))
public class RealValue extends NumericValue {

	/**
	 * 
	 */
	public RealValue() {
	}

	public RealValue(Double value, ILevelParameter parameter) {
		super(value, parameter);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.model.assessment.value.IValue#getReal()
	 */
	@Access(AccessType.PROPERTY)
	@Column(name = "dtValue")
	@Override
	public Double getReal() {
		return getNumber().doubleValue();
	}
	
	public void setReal(double real){
		setNumber(real);
	}
	
	@Override
	public boolean merge(IValue value) {
		if (value == null || !(value instanceof RealValue))
			return false;
		setReal(value.getReal());
		setParameter(value.getParameter());
		return true;
	}

}
