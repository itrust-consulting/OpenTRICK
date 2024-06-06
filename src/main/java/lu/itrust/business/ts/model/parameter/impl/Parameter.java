package lu.itrust.business.ts.model.parameter.impl;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.model.parameter.IParameter;

/**
 * The Parameter class is an abstract class that represents a parameter.
 * It provides common functionality and properties for all parameter types.
 */
@MappedSuperclass
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public abstract class Parameter implements IParameter {

	/** id unsaved value = -1 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id = 0;

	/** The SimpleParameter Description */
	@Column(name = "dtDescription", nullable = false)
	private String description = "";

	/** The SimpleParameter Value */
	@Column(name = "dtValue", nullable = false)
	private double value = 0;

	protected Parameter() {
	}

	/**
	 * Constructs a Parameter object with the specified value and description.
	 *
	 * @param value       The value of the parameter.
	 * @param description The description of the parameter.
	 */
	protected Parameter(double value, String description) {
		this.value = value;
		this.description = description;
	}

	@Override
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description of the parameter.
	 *
	 * @param description The value to set the parameter description.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public Double getValue() {
		return value;
	}

	/**
	 * Sets the value of the parameter.
	 *
	 * @param value The value to set the parameter value.
	 */
	public void setValue(double value) {
		this.value = value;
	}

	@Override
	public Integer getId() {
		return id;
	}

	/**
	 * Sets the ID of the parameter.
	 *
	 * @param id The value to set the parameter ID.
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Creates and returns a copy of the parameter object.
	 *
	 * @return A copy of the parameter object.
	 * @throws TrickException if the parameter cannot be copied.
	 */
	@Override
	public Parameter clone() {
		try {
			return (Parameter) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new TrickException("error.clone.parameter", "SimpleParameter cannot be copied");
		}
	}

	/**
	 * Creates and returns a duplicate of the parameter object.
	 *
	 * @return A duplicate of the parameter object.
	 */
	public Parameter duplicate() {
		Parameter parameter = this.clone();
		parameter.id = 0;
		return parameter;
	}

	/**
	 * Checks if the parameter matches the specified type.
	 *
	 * @param type The type to match.
	 * @return true if the parameter matches the type, false otherwise.
	 */
	public boolean isMatch(String type) {
		return this.getTypeName() == null ? type == null : this.getTypeName().equalsIgnoreCase(type);
	}

	/**
	 * Checks if the parameter matches the specified type and base key.
	 *
	 * @param typeName The type name to match.
	 * @param baseKey  The base key to match.
	 * @return true if the parameter matches the type and base key, false otherwise.
	 */
	public boolean isMatch(String typeName, String baseKey) {
		return this.getTypeName() == null
				? (typeName == null
						? (this.getBaseKey() == null ? baseKey == null : this.getBaseKey().equalsIgnoreCase(baseKey))
						: false)
				: this.getTypeName().equalsIgnoreCase(typeName)
						&& (this.getBaseKey() == null ? baseKey == null : this.getBaseKey().equalsIgnoreCase(baseKey));
	}

	/**
	 * Generates a key for the parameter using the specified type and base key.
	 *
	 * @param type    The type of the parameter.
	 * @param baseKey The base key of the parameter.
	 * @return The generated key for the parameter.
	 */
	public static String key(String type, String baseKey) {
		return String.format(IParameter.KEY_PARAMETER_FORMAT, type, baseKey);
	}
}