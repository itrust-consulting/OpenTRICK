/**
 * 
 */
package lu.itrust.business.ts.model.parameter.value.impl;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import lu.itrust.business.ts.model.parameter.ILevelParameter;
import lu.itrust.business.ts.model.parameter.value.IValue;
import lu.itrust.business.ts.model.parameter.value.NumericValue;

/**
 * @author eomar
 *
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverride(name = "id", column = @Column(name = "idRealValue"))
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
	 * @see lu.itrust.business.ts.model.assessment.value.IValue#getLevel()
	 */
	@Override
	public Integer getLevel() {
		return getParameter().getLevel();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.model.assessment.value.IValue#getReal()
	 */
	@Access(AccessType.PROPERTY)
	@Column(name = "dtValue")
	@Override
	public Double getReal() {
		return getNumber().doubleValue();
	}

	public void setReal(double real) {
		setNumber(real);
	}

	@Override
	public boolean merge(IValue value) {
		if (value == null || !(value instanceof RealValue))
			return false;
		setReal(value.getReal());
		setParameter(((RealValue) value).getParameter());
		return true;
	}

	@Override
	public Double getRaw() {
		return getReal();
	}

}
