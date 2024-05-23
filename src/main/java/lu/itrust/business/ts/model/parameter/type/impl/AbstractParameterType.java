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
 * This abstract class represents a parameter type in the system.
 * It provides common functionality and properties for all parameter types.
 */
@MappedSuperclass
public abstract class AbstractParameterType implements IParameterType {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(name = "dtName", unique = true)
	protected String name;
	
	/**
	 * Default constructor for the AbstractParameterType class.
	 * This constructor is protected to prevent direct instantiation of the abstract class.
	 */
	protected AbstractParameterType() {
	}

	/**
	 * Constructor for the AbstractParameterType class.
	 * Initializes the name property of the parameter type.
	 *
	 * @param name The name of the parameter type.
	 */
	protected AbstractParameterType(String name) {
		this.name = name;
	}

	/**
	 * Retrieves the ID of the parameter type.
	 *
	 * @return The ID of the parameter type.
	 */
	@Override
	public int getId() {
		return id;
	}

	/**
	 * Sets the ID of the parameter type.
	 *
	 * @param id The ID to set for the parameter type.
	 */
	@Override
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Retrieves the name of the parameter type.
	 *
	 * @return The name of the parameter type.
	 */
	@Override
	public String getName() {
		return name;
	}
}
