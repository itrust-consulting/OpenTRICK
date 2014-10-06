package lu.itrust.business.TS;

import java.io.Serializable;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import lu.itrust.business.TS.actionplan.ActionPlanMode;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.exception.TrickException;

/**
 * Norm: <br>
 * Represents a Norm Name
 * 
 * @author itrust consulting s.à. r.l. : EOM, BJA, SME
 * @version 0.1
 * @since 24 janv. 2013
 */
@Entity 
@Table(uniqueConstraints=@UniqueConstraint(columnNames={"dtLabel","dtVersion"}))
public class Standard implements Serializable, Cloneable {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** serialVersionUID */
	@Transient
	private static final long serialVersionUID = 1L;

	/** Norm ID */
	@Id @GeneratedValue 
	@Column(name="idStandard")
	private int id = -1;

	/** Norm Name */
	@Column(name="dtLabel", nullable=false)
	private String label = "";

	/** the norm verison */
	@Column(name="dtVersion", nullable=false)
	private int version = 2013;

	/** description of the norm */
	@Column(name="dtDescription", nullable=false)
	private String description = "";

	@Enumerated(EnumType.STRING) 
	@Column(name="dtType", nullable=false, unique=true)
	@Access(AccessType.FIELD)
	private StandardType type = null;
	
	/** norm available for actionplan computation */
	@Column(name="dtComputable", nullable=false, columnDefinition="TINYINT(1)")
	private boolean computable = true;

	/***********************************************************************************************
	 * Constructors
	 **********************************************************************************************/

	/**
	 * Constructor:<br>
	 */
	public Standard() {
	}

	/**
	 * Constructor:<br>
	 * 
	 * @param label
	 *            The Norm Name
	 * @throws TrickException 
	 */
	public Standard(String label) throws TrickException {
		this.setLabel(label);
	}

	/**
	 * Constructor:<br>
	 * 
	 * @param label
	 *            The Norm Name
	 * @throws TrickException 
	 */
	public Standard(String label, int version) throws TrickException {
		this.setLabel(label);
		this.setVersion(version);
	}

	/**
	 * Constructor:<br>
	 * 
	 * @param label
	 *            The Norm Name
	 * @throws TrickException 
	 */
	public Standard(String label, int version, String description) throws TrickException {
		this.setLabel(label);
		this.setVersion(version);
		this.setDescription(description);
	}

	/**
	 * Constructor:<br>
	 * 
	 * @param label
	 *            The Norm Name
	 * @throws TrickException 
	 */
	public Standard(String label, int version, String description, boolean computable) throws TrickException {
		this.setLabel(label);
		this.setVersion(version);
		this.setDescription(description);
		this.setComputable(computable);
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
		if (label == null || label.trim().isEmpty())
			throw new TrickException("error.norm.label","Name cannot be empty!");
		this.label = label;
	}

	/** getType: <br>
	 * Returns the type field value.
	 * 
	 * @return The value of the type field
	 */
	public StandardType getType() {
		return type;
	}

	/** setType: <br>
	 * Sets the Field "type" with a value.
	 * 
	 * @param type 
	 * 			The Value to set the type field
	 */
	public void setType(StandardType type) {
		this.type = type;
	}

	/**
	 * getName: <br>
	 * Returns the name field value.
	 * 
	 * @return The value of the name field
	 */
	public String getNameOfType() {
		return type.getName();
	}
		
	/**
	 * setName: <br>
	 * Sets the Field "name" with a value.
	 * 
	 * @param name
	 *            The Value to set the name field
	 */
	public void setType(String name) {
		this.type = StandardType.getByName(name.trim());
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (computable ? 1231 : 1237);
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + id;
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		result = prime * result + version;
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Standard other = (Standard) obj;
		if (id != other.id)
			if (id == -1 && other.id != -1)
				return false;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		if (version != other.version)
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Standard [id=" + id + ", label=" + label + ", version=" + version + ", description=" + description + ", computable=" + computable + "]";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Standard clone() throws CloneNotSupportedException {
		return (Standard) super.clone();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	public Standard duplicate() throws CloneNotSupportedException {
		Standard norm = (Standard) super.clone();
		if (norm.label.equalsIgnoreCase(Constant.STANDARD_CUSTOM))
			norm.id = -1;
		return norm;
	}

	/**
	 * getVersion: <br>
	 * Returns the version field value.
	 * 
	 * @return The value of the version field
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * setVersion: <br>
	 * Sets the Field "version" with a value.
	 * 
	 * @param version
	 *            The Value to set the version field
	 */
	public void setVersion(int version) {
		this.version = version;
	}

	/**
	 * isComputable: <br>
	 * Returns the computable field value.
	 * 
	 * @return The value of the computable field
	 */
	public boolean isComputable() {
		return computable;
	}

	/**
	 * setComputable: <br>
	 * Sets the Field "computable" with a value.
	 * 
	 * @param computable
	 *            The Value to set the computable field
	 */
	public void setComputable(boolean computable) {
		this.computable = computable;
	}

	/**
	 * getDescription: <br>
	 * Returns the description field value.
	 * 
	 * @return The value of the description field
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * setDescription: <br>
	 * Sets the Field "description" with a value.
	 * 
	 * @param description
	 *            The Value to set the description field
	 */
	public void setDescription(String description) {
		this.description = description;
	}

}
