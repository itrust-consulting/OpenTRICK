package lu.itrust.business.TS.data.basic;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.tsconstant.Constant;

/**
 * ItemInformation: <br>
 * This class represents an ItemInformation and all its data.
 * 
 * This class is used to store ItemInformation
 * 
 * @author itrust consulting s.à r.l. - SME,BJA
 * @version 0.1
 * @since 2012-08-21
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "fiAnalysis", "dtLabel" }))
public class ItemInformation implements Cloneable {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** id unsaved value = -1 */
	@Id
	@GeneratedValue
	@Column(name = "idItemInformation")
	private int id = -1;

	/** The Item Information Value */
	@Column(name = "dtValue", nullable = false, columnDefinition = "LONGTEXT")
	private String value = "";

	/** The Item Information description */
	@Column(name = "dtLabel", nullable = false)
	private String description = "";

	/** The Item Information Type */
	@Column(name = "dtType", nullable = false)
	private String type = "";

	/**
	 * Constructor: <br>
	 *
	 */
	public ItemInformation() {
	}

	/**
	 * Constructor: <br>
	 * 
	 * @param description
	 * @param type
	 * @param value
	 */
	public ItemInformation(String description, String type, String value) {
		this.description = description;
		this.type = type;
		this.value = value;
	}

	/***********************************************************************************************
	 * Getters and Setters
	 **********************************************************************************************/

	/**
	 * getValue: <br>
	 * Returns the "value" field value
	 * 
	 * @return The Item Information Value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * setValue: <br>
	 * Sets the "value" field with a value
	 * 
	 * @param value
	 *            The value to set the Item Information Value
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * getDescription: <br>
	 * Returns the "description" field value
	 * 
	 * @return The Item Information Description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * setDescription: <br>
	 * Sets the "description" field with a value
	 * 
	 * @param description
	 *            The value to set the Item Information Description
	 * @throws TrickException
	 */
	public void setDescription(String description) throws TrickException {
		if (description == null || description.trim().isEmpty())
			throw new TrickException("error.item_information.description.empty", "Description cannot be empty!");
		this.description = description;
	}

	/**
	 * getType: <br>
	 * Returns the "type" field value
	 * 
	 * @return The Item Information Type
	 */
	public String getType() {
		return type;
	}

	/**
	 * setType: <br>
	 * Sets the "type" field with a value
	 * 
	 * @param type
	 *            The value to set the Item Information Type (Scope or Organisation)
	 * @throws TrickException
	 */
	public void setType(String type) throws TrickException {
		if (type == null || !type.matches(Constant.REGEXP_VALID_ITEMINFORMATION_TYPE))
			throw new TrickException("error.item_information.type.empty", "Type needs to be Scope or Organisation only!");
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

	/**
	 * clone: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see java.lang.Object#clone()
	 */
	@Override
	public ItemInformation clone() throws CloneNotSupportedException {
		return (ItemInformation) super.clone();
	}

	/**
	 * duplicate: <br>
	 * Description
	 * 
	 * @return
	 * @throws CloneNotSupportedException
	 */
	public ItemInformation duplicate() throws CloneNotSupportedException {
		ItemInformation itemInformation = (ItemInformation) super.clone();
		itemInformation.id = -1;
		return itemInformation;
	}

}