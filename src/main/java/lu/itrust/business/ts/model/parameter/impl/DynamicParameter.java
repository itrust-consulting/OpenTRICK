package lu.itrust.business.ts.model.parameter.impl;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import lu.itrust.business.ts.constants.Constant;
import lu.itrust.business.ts.model.parameter.IAcronymParameter;

/**
 * Represents a dynamic parameter in the system.
 * This class extends the Parameter class and implements the IAcronymParameter interface.
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverride(name = "id", column = @Column(name = "idDynamicParameter"))
public class DynamicParameter extends Parameter implements IAcronymParameter {

	@Column(name = "dtAcronym", nullable = false)
	private String acronym;

	/**
	 * Default constructor.
	 */
	public DynamicParameter() {
	}

	/**
	 * Constructor with acronym and description.
	 * @param acronym The acronym of the dynamic parameter.
	 * @param description The description of the dynamic parameter.
	 */
	public DynamicParameter(String acronym, String description) {
		setAcronym(acronym);
		setDescription(description);
	}

	/**
	 * Constructor with acronym, description, and value.
	 * @param acronym The acronym of the dynamic parameter.
	 * @param description The description of the dynamic parameter.
	 * @param value The value of the dynamic parameter.
	 */
	public DynamicParameter(String acronym, String description, double value) {
		super(value, description);
		this.acronym = acronym;
	}

	/**
	 * Constructor with id, acronym, description, and value.
	 * @param id The id of the dynamic parameter.
	 * @param acronym The acronym of the dynamic parameter.
	 * @param description The description of the dynamic parameter.
	 * @param value The value of the dynamic parameter.
	 */
	public DynamicParameter(int id, String acronym, String description, double value) {
		super(value, description);
		this.acronym = acronym;
		this.setId(id);
	}

	/**
	 * Get the type name of the dynamic parameter.
	 * @return The type name of the dynamic parameter.
	 */
	@Override
	public String getTypeName() {
		return Constant.PARAMETERTYPE_TYPE_DYNAMIC_NAME;
	}

	/**
	 * Get the group of the dynamic parameter. see lu.itrust.business.ts.model.parameter.IProbabilityParameter#getGroup()
	 * @return The group of the dynamic parameter.
	 */
	@Override
	public String getGroup() {
		return Constant.PARAMETER_CATEGORY_DYNAMIC;
	}

	/**
	 * Set the acronym of the dynamic parameter.
	 * @param acronym The acronym of the dynamic parameter.
	 */
	public void setAcronym(String acronym) {
		this.acronym = acronym;
	}

	/**
	 * Get the acronym of the dynamic parameter.
	 * @return The acronym of the dynamic parameter.
	 */
	@Override
	public String getAcronym() {
		return acronym;
	}

}