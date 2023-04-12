package lu.itrust.business.ts.usermanagement;

public interface IUser {

	/**
	 * getLogin: <br>
	 * Description
	 * 
	 * @return
	 */
	String getLogin();

	/**
	 * getPassword: <br>
	 * Description
	 * 
	 * @return
	 */
	String getPassword();

	/**
	 * isEnable: <br>
	 * Description
	 * 
	 * @return
	 */
	boolean isEnable();
	
	/**
	 * Retrieve 2 factor secrete
	 * @return secrete
	 */
	default String getSecret(){
		return null;
	}
	
	/**
	 * User enable two factor authentication
	 * @return using2FA
	 */
	default boolean isUsing2FA(){
		return false;
	}

	/**
	 * @return Right
	 */
	RoleType getAccess();
	
	/**
	 * 
	 * @return
	 */
	String getFullname();

}