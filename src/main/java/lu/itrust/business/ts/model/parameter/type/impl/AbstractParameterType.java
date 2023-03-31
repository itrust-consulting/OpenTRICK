/**
 * 
 */
package lu.itrust.business.ts.model.parameter.type.impl;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

import lu.itrust.business.ts.model.parameter.type.IParameterType;

/**
 * @author eomar
 *
 */
@MappedSuperclass
public abstract class AbstractParameterType implements IParameterType {
	
	@Id
	@GeneratedValue(strategy =GenerationType.IDENTITY)
	private int id;
	
	@Column(name = "dtName", unique=true)
	protected String name;
	
	/**
	 * 
	 */
	protected AbstractParameterType() {
	}

	/**
	 * @param name
	 */
	protected AbstractParameterType(String name) {
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.model.parameter.IParameterType#getId()
	 */
	@Override
	public int getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.model.parameter.IParameterType#setId(int)
	 */
	@Override
	public void setId(int id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.model.parameter.IParameterType#getName()
	 */
	@Override
	public String getName() {
		return name;
	}
}
