package lu.itrust.business.ts.usermanagement;

/**
 * The ChangePasswordhelper class represents a helper class for changing passwords.
 */
public class ChangePasswordhelper {
	
	private String requestId;
	
	private String password;
	
	private String repeatPassword;
	

	/**
	 * Constructs a new ChangePasswordhelper object.
	 */
	public ChangePasswordhelper() {
	}

	/**
	 * Constructs a new ChangePasswordhelper object with the specified request ID.
	 * 
	 * @param requestId the request ID
	 */
	public ChangePasswordhelper(String requestId) {
		this.requestId = requestId;
	}

	/**
	 * Returns the password.
	 * 
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Sets the password.
	 * 
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Returns the repeated password.
	 * 
	 * @return the repeated password
	 */
	public String getRepeatPassword() {
		return repeatPassword;
	}

	/**
	 * Sets the repeated password.
	 * 
	 * @param repeatPassword the repeated password to set
	 */
	public void setRepeatPassword(String repeatPassword) {
		this.repeatPassword = repeatPassword;
	}

	/**
	 * Returns the request ID.
	 * 
	 * @return the request ID
	 */
	public String getRequestId() {
		return requestId;
	}

	/**
	 * Sets the request ID.
	 * 
	 * @param requestId the request ID to set
	 */
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

}
