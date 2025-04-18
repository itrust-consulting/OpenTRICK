package lu.itrust.business.ts.usermanagement;

public interface IUser {

	/**
	 * Returns the login of the user.
	 * 
	 * @return the login of the user
	 */
	String getLogin();

	/**
	 * Returns the password of the user.
	 * 
	 * @return the password of the user
	 */
	String getPassword();

	/**
	 * Returns whether the user is enabled or not.
	 * 
	 * @return true if the user is enabled, false otherwise
	 */
	boolean isEnable();
	
	/**
	 * Retrieves the two-factor secret for the user.
	 * 
	 * @return the two-factor secret
	 */
	default String getSecret(){
		return null;
	}
	
	/**
	 * Returns whether the user is using two-factor authentication or not.
	 * 
	 * @return true if the user is using two-factor authentication, false otherwise
	 */
	default boolean isUsing2FA(){
		return false;
	}

	/**
	 * Returns the access rights of the user.
	 * 
	 * @return the access rights of the user
	 */
	RoleType getAccess();
	
	/**
	 * Returns the full name of the user.
	 * 
	 * @return the full name of the user
	 */
	String getFullname();

}