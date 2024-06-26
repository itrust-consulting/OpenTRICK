package lu.itrust.business.ts.usermanagement.helper;

import org.springframework.util.StringUtils;

/**
 * The ResetPasswordHelper class represents a helper class for resetting passwords.
 * It stores the username and email associated with the password reset request.
 */
public class ResetPasswordHelper {

	private String username;

	private String email;

	public ResetPasswordHelper() {
	}

	/** Getters and Setters **/
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

	public String getData() {
		return StringUtils.hasText(username) ? username : email;
	}

}
