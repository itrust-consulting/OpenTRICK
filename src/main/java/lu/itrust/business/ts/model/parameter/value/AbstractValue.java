package lu.itrust.business.ts.model.parameter.value;


import static lu.itrust.business.ts.constants.Constant.PARAMETER_CATEGORY_DYNAMIC;
import static lu.itrust.business.ts.constants.Constant.PARAMETER_CATEGORY_IMPACT;
import static lu.itrust.business.ts.constants.Constant.PARAMETER_CATEGORY_PROBABILITY_LIKELIHOOD;

import org.hibernate.annotations.Any;
import org.hibernate.annotations.AnyDiscriminator;
import org.hibernate.annotations.AnyDiscriminatorValue;
import org.hibernate.annotations.AnyKeyJavaClass;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MappedSuperclass;
import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.model.parameter.ILevelParameter;
import lu.itrust.business.ts.model.parameter.impl.DynamicParameter;
import lu.itrust.business.ts.model.parameter.impl.ImpactParameter;
import lu.itrust.business.ts.model.parameter.impl.LikelihoodParameter;

@MappedSuperclass
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public abstract class AbstractValue implements IParameterValue {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Any
	@AnyDiscriminator(DiscriminatorType.STRING)
	@Column(name = "dtParameterType")
	@AnyKeyJavaClass(Integer.class)
	@AnyDiscriminatorValue(discriminator = PARAMETER_CATEGORY_DYNAMIC, entity = DynamicParameter.class)
	@AnyDiscriminatorValue(discriminator = PARAMETER_CATEGORY_PROBABILITY_LIKELIHOOD, entity = LikelihoodParameter.class)
	@AnyDiscriminatorValue(discriminator = PARAMETER_CATEGORY_IMPACT, entity = ImpactParameter.class)
	@Cascade(CascadeType.SAVE_UPDATE)
	@JoinColumn(name = "fiParameter")
	private ILevelParameter parameter;

	protected AbstractValue() {
	}

	/**
	 * Constructs a new AbstractValue object with the specified parameter.
	 *
	 * @param parameter the level parameter associated with this value
	 */
	protected AbstractValue(ILevelParameter parameter) {
		this.parameter = parameter;
	}

	/**
	 * Returns the ID of this AbstractValue.
	 *
	 * @return the ID of this AbstractValue
	 */
	public int getId() {
		return id;
	}

	/**
	 * Sets the ID of this AbstractValue.
	 *
	 * @param id the ID to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	
	/**
	 * Returns the level parameter associated with this value.
	 *
	 * @return the level parameter
	 */
	@Override
	public ILevelParameter getParameter() {
		return parameter;
	}

	/**
	 * Sets the level parameter associated with this value.
	 *
	 * @param parameter the level parameter to set
	 */
	public void setParameter(ILevelParameter parameter) {
		this.parameter = parameter;
	}

	/**
	 * Returns the level of the parameter.
	 *
	 * @return the level of the parameter
	 */
	@Override
	public Integer getLevel() {
		return parameter.getLevel();
	}

	/**
	 * Returns the variable as a string.
	 *
	 * @return the variable as a string
	 */
	@Override
	public String getVariable() {
		return parameter.getAcronym();
	}

	/**
	 * Returns the real value of the parameter as a Double.
	 *
	 * @return the real value of the parameter as a Double
	 */
	@Override
	public Double getReal() {
		return parameter.getValue().doubleValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 *  @return a clone of this AbstractValue object
	 */
	@Override
	public AbstractValue clone() {
		try {
			return (AbstractValue) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new TrickException("error.clone.value", "Value canot be copied", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.model.parameter.value.IValue#duplicate()
	 * @return a duplicate of this AbstractValue object with a new ID
	 */
	@Override
	public IValue duplicate() {
		AbstractValue value = this.clone();
		value.id = 0;
		return value;
	}

}