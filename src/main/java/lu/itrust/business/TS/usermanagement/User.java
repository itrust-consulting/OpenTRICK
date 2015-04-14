package lu.itrust.business.TS.usermanagement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import lu.itrust.business.TS.model.general.Customer;

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
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "dtLogin", "dtEmail" }))
public class User implements Serializable {

	@Transient
	private static final String DEFAULT_LANGUAGE = "default-language";

	@Transient
	private static final long serialVersionUID = 1L;

	/** Fields */

	@Id
	@GeneratedValue
	@Column(name = "idUser")
	private Integer id = -1;

	@Column(name = "dtLogin", nullable = false)
	private String login = null;

	@Column(name = "dtPassword", nullable = false)
	private String password = null;

	@Transient
	private String repeatPassword = null;

	@Column(name = "dtFirstName", nullable = false)
	private String firstName = null;

	@Column(name = "dtLastName", nullable = false)
	private String lastName = null;

	@Column(name = "dtEmail", nullable = false)
	private String email = null;

	@Column(name = "dtEnabled", nullable = false, columnDefinition = "TINYINT(1)")
	private boolean enable = true;

	@ManyToMany
	@JoinTable(name = "UserRole", joinColumns = { @JoinColumn(name = "fiUser", nullable = false, updatable = false) }, inverseJoinColumns = { @JoinColumn(name = "fiRole", nullable = false, updatable = false) })
	private List<Role> roles = null;

	@ManyToMany
	@JoinTable(name = "UserCustomer", joinColumns = { @JoinColumn(name = "fiUser", nullable = false, updatable = false) }, inverseJoinColumns = { @JoinColumn(name = "fiCustomer", nullable = false, updatable = false) }, uniqueConstraints = @UniqueConstraint(columnNames = {
			"fiUser", "fiCustomer" }))
	@Cascade(CascadeType.ALL)
	private List<Customer> customers = null;

	@Column(name = "dtLocale", nullable = false)
	private String locale = "en";

	@ElementCollection
	@MapKeyColumn(name = "dtName")
	@Column(name = "dtValue")
	@Cascade(CascadeType.ALL)
	@CollectionTable(name = "UserSetting", joinColumns = @JoinColumn(name = "fiUser"))
	private Map<String, String> userSettings = new HashMap<String, String>();

	/**
	 * Constructor: <br>
	 * 
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
	 * Constructor: <br>
	 *
	 */
	public User() {
		roles = new ArrayList<Role>();
		customers = new ArrayList<Customer>();
	}

	/**
	 * getId: <br>
	 * Description
	 * 
	 * @return
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * setId: <br>
	 * Description
	 * 
	 * @param id
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * getLogin: <br>
	 * Description
	 * 
	 * @return
	 */
	public String getLogin() {
		return login;
	}

	/**
	 * setLogin: <br>
	 * Description
	 * 
	 * @param login
	 */
	public void setLogin(String login) {
		this.login = login;
	}

	/**
	 * getPassword: <br>
	 * Description
	 * 
	 * @return
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * setPassword: <br>
	 * Description
	 * 
	 * @param password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * getFirstName: <br>
	 * Description
	 * 
	 * @return
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * setFirstName: <br>
	 * Description
	 * 
	 * @param firstName
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * getLastName: <br>
	 * Description
	 * 
	 * @return
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * setLastName: <br>
	 * Description
	 * 
	 * @param lastName
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * getEmail: <br>
	 * Description
	 * 
	 * @return
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * setEmail: <br>
	 * Description
	 * 
	 * @param email
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * isEnable: <br>
	 * Description
	 * 
	 * @return
	 */
	public boolean isEnable() {
		return enable;
	}

	/**
	 * setEnable: <br>
	 * Description
	 * 
	 * @param enable
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

	/**
	 * getRoles: <br>
	 * Description
	 * 
	 * @return
	 */
	public List<Role> getRoles() {
		return roles;
	}

	/**
	 * setRoles: <br>
	 * Description
	 * 
	 * @param roles
	 */
	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	/**
	 * addRole: <br>
	 * Description
	 * 
	 * @param role
	 * @return
	 */
	public boolean addRole(Role role) {
		boolean add = false;

		if (roles == null)
			roles = new ArrayList<Role>();

		if (!roles.contains(role))
			add = roles.add(role);

		enable = !roles.isEmpty();
		return add;
	}

	/**
	 * containsRole: <br>
	 * Description
	 * 
	 * @param role
	 * @return
	 */
	public boolean containsRole(Object role) {
		return roles.contains(role);
	}

	/**
	 * getRole: <br>
	 * Description
	 * 
	 * @param role
	 * @return
	 */
	public Role getRole(Role role) {
		if (roles == null)
			return null;
		return roles.get(roles.indexOf(role));
	}

	/**
	 * getLocale: <br>
	 * Description
	 * 
	 * @return
	 */
	public String getLocale() {
		String local = getSetting(DEFAULT_LANGUAGE);
		if (local == null)
			setLocale(local = "en");
		return local;
	}

	public Locale getLocaleObject() {
		return new Locale(getLocale());
	}

	/**
	 * setLocale: <br>
	 * Description
	 * 
	 * @param locale
	 */
	public void setLocale(String locale) {
		setSetting(DEFAULT_LANGUAGE, locale);
	}

	/**
	 * setLocaleObject: <br>
	 * Description
	 * 
	 * @param locale
	 */
	public void setLocaleObject(Locale locale) {
		setLocale(locale.getISO3Language().substring(0, 2));
	}

	/**
	 * isAutorised: <br>
	 * Description
	 * 
	 * @param role
	 * @return
	 */
	public boolean isAutorised(RoleType role) {

		if (role != null && roles != null) {
			for (Role role2 : roles)
				if (role2.getType().ordinal() >= role.ordinal())
					return true;
		}

		return false;

	}

	/**
	 * isAutorised: <br>
	 * Description
	 * 
	 * @param role
	 * @return
	 */
	public boolean isAutorised(String role) {

		return isAutorised(RoleType.valueOf(role));
	}

	/**
	 * removeRole: <br>
	 * Description
	 * 
	 * @param role
	 * @return
	 */
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
		if (roles == null || roleType == null)
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

	public Map<String, String> getUserSettings() {
		return userSettings;
	}

	public void setUserSettings(Map<String, String> userSettings) {
		this.userSettings = userSettings;
	}

	public String getSetting(String name) {
		return this.userSettings.get(name);
	}

	public Integer getInteger(String name) {
		try {
			String value = getSetting(name);
			if (value == null)
				return null;
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	public void setSetting(String name, Object value) {
		this.userSettings.put(name, String.valueOf(value));
	}

}