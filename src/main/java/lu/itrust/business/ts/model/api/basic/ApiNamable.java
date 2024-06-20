/**
 * 
 */
package lu.itrust.business.ts.model.api.basic;


/**
 * This class represents an API namable object.
 * It extends the {@link ApiTrickObject} class.
 */
public class ApiNamable extends ApiTrickObject {
	
	private String name;
	
	/**
	 * Default constructor for the ApiNamable class.
	 */
	public ApiNamable() {
	}

	/**
	 * Constructor for the ApiNamable class.
	 * 
	 * @param id   the ID of the object
	 * @param name the name of the object
	 */
	public ApiNamable(Object id, String name) {
		super(id);
		this.name = name;
	}

	/**
	 * Returns the name of the object.
	 * 
	 * @return the name of the object
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the object.
	 * 
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
}
