package lu.itrust.business.TS.model.general;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.exception.TrickException;

/**
 * Language: <br>
 * This class contains all data concerning a language.
 * 
 * @author itrust consulting s.à r.l. - SME,BJA
 * @version 0.1
 * @since 2012-09-24
 */
@Entity
public class Language {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** The Language Identifier */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idLanguage")
	private int id = -1;

	/** The Alpha3 Code */
	@Column(name = "dtAlpha3", length = 3, nullable = false, unique = true)
	private String alpha3 = "";

	/** The Language Name */
	@Column(name = "dtName", nullable = false)
	private String name = "";

	/** The Language Alternative Name */
	@Column(name = "dtAlternativeName", nullable = false)
	private String altName = "";

	/***********************************************************************************************
	 * Getters and Setters
	 **********************************************************************************************/

	public Language() {
	}

	public Language(String alpha3, String name, String altName) {
		setAlpha3(alpha3);
		setName(name);
		setAltName(altName);
	}

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
	 * @throws TrickException
	 */
	public void setId(int id) throws TrickException {
		if (id < 1)
			throw new TrickException("error.language.id", "ID should be greater than 0");
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
	 * @throws TrickException
	 */
	public void setAlpha3(String alpha3) throws TrickException {
		if (alpha3 == null || !alpha3.matches(Constant.REGEXP_VALID_ALPHA_3))
			throw new TrickException("error.language.alpha3.rejected", "Alpha3 has been rejected");
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
		return "Language [id=" + id + ", alpha3=" + alpha3 + ", name=" + name + ", altName=" + altName + "]";
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

	@Transient
	public String getAlpha2() {
		if (alpha3 == null || alpha3.length() != 3)
			return null;
		return alpha3.substring(0, 2);
	}
}