package lu.itrust.business.TS.model.standard;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import lu.itrust.business.TS.exception.TrickException;

/**
 * Standard: <br>
 * Represents a Standard
 * 
 * @author itrust consulting s.à. r.l. : EOM, BJA, SME
 * @version 0.1
 * @since 24 janv. 2013
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "dtLabel", "dtVersion", "dtType" }))
public class Standard implements Cloneable {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** Standard ID */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idStandard")
	private int id = -1;

	/** Standard Name */
	@Column(name = "dtLabel", nullable = false)
	private String label = "";

	/** the Standard verison */
	@Column(name = "dtVersion", nullable = false)
	private int version = 2013;

	/** description of the Standard */
	@Column(name = "dtDescription", nullable = false)
	private String description = "";

	@Enumerated(EnumType.STRING)
	@Column(name = "dtType", nullable = false)
	@Access(AccessType.FIELD)
	@Cascade({ CascadeType.SAVE_UPDATE, CascadeType.MERGE })
	private StandardType type = null;

	/** Standard available for actionplan computation */
	@Column(name = "dtComputable", nullable = false, columnDefinition = "TINYINT(1)")
	private boolean computable = true;

	@Column(name = "dtAnalysisOnly", nullable = false, columnDefinition = "TINYINT(1)")
	private boolean analysisOnly = false;

	/***********************************************************************************************
	 * Constructors
	 **********************************************************************************************/

	/**
	 * Constructor:<br>
	 */
	public Standard() {
	}

	/**
	 * Constructor: <br>
	 * 
	 * @param label
	 * @param type
	 * @param version
	 * @param description
	 * @param computable
	 * @throws TrickException
	 */
	public Standard(String label, StandardType type, int version, String description, boolean computable) throws TrickException {
		this.setLabel(label);
		this.setType(type);
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
			throw new TrickException("error.norm.label", "Name cannot be empty!");
		this.label = label;
	}

	/**
	 * getType: <br>
	 * Returns the type field value.
	 * 
	 * @return The value of the type field
	 */
	public StandardType getType() {
		return type;
	}

	/**
	 * setType: <br>
	 * Sets the Field "type" with a value.
	 * 
	 * @param type
	 *            The Value to set the type field
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

	/**
	 * hashCode: <br>
	 * Description
	 *
	 * @{tags
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

	/**
	 * equals: <br>
	 * Description
	 *
	 * @{tags
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

	/**
	 * toString: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Standard [id=" + id + ", label=" + label + ", version=" + version + ", description=" + description + ", computable=" + computable + "]";
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
	public Standard clone() throws CloneNotSupportedException {
		return (Standard) super.clone();
	}

	/**
	 * duplicate: <br>
	 * Description
	 * 
	 * @return
	 * @throws CloneNotSupportedException
	 */
	public Standard duplicate() throws CloneNotSupportedException {
		Standard standard = (Standard) super.clone();
		standard.id = -1;
		return standard;
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

	/**
	 * getAnalysis: <br>
	 * Description
	 * 
	 * @return
	 */
	public boolean isAnalysisOnly() {
		return analysisOnly;
	}

	/**
	 * setAnalysis: <br>
	 * Description
	 * 
	 * @param analysis
	 */
	public void setAnalysisOnly(boolean analysisOnly) {
		this.analysisOnly = analysisOnly;
	}

	public Standard update(Standard standard) {
		this.analysisOnly = standard.analysisOnly;
		this.computable = standard.computable;
		this.description = standard.description;
		this.version = standard.version;
		this.label = standard.label;
		this.type = standard.type;
		return this;
	}

	public boolean isMatch(Standard standard) {
		return label.equals(standard.label) && standard.type == standard.type;
	}

	public boolean hasSameName(Standard standard) {
		return label.equals(standard.label);
	}

}
