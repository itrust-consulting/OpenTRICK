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
 * Represents a real value in the system.
 * Extends the NumericValue class.
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverride(name = "id", column = @Column(name = "idRealValue"))
public class RealValue extends NumericValue {

	/**
	 * Default constructor.
	 */
	public RealValue() {
	}

	/**
	 * Constructor that initializes the value and parameter.
	 * 
	 * @param value     The real value.
	 * @param parameter The level parameter associated with the value.
	 */
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

	/**
	 * Sets the real value.
	 * 
	 * @param real The real value to set.
	 */
	public void setReal(double real) {
		setNumber(real);
	}

	/**
	 * Merges the given value with this RealValue.
	 * 
	 * @param value the value to merge with
	 * @return true if the merge was successful, false otherwise
	 */
	@Override
	public boolean merge(IValue value) {
		if (value == null || !(value instanceof RealValue))
			return false;
		setReal(value.getReal());
		setParameter(((RealValue) value).getParameter());
		return true;
	}

	/**
	 * Returns the raw value of the RealValue object.
	 *
	 * @return the raw value as a Double
	 */
	@Override
	public Double getRaw() {
		return getReal();
	}

}
