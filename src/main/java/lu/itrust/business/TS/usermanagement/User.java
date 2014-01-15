/**
 * 
 */
package lu.itrust.business.TS.usermanagement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lu.itrust.business.TS.Customer;

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

	private String repeatPassword = null;

	private String firstName = null;

	private String lastName = null;

	private String email = null;

	private boolean enable = true;

	private List<Role> roles = new ArrayList<Role>();

	private List<UserCustomer> customers = new ArrayList<UserCustomer>();

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
		roles = new ArrayList<Role>();
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

	/**
	 * disable: <br>
	 * Removes all accounts and disables it.
	 * 
	 */
	public void disable() {
		this.roles.clear();
		enable = !roles.isEmpty();
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public boolean addRole(Role role) {
		boolean add = false;

		if (roles == null)
			roles = new ArrayList<Role>();

		if (!roles.contains(role))
			add = roles.add(role);

		enable = !roles.isEmpty();
		return add;
	}

	public boolean containsRole(Object role) {
		return roles.contains(role);
	}

	public Role getRole(Role role) {
		if (roles == null)
			return null;
		return roles.get(roles.indexOf(role));
	}

	public boolean isAutorise(RoleType role) {

		if (role != null && roles != null) {

			for (Role role2 : roles)
				if (role2.getType().ordinal() >= role.ordinal())
					return true;
		}

		return false;

	}

	public boolean isAutorised(String role) {

		return isAutorise(RoleType.valueOf(role));
	}

	public Role removeRole(Role role) {

		if (roles != null) {

			roles.remove(role);
		}

		enable = roles != null && !roles.isEmpty();

		return role;
	}

	/**
	 * @return the repeatPassword
	 */
	public String getRepeatPassword() {
		return repeatPassword;
	}

	/**
	 * @param repeatPassword
	 *            the repeatPassword to set
	 */
	public void setRepeatPassword(String repeatPassword) {
		this.repeatPassword = repeatPassword;
	}

	/**
	 * hasRole: <br>
	 * Description
	 * 
	 * @param role
	 * @return
	 */
	public boolean hasRole(RoleType roleType) {
		for (Role role : roles) {
			if (role.getType().equals(roleType)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * getCustomers: <br>
	 * Returns the customers field value.
	 * 
	 * @return The value of the customers field
	 */
	public List<UserCustomer> getUserCustomers() {
		return customers;
	}

	/**
	 * getCustomers: <br>
	 * Returns the customers field value.
	 * 
	 * @return The value of the customers field
	 */
	public List<Customer> getCustomers() {
		List<Customer> customerlist = new ArrayList<Customer>();
		for (UserCustomer uc : customers) {
			customerlist.add(uc.getCustomer());
		}
		return customerlist;
	}

	/**
	 * setUserCustomer: <br>
	 * Sets the Field "customers" with a value.
	 * 
	 * @param customers
	 *            The Value to set the customers field
	 */
	public void setCustomers(List<UserCustomer> customers) {
		this.customers = customers;
	}

	/**
	 * addCustomer: <br>
	 * Description
	 * 
	 * @param customer
	 * @return
	 */
	public boolean addUserCustomer(UserCustomer customer) {
		return this.customers.add(customer);
	}

	/**
	 * addCustomer: <br>
	 * Description
	 * 
	 * @param customer
	 * @return
	 */
	public boolean addCustomer(Customer customer) {
		return this.customers.add(new UserCustomer(this, customer));
	}

	/**
	 * removeCustomer: <br>
	 * Description
	 * 
	 * @param customer
	 * @return
	 */
	public boolean removeUserCustomer(UserCustomer customer) {

		return this.customers.remove(customer);
	}

	/**
	 * hasCustomer: <br>
	 * Description
	 * 
	 * @param customer
	 * @return
	 */
	boolean hasUserCustomer(UserCustomer customer) {

		return customers.contains(customer);
	}

	/**
	 * hasCustomer: <br>
	 * Description
	 * 
	 * @param customer
	 * @return
	 */
	boolean hasCustomer(Customer customer) {

		for (UserCustomer uc : customers) {
			if (uc.getCustomer().equals(customer)) {
				return true;
			}
		}

		return false;
	}
}