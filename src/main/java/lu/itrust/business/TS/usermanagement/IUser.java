package lu.itrust.business.TS.usermanagement;

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
	 * @return Right
	 */
	RoleType getAccess();
	
	/**
	 * 
	 * @return
	 */
	String getFullname();

}