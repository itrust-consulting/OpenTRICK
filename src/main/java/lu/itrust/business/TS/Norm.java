package lu.itrust.business.TS;

import java.io.Serializable;

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
public class Norm implements Serializable, Cloneable {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;

	/** Norm ID */
	private int id = -1;

	/** Norm Name */
	private String label = "";

	/** the norm verison */
	private int version = 2013;

	/** description of the norm */
	private String description = "";

	/** norm available for actionplan computation */
	private boolean computable = true;

	/***********************************************************************************************
	 * Constructors
	 **********************************************************************************************/

	/**
	 * Constructor:<br>
	 */
	public Norm() {
	}

	/**
	 * Constructor:<br>
	 * 
	 * @param label
	 *            The Norm Name
	 * @throws TrickException 
	 */
	public Norm(String label) throws TrickException {
		this.setLabel(label);
	}

	/**
	 * Constructor:<br>
	 * 
	 * @param label
	 *            The Norm Name
	 * @throws TrickException 
	 */
	public Norm(String label, int version) throws TrickException {
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
	public Norm(String label, int version, String description) throws TrickException {
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
	public Norm(String label, int version, String description, boolean computable) throws TrickException {
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
		Norm other = (Norm) obj;
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
		return "Norm [id=" + id + ", label=" + label + ", version=" + version + ", description=" + description + ", computable=" + computable + "]";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Norm clone() throws CloneNotSupportedException {
		return (Norm) super.clone();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	public Norm duplicate() throws CloneNotSupportedException {
		Norm norm = (Norm) super.clone();
		if (norm.label.equalsIgnoreCase(Constant.NORM_CUSTOM))
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
