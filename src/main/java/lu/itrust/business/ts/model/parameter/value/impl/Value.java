/**
 * 
 */
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
 * @author eomar
 *
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverride(name = "id", column = @Column(name = "idValue"))
public class Value extends AbstractValue {

	/**
	 * 
	 */
	public Value() {
	}

	/**
	 * @param name
	 * @param parameter
	 */
	public Value(ILevelParameter parameter) {
		super(parameter);
	}

	@Override
	public boolean merge(IValue value) {
		if (value == null || !(value instanceof Value))
			return false;
		setParameter(((Value) value).getParameter());
		return true;
	}

	@Override
	public String getRaw() {
		return getVariable();
	}
	
	
}
