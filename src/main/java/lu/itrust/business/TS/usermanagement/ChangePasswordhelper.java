package lu.itrust.business.TS.usermanagement;

public class ChangePasswordhelper {
	
	private String requestId;
	
	private String password;
	
	private String repeatPassword;
	

	/**
	 * 
	 */
	public ChangePasswordhelper() {
	}

	public ChangePasswordhelper(String keyControl) {
		this.requestId = keyControl;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRepeatPassword() {
		return repeatPassword;
	}

	public void setRepeatPassword(String repeatPassword) {
		this.repeatPassword = repeatPassword;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

}
