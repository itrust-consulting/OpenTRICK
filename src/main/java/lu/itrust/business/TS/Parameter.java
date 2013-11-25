package lu.itrust.business.TS;

import java.io.Serializable;

/**
 * Parameter: <br>
 * This class represents a Parameter and its data.
 * 
 * @author itrust consulting s.à r.l. - BJA,SME
 * @version 0.1
 * @since 2012-08-21
 */
public class Parameter implements Serializable, Cloneable {

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** id unsaved value = -1 */
	private int id = -1;

	/** The Parameter Description */
	private String description = "";

	/** The Parameter Value */
	private double value = 0;

	/** The Parameter Type */
	private ParameterType type = null;

	/***********************************************************************************************
	 * Getters and Setters
	 **********************************************************************************************/

	/**
	 * getDescription: <br>
	 * Returns the "description" field value
	 * 
	 * @return The Parameter Description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * setDescription: <br>
	 * Sets the "description" field with a value
	 * 
	 * @param description
	 *            The value to set the Parameter Description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * getValue: <br>
	 * Returns the "value" field value
	 * 
	 * @return The Parameter Value
	 */
	public double getValue() {
		return value;
	}

	/**
	 * setValue: <br>
	 * Sets the "value" field with a value
	 * 
	 * @param value
	 *            The value to set the Parameter value
	 */
	public void setValue(double value) {
		this.value = value;
	}

	/**
	 * getType: <br>
	 * Returns the "type" field value
	 * 
	 * @return The Parameter Type Name
	 */
	public ParameterType getType() {
		return type;
	}

	/**
	 * setType: <br>
	 * Sets the "type" field with a value
	 * 
	 * @param type
	 *            The value to set the Parameter Type Name
	 */
	public void setType(ParameterType type) {
		this.type = type;
	}

	/**
	 * getId: <br>
	 * Returns the id field value.
	 * 
	 * @return The value of the id field
	 */
	public int getId() {
		return id;
	}

	/**
	 * setId: <br>
	 * Sets the Field "id" with a value.
	 * 
	 * @param id
	 *            The Value to set the id field
	 */
	public void setId(int id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Parameter clone() throws CloneNotSupportedException {
		return (Parameter) super.clone();
	}

	public Parameter duplicate() throws CloneNotSupportedException {
		Parameter parameter = (Parameter) super.clone();
		parameter.id = -1;
		return parameter;
	}
	
	
}