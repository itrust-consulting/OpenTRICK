package lu.itrust.business.TS.model.parameter.type.impl;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;

import lu.itrust.business.TS.exception.TrickException;

/**
 * ParameterType: <br>
 * Represents the SimpleParameter Type as Name.
 * 
 * @author itrust consulting s.à r.l. : EOM, BJA, SME
 * @version 0.1
 * @since 25 janv. 2013
 */
@Entity
@AttributeOverride(name = "id", column = @Column(name = "idParameterType"))
public class ParameterType extends AbstractParameterType {

	/***********************************************************************************************
	 * Constructors
	 **********************************************************************************************/

	/**
	 * Constructor: <br>
	 */
	public ParameterType() {
	}

	/**
	 * Constructor: <br>
	 * 
	 * @param name
	 *            The SimpleParameter Type Label
	 * 
	 * @throws TrickException
	 */
	public ParameterType(String name) throws TrickException {
		super(name);
	}

	/***********************************************************************************************
	 * Getters and Setters
	 **********************************************************************************************/

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.model.parameter.IParameterType#setName(java.lang.
	 * String)
	 */
	@Override
	public void setName(String name) throws TrickException {
		this.name = name.toUpperCase().trim();
	}
}