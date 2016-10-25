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
@AttributeOverride(name = "id", column = @Column(name = "idParameterValue"))
public class ParameterValue extends AbstractValue {

	/**
	 * 
	 */
	public ParameterValue() {
	}

	/**
	 * @param name
	 * @param parameter
	 */
	public ParameterValue(ILevelParameter parameter) {
		super(parameter);
	}

}
