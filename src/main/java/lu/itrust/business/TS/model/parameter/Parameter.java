package lu.itrust.business.TS.model.parameter;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

/**
 * Parameter: <br>
 * This class represents a Parameter and its data.
 * 
 * @author itrust consulting s.à r.l. - BJA,SME
 * @version 0.1
 * @since 2012-08-21
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Parameter implements Cloneable {

	@Transient
	protected static final String KEY_PARAMETER_FORMAT = "%s-#-_##_-#-%s";

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** id unsaved value = -1 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idParameter")
	private int id = -1;

	/** The Parameter Description */
	@Column(name = "dtLabel", nullable = false)
	private String description = "";

	/** The Parameter Value */
	@Column(name = "dtValue", nullable = false)
	private double value = 0;

	/** The Parameter Type */
	@ManyToOne
	@JoinColumn(name = "fiParameterType", nullable = false)
	@Access(AccessType.FIELD)
	@Cascade(CascadeType.SAVE_UPDATE)
	private ParameterType type = null;

	/***********************************************************************************************
	 * Getters and Setters
	 **********************************************************************************************/

	/**
	 * Constructor: <br>
	 * 
	 * @param type
	 * @param descriptif
	 * @param value
	 */
	public Parameter(ParameterType type, String descriptif, Double value) {
		setType(type);
		setDescription(descriptif);
		setValue(value);
	}

	/**
	 * Constructor: <br>
	 *
	 */
	public Parameter() {
	}

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

	/**
	 * clone: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Parameter clone() throws CloneNotSupportedException {
		return (Parameter) super.clone();
	}

	/**
	 * duplicate: <br>
	 * Description
	 * 
	 * @return
	 * @throws CloneNotSupportedException
	 */
	public Parameter duplicate() throws CloneNotSupportedException {
		Parameter parameter = (Parameter) super.clone();
		parameter.id = -1;
		return parameter;
	}

	public Boolean isMatch(String typeName, String description) {
		return this.type == null ? (typeName == null ? (this.description == null ? description == null : this.description.equals(description)) : false)
				: this.type.getLabel().equals(typeName) && (this.description == null ? description == null : this.description.equals(description));
	}

	public String getKey() {
		return key(type.getLabel(), description);
	}

	public static String key(String type, String description) {
		return String.format(Parameter.KEY_PARAMETER_FORMAT, type, description);
	}

	public Boolean isMatch(String type) {
		return this.type == null ? type == null : this.type.getLabel().equals(type);
	}

}