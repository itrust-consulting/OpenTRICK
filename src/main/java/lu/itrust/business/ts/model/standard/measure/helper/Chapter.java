/**
 * 
 */
package lu.itrust.business.ts.model.standard.measure.helper;

import lu.itrust.business.ts.model.standard.Standard;


/**
 * @author eomar
 *
 */
public class Chapter {

	private Standard standard;

	private String reference;

	/**
	 * 
	 */
	public Chapter() {
	}

	/**
	 * Constructor: <br>
	 * 
	 * @param standard
	 * @param reference
	 */
	public Chapter(Standard standard, String reference) {
		this.standard = standard;
		this.reference = reference;
	}

	/**
	 * getStandard: <br>
	 * Description
	 * 
	 * @return
	 */
	public Standard getStandard() {
		return standard;
	}

	/**
	 * setStandard: <br>
	 * Description
	 * 
	 * @param standard
	 */
	public void setStandard(Standard standard) {
		this.standard = standard;
	}

	/**
	 * getReference: <br>
	 * Description
	 * 
	 * @return
	 */
	public String getReference() {
		return reference;
	}

	/**
	 * setReference: <br>
	 * Description
	 * 
	 * @param reference
	 */
	public void setReference(String reference) {
		this.reference = reference;
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
		result = prime * result + ((standard == null) ? 0 : standard.hashCode());
		result = prime * result + ((reference == null) ? 0 : reference.hashCode());
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
