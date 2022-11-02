package lu.itrust.business.TS.model.parameter.impl;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.parameter.IParameter;

@MappedSuperclass
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public abstract class Parameter implements IParameter {

	/** id unsaved value = -1 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id = -1;

	/** The SimpleParameter Description */
	@Column(name = "dtDescription", nullable = false)
	private String description = "";

	/** The SimpleParameter Value */
	@Column(name = "dtValue", nullable = false)
	private double value = 0;

	public Parameter() {
	}

	/**
	 * @param value
	 * @param description
	 */
	public Parameter(double value, String description) {
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
		parameter.id = -1;
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