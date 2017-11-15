package lu.itrust.business.TS.usermanagement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.persistence.Cacheable;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.MapKeyColumn;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import lu.itrust.business.TS.model.general.Customer;

/**
 * User: <br>
 * Detailed description...
 *
 * @author oensuifudine itrust consulting s.a.rl.:
 * @version
 * @since Aug 19, 2012
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class User implements Serializable, IUser {

	/**
	 * Authorise ALL Connexion type
	 */
	@Transient
	public static final int BOTH_CONNEXION = 0;

	/**
	 * ONLY LDAP
	 */
	@Transient
	public static final int LADP_CONNEXION = 1;

	@Transient
	public static final String LDAP_KEY_PASSWORD = "!-_-!LDAP connexion is required.!-_-!";

	/**
	 * Authorise Only Standard connexion
	 */
	@Transient
	public static final int STANDARD_CONNEXION = -1;

	@Transient
	private static final String DEFAULT_LANGUAGE = "default-language";

	@Transient
	private static final long serialVersionUID = 1L;

	private static final String USER_2_FACTOR_SECRET = "user-2-factor-secret";

	private static final String USER_USING_2_FACTOR_AUTHENTICATION = "user-using-2-factor-authentication";

	/** Fields */

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idUser", length = 12)
	private Integer id = -1;

	@Column(name = "dtConnexionType", nullable = false)
	private int connexionType = BOTH_CONNEXION;

	@ManyToMany
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@JoinTable(name = "UserCustomer", joinColumns = { @JoinColumn(name = "fiUser", nullable = false, updatable = false) }, inverseJoinColumns = {
			@JoinColumn(name = "fiCustomer", nullable = false, updatable = false) }, uniqueConstraints = @UniqueConstraint(columnNames = { "fiUser", "fiCustomer" }))
	@Cascade(CascadeType.ALL)
	private List<Customer> customers = null;

	@Column(name = "dtEmail", nullable = false, unique = true)
	private String email = null;

	@Column(name = "dtEnabled", nullable = false)
	private boolean enable = true;

	@Column(name = "dtFirstName", nullable = false)
	private String firstName = null;

	@Column(name = "dtLastName", nullable = false)
	private String lastName = null;

	@Column(name = "dtLocale", nullable = false)
	private String locale = "en";

	@Column(name = "dtLogin", nullable = false, unique = true)
	private String login = null;

	@Column(name = "dtPassword", nullable = false)
	private String password = null;

	@Column(name = "dtEmailValidated", nullable = false)
	private boolean emailValidated = false;

	@Transient
	private String repeatPassword = null;

	@ManyToMany
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@JoinTable(name = "UserRole", joinColumns = { @JoinColumn(name = "fiUser", nullable = false, updatable = false) }, inverseJoinColumns = {
			@JoinColumn(name = "fiRole", nullable = false, updatable = false) })
	private List<Role> roles = null;

	@ElementCollection
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@MapKeyColumn(name = "dtName")
	@Column(name = "dtValue")
	@Cascade(CascadeType.ALL)
	@CollectionTable(name = "UserSetting", joinColumns = @JoinColumn(name = "fiUser"))
	private Map<String, String> userSettings = new HashMap<String, String>();

	/**
	 * Constructor: <br>
	 *
	 */
	public User() {
		roles = new ArrayList<Role>();
		customers = new ArrayList<Customer>();
	}

	public User(String username, String firstName, String lastName, String email, int connexionType) {
		this.login = username;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.connexionType = connexionType;
		if (connexionType == LADP_CONNEXION)
			this.password = LDAP_KEY_PASSWORD;
	}

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
	 * disable: <br>
	 * Removes all accounts and disables it.
	 * 
	 */
	public void disable() {
		enable = !clearRole();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.usermanagement.IUser#getAccess()
	 */
	@Override
	public RoleType getAccess() {
		RoleType[] roleTypes = RoleType.values();
		for (int i = roleTypes.length - 1; i >= 0; i--) {
			if (hasRole(roleTypes[i]))
				return roleTypes[i];
		}
		return null;
	}

	/**
	 * @return the connexionType
	 */
	public int getConnexionType() {
		return connexionType;
	}

	/**
	 * @return the customers
	 */
	public List<Customer> getCustomers() {
		return customers;
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
	 * getFirstName: <br>
	 * Description
	 * 
	 * @return
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * return {@value User#firstName} +" "+ {@value #lastName}
	 */
	@Override
	public String getFullname() {
		return (firstName == null ? "" : firstName + " ") + (lastName == null ? "" : lastName);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.usermanagement.IUser#getLogin()
	 */
	@Override
	public String getLogin() {
		return login;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.usermanagement.IUser#getPassword()
	 */
	@Override
	public String getPassword() {
		return password;
	}

	/**
	 * @return the repeatPassword
	 */
	public String getRepeatPassword() {
		return repeatPassword;
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
	 * getRoles: <br>
	 * Description
	 * 
	 * @return
	 */
	public List<Role> getRoles() {
		return roles;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.usermanagement.IUser#getScrete()
	 */
	@Override
	public String getSecret() {
		return getUserSettings().get(User.USER_2_FACTOR_SECRET);
	}

	public String getSetting(String name) {
		return this.userSettings.get(name);
	}

	public Map<String, String> getUserSettings() {
		return userSettings;
	}

	public boolean hasRole(Role role) {
		return role == null || roles == null || roles.isEmpty() ? false : roles.contains(role);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.usermanagement.IUser#isEnable()
	 */
	@Override
	public boolean isEnable() {
		return enable;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.usermanagement.IUser#isUsing2FA()
	 */
	@Override
	public boolean isUsing2FA() {
		return Boolean.valueOf(getSetting(User.USER_USING_2_FACTOR_AUTHENTICATION));
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

	public String removeSetting(String name) {
		return userSettings.remove(name);
	}

	/**
	 * @param connexionType
	 *            the connexionType to set
	 */
	public void setConnexionType(int connexionType) {
		this.connexionType = connexionType;
	}

	/**
	 * @param customers
	 *            the customers to set
	 */
	public void setCustomers(List<Customer> customers) {
		this.customers = customers;
	}

	/**
	 * setEmail: <br>
	 * Description
	 * 
	 * @param email
	 */
	public void setEmail(String email) {
		if (this.email != null && isEmailValidated())
			setEmailValidated(this.email.equalsIgnoreCase(email));
		this.email = email;
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
	 * setFirstName: <br>
	 * Description
	 * 
	 * @param firstName
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
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
	 * setLastName: <br>
	 * Description
	 * 
	 * @param lastName
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
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
	 * setLogin: <br>
	 * Description
	 * 
	 * @param login
	 */
	public void setLogin(String login) {
		this.login = login;
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
	 * @param repeatPassword
	 *            the repeatPassword to set
	 */
	public void setRepeatPassword(String repeatPassword) {
		this.repeatPassword = repeatPassword;
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

	public void setSecret(String secret) {
		setSetting(USER_2_FACTOR_SECRET, secret);
	}

	public void setSetting(String name, Object value) {
		if (name == null)
			return;
		else if (value == null)
			this.userSettings.remove(name);
		else
			this.userSettings.put(name, String.valueOf(value));
	}

	public void setUserSettings(Map<String, String> userSettings) {
		this.userSettings = userSettings;
	}

	public void setUsing2FA(boolean using2FA) {
		setSetting(USER_USING_2_FACTOR_AUTHENTICATION, using2FA);
	}

	private boolean clearRole() {
		if (roles == null)
			return true;
		else
			roles.clear();
		return roles.isEmpty();
	}

	public boolean isEmailValidated() {
		return emailValidated;
	}

	public void setEmailValidated(boolean emailValidated) {
		this.emailValidated = emailValidated;
	}

}