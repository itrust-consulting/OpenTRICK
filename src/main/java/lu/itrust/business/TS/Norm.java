package lu.itrust.business.TS;

import java.io.Serializable;

import lu.itrust.business.TS.tsconstant.Constant;

/**
 * Norm: <br>
 * Represents a Norm Name
 * 
 * @author itrust consulting s.à. r.l. : EOM, BJA, SME
 * @version 0.1
 * @since 24 janv. 2013
 */
public class Norm implements Serializable {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;

	/** Norm ID */
	private int id = -1;
	
	/** Norm Name */
	private String label = "";

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
	 */
	public Norm(String label) {
		this.setLabel(label);
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
	 */
	public void setLabel(String label) {
		if ((label == null) || (label.trim().equals(Constant.EMPTY_STRING)))
			throw new IllegalArgumentException("Given Norm Name is not valid!");
		this.label = label;
	}

	/**
	 * hashCode: <br>
	 * Description
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		return result;
	}

	/**
	 * equals: <br>
	 * Description
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
			return false;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		return true;
	}

	/**
	 * toString: <br>
	 * Description
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Norm [id=" + id + ", label=" + label + "]";
	}
}