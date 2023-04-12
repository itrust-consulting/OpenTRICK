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
	 * @param value
	 * @param description
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
	 * setDescription: <br>
	 * Sets the "description" field with a value
	 * 
	 * @param description
	 *                    The value to set the SimpleParameter Description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public Double getValue() {
		return value;
	}

	/**
	 * setValue: <br>
	 * Sets the "value" field with a value
	 * 
	 * @param value
	 *              The value to set the SimpleParameter value
	 */
	public void setValue(double value) {
		this.value = value;
	}

	@Override
	public Integer getId() {
		return id;
	}

	/**
	 * setId: <br>
	 * Sets the Field "id" with a value.
	 * 
	 * @param id
	 *           The Value to set the id field
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * clone: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see java.lang.Object#clone()
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
	 * duplicate: <br>
	 * Description
	 * 
	 * @return
	 * @throws CloneNotSupportedException
	 */
	public Parameter duplicate() {
		Parameter parameter = (Parameter) this.clone();
		parameter.id = 0;
		return parameter;
	}

	public boolean isMatch(String type) {
		return this.getTypeName() == null ? type == null : this.getTypeName().equalsIgnoreCase(type);
	}

	public boolean isMatch(String typeName, String baseKey) {
		return this.getTypeName() == null
				? (typeName == null
						? (this.getBaseKey() == null ? baseKey == null : this.getBaseKey().equalsIgnoreCase(baseKey))
						: false)
				: this.getTypeName().equalsIgnoreCase(typeName)
						&& (this.getBaseKey() == null ? baseKey == null : this.getBaseKey().equalsIgnoreCase(baseKey));
	}

	public static String key(String type, String baseKey) {
		return String.format(IParameter.KEY_PARAMETER_FORMAT, type, baseKey);
	}
}