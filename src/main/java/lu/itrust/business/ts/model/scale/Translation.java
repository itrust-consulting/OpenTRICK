/**
 * 
 */
package lu.itrust.business.ts.model.scale;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Translation {

	@Column(name = "dtTranslate", nullable = false)
	private String name;

	@Column(name = "dtShortName", nullable = false)
	private String shortName;

	/**
	 * Default constructor for the Translation class.
	 */
	public Translation() {
	}

	/**
	 * Constructor for the Translation class.
	 * 
	 * @param name      the name of the translation
	 * @param shortName the short name of the translation
	 */
	public Translation(String name, String shortName) {
		this.name = name;
		this.shortName = shortName;
	}

	/**
	 * Get the name of the translation.
	 * 
	 * @return the name of the translation
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the name of the translation.
	 * 
	 * @param name the name of the translation to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the short name of the translation.
	 * 
	 * @return the short name of the translation
	 */
	public String getShortName() {
		return shortName;
	}

	/**
	 * Set the short name of the translation.
	 * 
	 * @param shortName the short name of the translation to set
	 */
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

}
