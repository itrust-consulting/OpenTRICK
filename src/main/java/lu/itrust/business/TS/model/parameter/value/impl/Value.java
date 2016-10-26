/**
 * 
 */
package lu.itrust.business.TS.model.parameter.value.impl;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;

import lu.itrust.business.TS.model.parameter.ILevelParameter;

/**
 * @author eomar
 *
 */
@Entity
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

}
