/**
 * 
 */
package lu.itrust.business.ts.model.api;

/**
 * Represents an object describing the outcome of an API call.
 */
/**
 * Represents the result of an API operation.
 */
public class ApiResult {
	/**
	 * Initializes a new ApiResult instance without any explicit message.
	 * 
	 * @param code An error code identifying the reason in case of a failure, zero (0) in case of success.
	 */
	public ApiResult(int code) {
		this(code, null);
	}

	/**
	 * Initializes a new ApiResult instance.
	 * 
	 * @param code    An error code identifying the reason in case of a failure, zero (0) in case of success.
	 * @param message An error message giving more details about the failure.
	 */
	public ApiResult(int code, String message) {
		this.code = code;
		this.message = message;
	}

	
	/**
	 * Zero (0) in case of success, otherwise an error code identifying the reason why the call failed.
	 */
	private int code;

	/**
	 * A message which gives more details about why the call failed.
	 */
	private String message;
	/**
	 * Returns the code associated with this API result.
	 *
	 * @return the code associated with this API result
	 */
	public int getCode() {
		return code;
	}

	/**
	 * Sets the error code.
	 * 
	 * @param code The error code to set. Zero (0) in case of success, otherwise an error code identifying the reason why the call failed.
	 */
	public void setCode(int code) {
		this.code = code;
	}

	/**
	 * Gets the error message.
	 * 
	 * @return The error message, which gives more details about why the call failed.
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Sets the error message.
	 * 
	 * @param message The error message to set, which gives more details about why the call failed.
	 */
	public void setMessage(String message) {
		this.message = message;
	}


}
