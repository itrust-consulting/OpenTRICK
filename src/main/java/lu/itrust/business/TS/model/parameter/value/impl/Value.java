/**
 * 
 */
package lu.itrust.business.TS.model.parameter.value.impl;

import javax.persistence.AttributeOverride;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import lu.itrust.business.TS.model.parameter.ILevelParameter;
import lu.itrust.business.TS.model.parameter.value.AbstractValue;
import lu.itrust.business.TS.model.parameter.value.IValue;

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
		setParameter(value.getParameter());
		return true;
	}
}
