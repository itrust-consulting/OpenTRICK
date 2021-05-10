/**
 * 
 */
package lu.itrust.business.TS.model.parameter.value.impl;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.parameter.value.IValue;

/**
 * @author eomar
 *
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class FormulaValue implements IValue {
	
	@Id
	@Column(name = "idFormulaValue")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(name = "dtFormula", nullable = true)
	private String variable;
	
	@Column(name = "dtValue",nullable = true)
	private double value;
	
	@Column(name = "dtLevel",nullable = true)
	private int level;

	/**
	 * 
	 */
	public FormulaValue() {
	}
	
	/**
	 * @param variable
	 * @param value
	 */
	public FormulaValue(String variable, double value) {
		update(variable, value, -1);
	}

	/**
	 * @param variable
	 * @param value
	 * @param level
	 */
	public FormulaValue(String variable, double value, int level) {
		update(variable, value, level);
		
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @param variable the variable to set
	 */
	public void setVariable(String variable) {
		this.variable = variable;
	}

	/**
	 * @param level the level to set
	 */
	public void setLevel(int level) {
		this.level = level;
	}

	@Override
	public Integer getLevel() {
		return level;
	}

	@Override
	public String getVariable() {
		return variable;
	}

	@Override
	public Double getReal() {
		return value;
	}
	
	/**
	 * @param value
	 */
	public void setReal(double value) {
		this.value = value;
	}

	@Override
	public Object getRaw() {
		return variable;
	}

	@Override
	public boolean merge(IValue value) {
		if (value == null || !(value instanceof FormulaValue))
			return false;
		setReal(value.getReal());
		setLevel(value.getLevel());
		setVariable(value.getVariable());
		return true;
	}
	
	public void update(String variable, double value, int level) {
		this.variable = variable;
		this.value = value;
		this.level = level;
	}


	@Override
	public String getName() {
		return Constant.PARAMETER_TYPE_IMPACT_NAME;
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public FormulaValue clone() {
		try {
			return (FormulaValue) super.clone();
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
		FormulaValue value = this.clone();
		value.id = 0;
		return value;
	}

}
