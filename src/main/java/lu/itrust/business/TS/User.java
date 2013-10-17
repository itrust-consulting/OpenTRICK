/**
 * 
 */
package lu.itrust.business.TS;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author oensuifudine
 * 
 */
public class User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long id = -1;

	private String login = null;

	private String password = null;

	private String firstName = null;

	private String lastName = null;

	private String email = null;

	private boolean enable = true;

	private List<Role> roles = null;

	/**
	 * @param login
	 * @param password
	 * @param firstName
	 * @param lastName
	 * @param email
	 */
	public User(String login, String password, String firstName, String lastName, String email) {
		this.login = login;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
	}

	/**
	 * 
	 */
	public User() {
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the login
	 */
	public String getLogin() {
		return login;
	}

	/**
	 * @param login
	 *            the login to set
	 */
	public void setLogin(String login) {
		this.login = login;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName
	 *            the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName
	 *            the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email
	 *            the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the enable
	 */
	public boolean isEnable() {
		return enable;
	}

	/**
	 * @param enable
	 *            the enable to set
	 */
	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public boolean add(Role arg0) {
		boolean add = false;

		if (roles == null)
			roles = new ArrayList<Role>();

		if (!roles.contains(arg0))
			add = roles.add(arg0);

		enable = !roles.isEmpty();
		return add;
	}

	public boolean contains(Object arg0) {
		return roles.contains(arg0);
	}

	public Role get(int arg0) {
		if (roles == null)
			return null;
		return roles.get(arg0);
	}

	public boolean isAutorise(RoleType role) {

		if (role != null && roles != null) {

			for (Role role2 : roles)
				if (role2.getType().ordinal() >= role.ordinal())
					return true;
		}

		return false;

	}

	public boolean isAutorise(String role) {

		return isAutorise(RoleType.valueOf(role));
	}

	public Role remove(Long roleId) {

		Role role = null;

		if (roles != null) {

			for (int i = 0; i < roles.size(); i++) {
				if (roles.get(i).getId() == roleId) {
					role = roles.remove(i);
					break;
				}
			}
		}

		enable = roles != null && !roles.isEmpty();

		return role;
	}

}
