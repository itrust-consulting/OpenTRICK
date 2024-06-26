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
 * Represents a level value in the system.
 * Extends the NumericValue class and implements the IValue interface.
 * Provides methods to get and set the level value, as well as merge with another value.
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
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
	 * @see lu.itrust.business.ts.model.assessment.value.IValue#getLevel()
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
	 * @see lu.itrust.business.ts.model.assessment.value.IValue#getReal()
	 */
	@Override
	public Double getReal() {
		return getParameter().getValue();
	}

	/**
	 * Merges the given value with this LevelValue.
	 * 
	 * @param value the value to merge with
	 * @return true if the merge was successful, false otherwise
	 */
	@Override
	public boolean merge(IValue value) {
		if (value == null || !(value instanceof LevelValue))
			return false;
		setLevel(value.getLevel());
		setParameter(((LevelValue) value).getParameter());
		return true;
	}

	/**
	 * Returns the raw value of the LevelValue object.
	 *
	 * @return the raw value as an Object
	 */
	@Override
	public Object getRaw() {
		return getVariable();
	}

}
