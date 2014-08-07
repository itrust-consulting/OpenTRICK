/**
 * 
 */
package lu.itrust.business.exception;

/**
 * @author eomar
 *
 */
public class TrickException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String code = null;
	
	private Object [] parameters;

	/**
	 * @param code
	 * @param message
	 */
	public TrickException(String code , String message) {
		super(message);
		this.code = code;
	}

	/**
	 * @param code
	 * @param meassage
	 * @param parameters
	 */
	public TrickException(String code,String meassage, Object[] parameters) {
		super(meassage);
		this.code = code;
		this.parameters = parameters;
	}
	

	public TrickException(String code, String message, String... parameters) {
		super(message);
		this.code = code;
		this.parameters = parameters;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the parameters
	 */
	public Object [] getParameters() {
		return parameters;
	}

	/**
	 * @param parameters the parameters to set
	 */
	public void setParameters(Object [] parameters) {
		this.parameters = parameters;
	}
	
	
	
}
