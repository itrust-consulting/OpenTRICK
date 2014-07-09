/**
 * 
 */
package lu.itrust.business.component.helper;

import lu.itrust.business.TS.Norm;

/**
 * @author eomar
 *
 */
public class Chapter {
	
	private Norm norm;
	
	private String reference;

	/**
	 * 
	 */
	public Chapter() {
	}

	/**
	 * @param norm
	 * @param reference
	 */
	public Chapter(Norm norm, String reference) {
		this.norm = norm;
		this.reference = reference;
	}

	/**
	 * @return the norm
	 */
	public Norm getNorm() {
		return norm;
	}

	/**
	 * @param norm the norm to set
	 */
	public void setNorm(Norm norm) {
		this.norm = norm;
	}

	/**
	 * @return the reference
	 */
	public String getReference() {
		return reference;
	}

	/**
	 * @param reference the reference to set
	 */
	public void setReference(String reference) {
		this.reference = reference;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((norm == null) ? 0 : norm.hashCode());
		result = prime * result + ((reference == null) ? 0 : reference.hashCode());
		return result;
	}

	/* (non-Javadoc)
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
		Chapter other = (Chapter) obj;
		if (norm == null) {
			if (other.norm != null)
				return false;
		} else if (!norm.equals(other.norm))
			return false;
		if (reference == null) {
			if (other.reference != null)
				return false;
		} else if (!reference.equals(other.reference))
			return false;
		return true;
	}

}
