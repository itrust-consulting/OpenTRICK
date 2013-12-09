package lu.itrust.business.TS;

import java.io.Serializable;

import lu.itrust.business.TS.tsconstant.Constant;

/**
 * Language: <br>
 * This class contains all data concerning a language.
 * 
 * @author itrust consulting s.à r.l. - SME,BJA
 * @version 0.1
 * @since 2012-09-24
 */
public class Language implements Serializable {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;

	/** The Language Identifier */
	private int id = -1;

	/** The Alpha3 Code */
	private String alpha3 = "";

	/** The Language Name */
	private String name = "";

	/** The Language Alternative Name */
	private String altName = "";

	/***********************************************************************************************
	 * Getters and Setters
	 **********************************************************************************************/

	/**
	 * getId: <br>
	 * Returns the "id" field value
	 * 
	 * @return The Language Identifier
	 */
	public int getId() {
		return id;
	}

	/**
	 * setId: <br>
	 * Sets the "id" field with a value
	 * 
	 * @param id
	 *            The value to set the Language Identifier
	 */
	public void setId(int id) {
		if (id < 1) {
			throw new IllegalArgumentException("Language ID needs to be >= 1!");
		}
		this.id = id;
	}

	/**
	 * getAlpha3: <br>
	 * Returns the "alpha3" field value
	 * 
	 * @return The Alpha3 Code
	 */
	public String getAlpha3() {
		return alpha3;
	}

	/**
	 * setAlpha3: <br>
	 * Sets the "alpha3" field with a value
	 * 
	 * @param alpha3
	 *            The value to set the Alpha3 Code
	 */
	public void setAlpha3(String alpha3) {
		if ((alpha3 == null) || (!alpha3.matches(Constant.REGEXP_VALID_ALPHA_3))) {
			throw new IllegalArgumentException(
					"Language Alpha3 should meet this regular expression " + Constant.REGEXP_VALID_ALPHA_3);
		}
		this.alpha3 = alpha3;
	}

	/**
	 * getName: <br>
	 * Returns the "name" field value
	 * 
	 * @return The Language Name
	 */
	public String getName() {
		return name;
	}

	/**
	 * setName: <br>
	 * Sets the "name" field with a value
	 * 
	 * @param name
	 *            The value to set the Language Name
	 */
	public void setName(String name) {
//		if ((name == null) || (!name.matches(Constant.REGEXP_VALID_NAME))) {
//			throw new IllegalArgumentException(
//					"Language Name cannot be null or empty and must be valid!");
//		}
		this.name = name;
	}

	/**
	 * getName: <br>
	 * Returns the "altName" field value
	 * 
	 * @return The Alternative Language Name
	 */
	public String getAltName() {
		return altName;
	}

	/**
	 * setName: <br>
	 * Sets the "altName" field with a value
	 * 
	 * @param name
	 *            The value to set the Alternative Language Name
	 */
	public void setAltName(String name) {
//		if ((name == null) || (!name.matches(Constant.REGEXP_VALID_NAME))) {
//			throw new IllegalArgumentException(
//					"Language Alternative Name cannot be null or empty and must be valid!");
//		}
		this.altName = name;
	}

	/**
	 * toString: <br>
	 * Description
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Language [id=" + id + ", alpha3=" + alpha3 + ", name=" + name + ", altName="
			+ altName + "]";
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
		result = prime * result + ((alpha3 == null) ? 0 : alpha3.hashCode());
		result = prime * result + id;
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
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Language)) {
			return false;
		}
		Language other = (Language) obj;
		if (getAlpha3() == null) {
			if (other.getAlpha3() != null) {
				return false;
			}
		} else if (!getAlpha3().equals(other.getAlpha3())) {
			return false;
		}
		return true;
	}
}