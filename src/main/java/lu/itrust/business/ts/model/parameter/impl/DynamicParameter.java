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
 * Represents a parameter whose value is assigned dynamically by external
 * notifications.
 * 
 * @author Steve Muller (SMU), itrust consulting s.à r.l.
 * @since Jun 10, 2015
 */
@Entity
@Cacheable

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverride(name = "id", column = @Column(name = "idDynamicParameter"))
public class DynamicParameter extends Parameter implements IAcronymParameter {

	@Column(name = "dtAcronym", nullable = false)
	private String acronym;

	/**
	 * 
	 */
	public DynamicParameter() {
	}

	public DynamicParameter(String acronym, String description) {
		setAcronym(acronym);
		setDescription(description);
	}

	/**
	 * @param value
	 * @param description
	 * @param acronym
	 */
	public DynamicParameter(String acronym, String description, double value) {
		super(value, description);
		this.acronym = acronym;
	}

	/**
	 * @param value
	 * @param description
	 * @param acronym
	 */
	public DynamicParameter(int id, String acronym, String description, double value) {
		super(value, description);
		this.acronym = acronym;
		this.setId(id);
	}

	@Override
	public String getTypeName() {
		return Constant.PARAMETERTYPE_TYPE_DYNAMIC_NAME;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.model.parameter.IProbabilityParameter#getGroup()
	 */
	@Override
	public String getGroup() {
		return Constant.PARAMETER_CATEGORY_DYNAMIC;
	}

	public void setAcronym(String acronym) {
		this.acronym = acronym;
	}

	@Override
	public String getAcronym() {
		return acronym;
	}

}