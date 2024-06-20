/**
 * 
 */
package lu.itrust.business.ts.model.standard.measure.helper;

import lu.itrust.business.ts.model.standard.Standard;

/**
 * Represents a chapter in a standard.
 */
public class Chapter {

	private Standard standard;

	private String reference;

	/**
	 * Default constructor.
	 */
	public Chapter() {
	}

	/**
	 * Constructor with parameters.
	 * 
	 * @param standard  the standard associated with the chapter
	 * @param reference the reference of the chapter
	 */
	public Chapter(Standard standard, String reference) {
		this.standard = standard;
		this.reference = reference;
	}

	/**
	 * Gets the standard associated with the chapter.
	 * 
	 * @return the standard
	 */
	public Standard getStandard() {
		return standard;
	}

	/**
	 * Sets the standard associated with the chapter.
	 * 
	 * @param standard the standard to set
	 */
	public void setStandard(Standard standard) {
		this.standard = standard;
	}

	/**
	 * Gets the reference of the chapter.
	 * 
	 * @return the reference
	 */
	public String getReference() {
		return reference;
	}

	/**
	 * Sets the reference of the chapter.
	 * 
	 * @param reference the reference to set
	 */
	public void setReference(String reference) {
		this.reference = reference;
	}

	/**
	 * Generates a hash code for the chapter.
	 * 
	 * @return the hash code
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((standard == null) ? 0 : standard.hashCode());
		result = prime * result + ((reference == null) ? 0 : reference.hashCode());
		return result;
	}

	/**
	 * Checks if this chapter is equal to another object.
	 * 
	 * @param obj the object to compare
	 * @return true if the chapters are equal, false otherwise
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Chapter other = (Chapter) obj;
		if (standard == null) {
			if (other.standard != null)
				return false;
		} else if (!standard.equals(other.standard))
			return false;
		if (reference == null) {
			if (other.reference != null)
				return false;
		} else if (!reference.equals(other.reference))
			return false;
		return true;
	}

}
