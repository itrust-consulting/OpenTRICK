package lu.itrust.business.ts.model.iteminformation;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import lu.itrust.business.ts.constants.Constant;
import lu.itrust.business.ts.exception.TrickException;

/**
 * ItemInformation: <br>
 * This class represents an ItemInformation and all its data.
 * 
 * This class is used to store ItemInformation
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "fiAnalysis", "dtLabel" }))
public class ItemInformation implements Cloneable {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** id unsaved value = -1 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idItemInformation")
	private int id = 0;

	/** The Item Information Value */
	@Column(name = "dtValue", nullable = false, length=16777216)
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
	 *            The value to set the Item Information Type (Scope or
	 *            Organisation)
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
		itemInformation.id = 0;
		return itemInformation;
	}

	/**
	 * reset value + id
	 * @return duplicate
	 */
	public ItemInformation anonymise() {
		try {
			ItemInformation itemInformation = duplicate();
			itemInformation.value = "";
			return itemInformation;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}

	}

}