/**
 * 
 */
package lu.itrust.business.TS.model.parameter.helper.value;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;

import lu.itrust.business.TS.model.parameter.AcronymParameter;

/**
 * @author eomar
 *
 */
@Entity
public class DefaultRealValue extends AbstractNumeric {

	/**
	 * 
	 */
	public DefaultRealValue() {
	}

	public DefaultRealValue(String name, Double value, AcronymParameter parameter) {
		super(name, value, parameter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.model.assessment.value.IValue#getLevel()
	 */
	@Override
	public Integer getLevel() {
		return getParameter().getLevel();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.model.assessment.value.IValue#getReal()
	 */
	@Access(AccessType.PROPERTY)
	@Column(name = "dtValue")
	@Override
	public Double getReal() {
		return getNumber().doubleValue();
	}
	
	public void setReal(double real){
		setNumber(real);
	}

}
