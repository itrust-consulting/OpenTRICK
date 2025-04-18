/**
 * 
 */
package lu.itrust.business.ts.model.parameter.value.impl;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import lu.itrust.business.ts.constants.Constant;
import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.model.parameter.value.IValue;

/**
 * Represents a formula value in the system.
 * This class implements the IValue interface and provides methods to manipulate and retrieve formula values.
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
	 * Default constructor.
	 */
	public FormulaValue() {
	}
	
	/**
	 * Constructor with variable and value.
	 * 
	 * @param variable the variable name
	 * @param value the value
	 */
	public FormulaValue(String variable, double value) {
		update(variable, value, -1);
	}

	/**
	 * Constructor with variable, value, and level.
	 * 
	 * @param variable the variable name
	 * @param value the value
	 * @param level the level
	 */
	public FormulaValue(String variable, double value, int level) {
		update(variable, value, level);
	}

	/**
	 * Get the ID of the formula value.
	 * 
	 * @return the ID
	 */
	public int getId() {
		return id;
	}

	/**
	 * Set the ID of the formula value.
	 * 
	 * @param id the ID to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Set the variable name.
	 * 
	 * @param variable the variable name to set
	 */
	public void setVariable(String variable) {
		this.variable = variable;
	}

	/**
	 * Set the level.
	 * 
	 * @param level the level to set
	 */
	public void setLevel(int level) {
		this.level = level;
	}

	/**
	 * Returns the level of the formula value.
	 *
	 * @return the level of the formula value
	 */
	@Override
	public Integer getLevel() {
		return level;
	}

	/**
	 * Returns the variable associated with this formula value.
	 *
	 * @return the variable as a String
	 */
	@Override
	public String getVariable() {
		return variable;
	}

	/**
	 * Returns the real value of the formula.
	 *
	 * @return the real value of the formula
	 */
	@Override
	public Double getReal() {
		return value;
	}
	
	/**
	 * Set the real value.
	 * 
	 * @param value the real value to set
	 */
	public void setReal(double value) {
		this.value = value;
	}

	/**
	 * Returns the raw value of the formula.
	 *
	 * @return the raw value of the formula as an Object
	 */
	@Override
	public Object getRaw() {
		return variable;
	}

	/**
	 * Merges the given value with this FormulaValue.
	 * 
	 * @param value the value to merge with
	 * @return true if the merge was successful, false otherwise
	 */
	@Override
	public boolean merge(IValue value) {
		if (value == null || !(value instanceof FormulaValue))
			return false;
		setReal(value.getReal());
		setLevel(value.getLevel());
		setVariable(value.getVariable());
		return true;
	}
	
	/**
	 * Update the formula value with the given variable, value, and level.
	 * 
	 * @param variable the variable name
	 * @param value the value
	 * @param level the level
	 */
	public void update(String variable, double value, int level) {
		this.variable = variable;
		this.value = value;
		this.level = level;
	}

	/**
	 * Returns the name of the formula value.
	 *
	 * @return the name of the formula value
	 */
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
	 * @see lu.itrust.business.ts.model.parameter.value.IValue#duplicate()
	 */
	@Override
	public IValue duplicate() {
		FormulaValue value = this.clone();
		value.id = 0;
		return value;
	}

	/**
	 * Returns a string representation of the FormulaValue object.
	 * 
	 * @return a string representation of the object
	 */
	@Override
	public String toString() {
		return "FormulaValue [id=" + id + ", variable=" + variable + ", value=" + value + ", level=" + level + "]";
	}
}
