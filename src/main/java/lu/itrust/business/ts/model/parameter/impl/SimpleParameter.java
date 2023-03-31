package lu.itrust.business.ts.model.parameter.impl;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import lu.itrust.business.ts.constants.Constant;
import lu.itrust.business.ts.model.parameter.ITypedParameter;
import lu.itrust.business.ts.model.parameter.type.impl.ParameterType;

/**
 * SimpleParameter: <br>
 * This class represents a SimpleParameter and its data.
 * 
 * @author itrust consulting s.à r.l. - BJA,SME
 * @version 0.1
 * @since 2012-08-21
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverride(name="id", column=@Column(name="idSimpleParameter"))
public class SimpleParameter extends Parameter implements ITypedParameter {

	/** The SimpleParameter Type */
	@ManyToOne
	@JoinColumn(name = "fiParameterType", nullable = false)
	@Cascade(CascadeType.SAVE_UPDATE)
	protected ParameterType type = null;

	/**
	 * Constructor: <br>
	 * 
	 * @param type
	 * @param descriptif
	 * @param value
	 */
	public SimpleParameter(ParameterType type, String descriptif, Double value) {
		setType(type);
		setDescription(descriptif);
		setValue(value);
	}

	/**
	 * Constructor: <br>
	 *
	 */
	public SimpleParameter() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.model.parameter.IParameter#getType()
	 */
	@Override
	public ParameterType getType() {
		return type;
	}

	/**
	 * setType: <br>
	 * Sets the "type" field with a value
	 * 
	 * @param type
	 *            The value to set the SimpleParameter Type Name
	 */
	public void setType(ParameterType type) {
		this.type = type;
	}

	@Override
	public String getTypeName() {
		return this.type.getName();
	}

	@Override
	public String getGroup() {
		return Constant.PARAMETER_CATEGORY_SIMPLE;
	}
	
	

}