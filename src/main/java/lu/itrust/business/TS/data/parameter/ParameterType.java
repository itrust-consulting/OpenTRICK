package lu.itrust.business.TS.data.parameter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.exception.TrickException;

/**
 * ParameterType: <br>
 * Represents the Parameter Type as Name.
 * 
 * @author itrust consulting s.à r.l. : EOM, BJA, SME
 * @version 0.1
 * @since 25 janv. 2013
 */
@Entity
public class ParameterType {

	/***********************************************************************************************
	 * Field declarations
	 **********************************************************************************************/

	/** Parameter Type Identifier */
	@Id
	@Column(name = "idParameterType")
	private int id = -1;

	/** Parameter Type Label */
	@Column(name = "dtLabel", unique = true, nullable = false)
	private String label = "";

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
	 * @param label
	 *            The Parameter Type Label
	 * @throws TrickException
	 */
	public ParameterType(String label) throws TrickException {
		setLabel(label);
	}

	/***********************************************************************************************
	 * Getters and Setters
	 **********************************************************************************************/

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

	/**
	 * getLabel: <br>
	 * Returns the label field value.
	 * 
	 * @return The value of the label field
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * setLabel: <br>
	 * Sets the Field "label" with a value.
	 * 
	 * @param label
	 *            The Value to set the label field
	 * @throws TrickException
	 */
	public void setLabel(String label) throws TrickException {
		if (label == null || !label.trim().matches(Constant.REGEXP_VALID_PARAMETERTYPE))
			throw new TrickException("error.parameter_type.name.not_exist", "Parameter name does not exist!");
		this.label = label.trim();
	}
}