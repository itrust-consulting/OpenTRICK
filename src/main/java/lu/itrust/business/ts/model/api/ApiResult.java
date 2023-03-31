/**
 * 
 */
package lu.itrust.business.ts.model.api;

/**
 * Represents an object describing the outcome of an API call.
 * @author Steve Muller (SMU), itrust consulting s.à r.l.
 * @since Jun 5, 2015
 */
public class ApiResult {
	/**
	 * Initializes a new ApiResult instance without any explicit message.
	 * @param code An error code identifying the reason in case of a failure, zero (0) in case of success.
	 */
	public ApiResult(int code) {
		this(code, null);
	}

	/**
	 * Initializes a new ApiResult instance.
	 * @param code An error code identifying the reason in case of a failure, zero (0) in case of success.
	 * @param message An error message giving more details about the failure.
	 */
	public ApiResult(int code, String message) {
		this.code = code;
		this.message = message;
	}

	/**
	 * Zero (0) in case of success,
	 * otherwise an error code identifying the reason why the call failed.
	 */
	private int code;

	/**
	 * A message which gives more details about why the call failed.
	 */
	private String message;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
