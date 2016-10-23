/**
 * 
 */
package lu.itrust.business.TS.model.parameter.value.impl;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

import lu.itrust.business.TS.model.parameter.ILevelParameter;

/**
 * @author eomar
 *
 */
@Entity
@PrimaryKeyJoinColumn(name="idDefaultLevelValue")
public class DefaultRealValue extends AbstractNumeric {

	/**
	 * 
	 */
	public DefaultRealValue() {
	}

	public DefaultRealValue(String name, Double value, ILevelParameter parameter) {
		super(name, value, parameter);
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

}
