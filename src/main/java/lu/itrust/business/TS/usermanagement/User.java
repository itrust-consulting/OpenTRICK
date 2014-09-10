package lu.itrust.business.TS.usermanagement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import lu.itrust.business.TS.Customer;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

/**
 * User: <br>
 * Detailed description...
 *
 * @author oensuifudine itrust consulting s.a.rl.:
 * @version 
 * @since Aug 19, 2012
 */
@Entity
@Table(uniqueConstraints=@UniqueConstraint(columnNames={"dtLogin","dtEmail"}))
public class User implements Serializable {

	@Transient
	private static final long serialVersionUID = 1L;

	/** Fields */
	
	@Id @GeneratedValue 
	@Column(name="idUser")
	private Integer id = -1;

	@Column(name="dtLogin", nullable=false)
	private String login = null;

	@Column(name="dtPassword", nullable=false)
	private String password = null;

	@Transient
	private String repeatPassword = null;

	@Column(name="dtFirstName", nullable=false)
	private String firstName = null;

	@Column(name="dtLastName", nullable=false)
	private String lastName = null;

	@Column(name="dtEmail", nullable=false)
	private String email = null;

	@Column(name="dtEnabled", nullable=false, columnDefinition="TINYINT(1)")
	private boolean enable = true;

	@ManyToMany
	@JoinTable(name = "UserRole", 
			   joinColumns = { @JoinColumn(name = "fiUser", nullable = false, updatable = false) }, 
			   inverseJoinColumns = { @JoinColumn(name = "fiRole", nullable = false, updatable = false) }
	)
	private List<Role> roles = new ArrayList<Role>();

	@ManyToMany
	@JoinTable(name = "UserCustomer", 
			   joinColumns = { @JoinColumn(name = "fiUser", nullable = false, updatable = false) }, 
			   inverseJoinColumns = { @JoinColumn(name = "fiCustomer", nullable = false, updatable = false) }
	)
	@Cascade(CascadeType.ALL)
	private List<Customer> customers = new ArrayList<Customer>();

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
	public Integer getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Integer id) {
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

	public boolean isAutorised(RoleType role) {

		if (role != null && roles != null) {

			for (Role role2 : roles)
				if (role2.getType().ordinal() >= role.ordinal())
					return true;
		}

		return false;

	}

	public boolean isAutorised(String role) {

		return isAutorised(RoleType.valueOf(role));
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
		if(roles == null || roleType == null)
			return false;
		for (Role role : roles) {
			if (role.getType().equals(roleType))
				return true;
		}
		return false;
	}

	/**
	 * @return the customers
	 */
	public List<Customer> getCustomers() {
		return customers;
	}

	/**
	 * @param customers
	 *            the customers to set
	 */
	public void setCustomers(List<Customer> customers) {
		this.customers = customers;
	}

	/**
	 * addCustomer: <br>
	 * Description
	 * 
	 * @param arg0
	 * @return
	 */
	public boolean addCustomer(Customer customer) {
		if (!containsCustomer(customer))
			return customers.add(customer);
		return true;
	}

	/**
	 * containsCustomer: <br>
	 * Description
	 * 
	 * @param arg0
	 * @return
	 */
	public boolean containsCustomer(Customer customer) {
		return customers.contains(customer);
	}
	
	/**
	 * removeCustomer: <br>
	 * Description
	 * 
	 * @param customer
	 * @return
	 */
	public boolean removeCustomer(Customer customer) {
		if (customers.contains(customer))
			return customers.remove(customer);
		return true;
	}
}