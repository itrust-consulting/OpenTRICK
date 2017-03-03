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
@AttributeOverride(name = "id", column = @Column(name = "idLevelValue"))
public class LevelValue extends NumericValue {

	/**
	 * 
	 */
	public LevelValue() {
	}

	public LevelValue(Integer value, ILevelParameter parameter) {
		super(value, parameter);
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

	public void setLevel(int level) {
		setNumber(level);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.model.assessment.value.IValue#getReal()
	 */
	@Override
	public Double getReal() {
		return getParameter().getValue();
	}

	@Override
	public boolean merge(IValue value) {
		if (value == null || !(value instanceof LevelValue))
			return false;
		setLevel(value.getLevel());
		setParameter(value.getParameter());
		return true;
	}

}
