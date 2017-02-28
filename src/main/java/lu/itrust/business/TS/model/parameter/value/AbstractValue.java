package lu.itrust.business.TS.model.parameter.value;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.Any;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.parameter.ILevelParameter;

@MappedSuperclass
public abstract class AbstractValue implements IValue {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Any(metaColumn = @Column(name = "dtParameterType"), metaDef = "PARAMETER_META_DEF")
	@Cascade(CascadeType.SAVE_UPDATE)
	@JoinColumn(name = "fiParameter")
	private ILevelParameter parameter;

	public AbstractValue() {
	}

	/**
	 * @param parameter
	 */
	public AbstractValue(ILevelParameter parameter) {
		this.parameter = parameter;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public ILevelParameter getParameter() {
		return parameter;
	}

	/**
	 * @param parameter
	 *            the parameter to set
	 */
	public void setParameter(ILevelParameter parameter) {
		this.parameter = parameter;
	}

	@Override
	public Integer getLevel() {
		return parameter.getLevel();
	}

	@Override
	public String getVariable() {
		return parameter.getAcronym();
	}

	@Override
	public Double getReal() {
		return parameter.getValue().doubleValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
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
	 * @see lu.itrust.business.TS.model.parameter.value.IValue#duplicate()
	 */
	@Override
	public IValue duplicate() {
		AbstractValue value = this.clone();
		value.id = 0;
		return value;
	}

	
}