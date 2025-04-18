package lu.itrust.business.ts.model.api.basic;

/**
 * Represents an API trick object.
 */
public class ApiTrickObject {
	
	private Object id;

	/**
	 * Default constructor for ApiTrickObject.
	 */
	public ApiTrickObject() {
	}

	/**
	 * Constructor for ApiTrickObject with specified id.
	 * 
	 * @param id the id of the trick object
	 */
	public ApiTrickObject(Object id) {
		this.id = id;
	}

	/**
	 * Retrieves the id of the trick object.
	 * 
	 * @return the id of the trick object
	 */
	public Object getId() {
		return id;
	}

	/**
	 * Sets the id of the trick object.
	 * 
	 * @param id the id to set
	 */
	public void setId(Object id) {
		this.id = id;
	}
}
