/**
 * 
 */
package lu.itrust.business.ts.model.scale;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * @author eomar
 *
 */
@Embeddable
public class Translation {

	@Column(name = "dtTranslate", nullable = false)
	private String name;

	@Column(name = "dtShortName", nullable = false)
	private String shortName;

	/**
	 * 
	 */
	public Translation() {
	}

	/**
	 * @param language
	 * @param name
	 * @param shortName
	 */
	public Translation(String name, String shortName) {
		this.name = name;
		this.shortName = shortName;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the shortName
	 */
	public String getShortName() {
		return shortName;
	}

	/**
	 * @param shortName
	 *            the shortName to set
	 */
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

}
