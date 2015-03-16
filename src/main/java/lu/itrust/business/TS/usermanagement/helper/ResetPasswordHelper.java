package lu.itrust.business.TS.usermanagement.helper;

public class ResetPasswordHelper {
	
	private String username;
	
	private String email;

	/**
	 * 
	 */
	public ResetPasswordHelper() {
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public boolean isEmpty() {
		return (username == null || username.trim().isEmpty()) && (email == null || email.trim().isEmpty());
	}
}
