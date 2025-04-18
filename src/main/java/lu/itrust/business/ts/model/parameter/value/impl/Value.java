package lu.itrust.business.ts.model.parameter.value.impl;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import lu.itrust.business.ts.model.parameter.ILevelParameter;
import lu.itrust.business.ts.model.parameter.value.AbstractValue;
import lu.itrust.business.ts.model.parameter.value.IValue;

/**
 * Represents a value in the system.
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverride(name = "id", column = @Column(name = "idValue"))
public class Value extends AbstractValue {

	/**
	 * Default constructor.
	 */
	public Value() {
	}

	/**
	 * Constructor with parameter.
	 *
	 * @param parameter the level parameter associated with the value
	 */
	public Value(ILevelParameter parameter) {
		super(parameter);
	}

	/**
	 * Merges the given value with this value.
	 *
	 * @param value the value to merge
	 * @return true if the merge was successful, false otherwise
	 */
	@Override
	public boolean merge(IValue value) {
		if (!(value instanceof Value))
			return false;
		setParameter(((Value) value).getParameter());
		return true;
	}

	/**
	 * Gets the raw value.
	 *
	 * @return the raw value as a string
	 */
	@Override
	public String getRaw() {
		return getVariable();
	}
	
	
}
