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
public class DefaultLevelValue extends AbstractNumeric {

	/**
	 * 
	 */
	public DefaultLevelValue() {
	}

	public DefaultLevelValue(String name, Integer value, ILevelParameter parameter) {
		super(name, value, parameter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.model.assessment.value.IValue#getLevel()
	 */
	@Access(AccessType.PROPERTY)
	@Column(name = "dtLevel")
	@Override
	public Integer getLevel() {
		return getNumber().intValue();
	}
	
	public void setLevel(int level){
		setNumber(level);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.model.assessment.value.IValue#getReal()
	 */
	@Override
	public Double getReal() {
		return getParameter().getValue().doubleValue();
	}

}
