/**
 * 
 */
package lu.itrust.business.ts.usermanagement.helper;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.ppolicy.PasswordPolicyControl;
import org.springframework.security.ldap.ppolicy.PasswordPolicyResponseControl;
import org.springframework.security.ldap.userdetails.LdapUserDetailsImpl;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import lu.itrust.business.ts.component.TrickLogManager;
import lu.itrust.business.ts.database.dao.DAORole;
import lu.itrust.business.ts.database.dao.DAOUser;
import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.usermanagement.Role;
import lu.itrust.business.ts.usermanagement.RoleType;
import lu.itrust.business.ts.usermanagement.User;

/**
 * Based on org.springframework.security.ldap.userdetails.LdapUserDetailsMapper
 * by Luke Taylor supported role
 * ROLE_USER,ROLE_CONSULTANT,ROLE_ADMIN,ROLE_SUPERVISOR
 * This class is an implementation of the UserDetailsContextMapper interface
 * and is responsible for mapping LDAP user details to a custom User object.
 * It provides methods for configuring various attributes and roles used during
 * the mapping process.
 *
 * The class also includes methods for loading and formatting roles, mapping
 * the user's password, and creating authorities from role attributes.
 *
 * Note: This class requires the DAOUser and DAORole beans to be autowired.
 */
public class TRICKLdapUserDetailsMapper implements UserDetailsContextMapper {

	private String firstNameAttribute = "givenName";

	private String lastNameAttribute = "sn";

	private String emailAttribute = "mail";

	private String fullNameAttribute = "displayName";

	private String passwordAttributeName = "userPassword";

	private String rolePrefix = "ROLE_";

	private boolean convertToUpperCase = true;

	private boolean alwaysLoadRole = true;

	private boolean allowedAuthentication = false;

	private String defaultRole = RoleType.ROLE_USER.name();

	protected boolean initialised = false;

	private List<String> supervisorRoles;

	private List<String> adminRoles;

	private List<String> consultantRoles;

	private List<String> userRoles;

	private String[] roleAttributes;

	@Autowired
	private DAOUser daoUser;

	@Autowired
	private DAORole daoRole;

	protected void initialisation() {
		if (!initialised) {
			synchronized (this) {
				if (!initialised) {
					formatRoles(supervisorRoles);
					formatRoles(adminRoles);
					formatRoles(consultantRoles);
					formatRoles(userRoles);
					initialised = true;
				}
			}
		}
	}

	/**
	 * Interface representing the details of a user.
	 * Implementations of this interface are responsible for providing the necessary information
	 * about a user, such as their username, password, authorities, and other attributes.
	 * This information is used by the authentication and authorization processes in an application.
	 */
	@Transactional
	@Override
	public UserDetails mapUserFromContext(DirContextOperations ctx, String username,
			Collection<? extends GrantedAuthority> authorities) {

		try {
			if (!initialised)
				initialisation();

			LdapUserDetailsImpl.Essence essence = new LdapUserDetailsImpl.Essence();

			essence.setDn(ctx.getNameInNamespace());

			Object passwordValue = ctx.getObjectAttribute(passwordAttributeName);

			if (passwordValue != null) {
				essence.setPassword(mapPassword(passwordValue));
			}

			String firstName = ctx.getStringAttribute(firstNameAttribute);
			String lastName = ctx.getStringAttribute(lastNameAttribute);
			String email = ctx.getStringAttribute(emailAttribute);
			String fullName = ctx.getStringAttribute(fullNameAttribute);

			if (firstName == null) {
				if (fullName == null)
					firstName = username;
				else
					firstName = fullName;
			}

			if (lastName == null) {
				if (fullName == null)
					lastName = username;
				else
					lastName = fullName;
			}

			User user = daoUser.get(username);
			if (user == null) {
				if (StringUtils.hasText(email))
					user = daoUser.getByEmail(email);
				else
					throw new TrickException("error.ldap.email.empty",
							"Please contact your administrator, your email cannot be loaded");
				if (user == null)
					user = new User(username, firstName, lastName, email, User.LADP_CONNEXION);
			} else if (!(email == null || email.equalsIgnoreCase(user.getEmail())) && daoUser.existByEmail(email))
				throw new TrickException("error.ldap.conflit.account",
						"Please contact your administrator, your username and email are both in use by two different people");

			if (!allowedAuthentication || user.getConnexionType() == User.STANDARD_CONNEXION)
				throw new BadCredentialsException("User is not authorised to connect via LDAP");

			if (user.getId() < 1 || alwaysLoadRole)
				loadRoles(ctx, authorities, essence, user);
			else
				user.getRoles()
						.forEach(role -> essence.addAuthority(new SimpleGrantedAuthority(role.getType().name())));

			if (!user.isEnable())
				throw new DisabledException("User account is disabled");

			essence.setUsername(user.getLogin());

			// Check for PPolicy data
			PasswordPolicyResponseControl ppolicy = (PasswordPolicyResponseControl) ctx
					.getObjectAttribute(PasswordPolicyControl.OID);

			if (ppolicy != null) {
				essence.setTimeBeforeExpiration(ppolicy.getTimeBeforeExpiration());
				essence.setGraceLoginsRemaining(ppolicy.getGraceLoginsRemaining());
			}
			return essence.createUserDetails();
		} catch (BadCredentialsException | DisabledException e) {
			throw e;
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			throw new InternalAuthenticationServiceException(e.getMessage(), e);
		}
	}

	/**
	 * Loads the roles for the user from the LDAP context and adds them to the user details.
	 *
	 * @param ctx         the DirContextOperations object representing the LDAP context
	 * @param authorities the collection of GrantedAuthority objects representing the user's authorities
	 * @param essence     the LdapUserDetailsImpl.Essence object representing the user details essence
	 * @param user        the User object representing the user
	 * @throws Exception if an error occurs while loading the roles
	 */
	private void loadRoles(DirContextOperations ctx, Collection<? extends GrantedAuthority> authorities,
			LdapUserDetailsImpl.Essence essence, User user) throws Exception {
		// Map the roles
		if (roleAttributes != null) {
			for (int i = 0; i < roleAttributes.length; i++) {
				String[] rolesForAttribute = ctx.getStringAttributes(roleAttributes[i]);
				if (rolesForAttribute != null) {
					for (String role : rolesForAttribute) {
						GrantedAuthority authority = createAuthority(role);
						if (authority != null) {
							essence.addAuthority(authority);
						}
					}
				}
			}
		}

		// Add the supplied authorities
		for (GrantedAuthority authority : authorities) {
			AddRole(essence, authority, supervisorRoles, RoleType.ROLE_SUPERVISOR.name());
			AddRole(essence, authority, adminRoles, RoleType.ROLE_ADMIN.name());
			AddRole(essence, authority, consultantRoles, RoleType.ROLE_CONSULTANT.name());
			AddRole(essence, authority, userRoles, RoleType.ROLE_USER.name());
		}

		if (essence.getGrantedAuthorities().isEmpty() && StringUtils.hasText(defaultRole))
			essence.addAuthority(new SimpleGrantedAuthority(defaultRole));

		user.disable();

		for (GrantedAuthority grantedAuthority : essence.getGrantedAuthorities()) {
			Role role = daoRole.getByName(grantedAuthority.getAuthority());
			if (role == null) {
				role = new Role(RoleType.valueOf(grantedAuthority.getAuthority()));
				daoRole.saveOrUpdate(role);
			}
			user.addRole(role);
		}
		daoUser.saveOrUpdate(user);

	}

	/**
	 * Adds a role to the specified `LdapUserDetailsImpl.Essence` object if the given authority is present in the roles list.
	 * 
	 * @param essence The `LdapUserDetailsImpl.Essence` object to which the role will be added.
	 * @param authority The `GrantedAuthority` object representing the authority to check against the roles list.
	 * @param roles The list of roles to check against.
	 * @param roleName The name of the role to add if the authority is present in the roles list.
	 */
	private void AddRole(LdapUserDetailsImpl.Essence essence, GrantedAuthority authority, List<String> roles,
			String roleName) {
		if (roles != null && roles.stream().anyMatch(role -> role.equalsIgnoreCase(authority.getAuthority())))
			essence.addAuthority(new SimpleGrantedAuthority(roleName));
	}

	/**
	 * Maps the given UserDetails object to the provided DirContextAdapter object.
	 * This method is not supported in the TRICKLdapUserDetailsMapper class and will throw an UnsupportedOperationException.
	 * Subclasses should override this method if mapUserToContext() functionality is required.
	 *
	 * @param user The UserDetails object to be mapped.
	 * @param ctx The DirContextAdapter object to which the user details will be mapped.
	 * @throws UnsupportedOperationException if called in the TRICKLdapUserDetailsMapper class.
	 */
	public void mapUserToContext(UserDetails user, DirContextAdapter ctx) {
		throw new UnsupportedOperationException("LdapUserDetailsMapper only supports reading from a context. Please"
				+ "use a subclass if mapUserToContext() is required.");
	}

	/**
	 * Extension point to allow customized creation of the user's password from
	 * the attribute stored in the directory.
	 *
	 * @param passwordValue
	 *                      the value of the password attribute
	 * @return a String representation of the password.
	 */
	protected String mapPassword(Object passwordValue) {

		if (!(passwordValue instanceof String)) {
			// Assume it's binary
			passwordValue = new String((byte[]) passwordValue);
		}

		return (String) passwordValue;

	}

	/**
	 * Creates a GrantedAuthority from a role attribute. Override to customize
	 * authority object creation.
	 * <p>
	 * The default implementation converts string attributes to roles, making
	 * use of the <tt>rolePrefix</tt> and <tt>convertToUpperCase</tt>
	 * properties. Non-String attributes are ignored.
	 * </p>
	 *
	 * @param role
	 *             the attribute returned from
	 * @return the authority to be added to the list of authorities for the
	 *         user, or null if this attribute should be ignored.
	 */
	protected GrantedAuthority createAuthority(Object role) {
		if (role instanceof String) {
			if (convertToUpperCase) {
				role = ((String) role).toUpperCase();
			}
			return new SimpleGrantedAuthority(rolePrefix + role);
		}
		return null;
	}

	/**
	 * Determines whether role field values will be converted to upper case when
	 * loaded. The default is true.
	 *
	 * @param convertToUpperCase
	 *                           true if the roles should be converted to upper
	 *                           case.
	 */
	public void setConvertToUpperCase(boolean convertToUpperCase) {
		this.convertToUpperCase = convertToUpperCase;
	}

	/**
	 * The name of the attribute which contains the user's password. Defaults to
	 * "userPassword".
	 *
	 * @param passwordAttributeName
	 *                              the name of the attribute
	 */
	public void setPasswordAttributeName(String passwordAttributeName) {
		this.passwordAttributeName = passwordAttributeName;
	}

	/**
	 * The names of any attributes in the user's entry which represent
	 * application roles. These will be converted to <tt>GrantedAuthority</tt>s
	 * and added to the list in the returned LdapUserDetails object. The
	 * attribute values must be Strings by default.
	 *
	 * @param roleAttributes
	 *                       the names of the role attributes.
	 */
	public void setRoleAttributes(String[] roleAttributes) {
		this.roleAttributes = roleAttributes;
	}

	/**
	 * The prefix that should be applied to the role names
	 * 
	 * @param rolePrefix
	 *                   the prefix (defaults to "ROLE_").
	 */
	public void setRolePrefix(String rolePrefix) {
		this.rolePrefix = rolePrefix;
	}

	/**
	 * Returns the first name attribute.
	 *
	 * @return the first name attribute
	 */
	public String getFirstNameAttribute() {
		return firstNameAttribute;
	}

	/**
	 * Sets the attribute name for the first name in the LDAP user details.
	 *
	 * @param firstNameAttribute the attribute name for the first name
	 */
	public void setFirstNameAttribute(String firstNameAttribute) {
		this.firstNameAttribute = firstNameAttribute;
	}

	/**
	 * Returns the last name attribute.
	 *
	 * @return the last name attribute
	 */
	public String getLastNameAttribute() {
		return lastNameAttribute;
	}

	/**
	 * Sets the attribute name for the last name in the LDAP user details.
	 *
	 * @param lastNameAttribute the attribute name for the last name
	 */
	public void setLastNameAttribute(String lastNameAttribute) {
		this.lastNameAttribute = lastNameAttribute;
	}

	/**
	 * Returns the list of supervisor roles.
	 *
	 * @return the list of supervisor roles
	 */
	public List<String> getSupervisorRoles() {
		return supervisorRoles;
	}

	/**
	 * Sets the supervisor roles for the user.
	 * 
	 * @param supervisorRoles an array of supervisor roles
	 */
	public void setSupervisorRoles(String[] supervisorRoles) {
		if (supervisorRoles != null)
			this.supervisorRoles = Arrays.asList(supervisorRoles);
	}

	/**
	 * Formats the roles by adding a prefix and converting them to uppercase if required.
	 *
	 * @param roles the list of roles to be formatted
	 */
	private void formatRoles(List<String> roles) {
		if (roles == null || roles.isEmpty())
			return;
		for (int i = 0; i < roles.size(); i++)
			roles.set(i,
					String.format("%s%s", rolePrefix, convertToUpperCase ? roles.get(i).toUpperCase() : roles.get(i)));
	}

	/**
	 * Returns the list of admin roles.
	 *
	 * @return the list of admin roles
	 */
	public List<String> getAdminRoles() {
		return adminRoles;
	}

	/**
	 * Sets the admin roles for the user.
	 * 
	 * @param adminRoles an array of admin roles to be set
	 */
	public void setAdminRoles(String[] adminRoles) {
		if (adminRoles != null)
			this.adminRoles = Arrays.asList(adminRoles);
	}

	/**
	 * Returns the list of consultant roles.
	 *
	 * @return the list of consultant roles
	 */
	public List<String> getConsultantRoles() {
		return consultantRoles;
	}

	/**
	 * Sets the consultant roles for the user.
	 * 
	 * @param consultantRoles an array of consultant roles
	 */
	public void setConsultantRoles(String[] consultantRoles) {
		if (consultantRoles != null)
			this.consultantRoles = Arrays.asList(consultantRoles);
	}

	/**
	 * Returns the roles assigned to the user.
	 *
	 * @return a list of strings representing the user roles
	 */
	public List<String> getUserRoles() {
		return userRoles;
	}

	/**
	 * Sets the roles for the user.
	 *
	 * @param userRoles an array of roles to be assigned to the user
	 */
	public void setUserRoles(String[] userRoles) {
		if (userRoles != null)
			this.userRoles = Arrays.asList(userRoles);
	}

	/**
	 * Returns the password attribute name.
	 *
	 * @return the password attribute name as a String.
	 */
	public String getPasswordAttributeName() {
		return passwordAttributeName;
	}

	/**
	 * Returns the role attributes.
	 *
	 * @return an array of role attributes
	 */
	public String[] getRoleAttributes() {
		return roleAttributes;
	}

	/**
	 * Returns the role prefix.
	 *
	 * @return the role prefix as a {@code String}
	 */
	public String getRolePrefix() {
		return rolePrefix;
	}

	/**
	 * Returns whether the conversion to upper case is enabled or not.
	 *
	 * @return true if the conversion to upper case is enabled, false otherwise
	 */
	public boolean isConvertToUpperCase() {
		return convertToUpperCase;
	}

	/**
	 * @return the emailAttribute
	 */
	public String getEmailAttribute() {
		return emailAttribute;
	}

	/**
	 * @param emailAttribute
	 *                       the emailAttribute to set
	 */
	public void setEmailAttribute(String emailAttribute) {
		this.emailAttribute = emailAttribute;
	}

	/**
	 * @return the fullNameAttribute
	 */
	public String getFullNameAttribute() {
		return fullNameAttribute;
	}

	/**
	 * @param fullNameAttribute
	 *                          the fullNameAttribute to set
	 */
	public void setFullNameAttribute(String fullNameAttribute) {
		this.fullNameAttribute = fullNameAttribute;
	}

	/**
	 * @return the alwaysLoadRole
	 */
	public boolean isAlwaysLoadRole() {
		return alwaysLoadRole;
	}

	/**
	 * @param alwaysLoadRole
	 *                       the alwaysLoadRole to set
	 */
	public void setAlwaysLoadRole(boolean alwaysLoadRole) {
		this.alwaysLoadRole = alwaysLoadRole;
	}

	/**
	 * @return the allowedAuthentication
	 */
	public boolean isAllowedAuthentication() {
		return allowedAuthentication;
	}

	/**
	 * @param allowedAuthentication
	 *                              the allowedAuthentication to set
	 */
	public void setAllowedAuthentication(boolean allowedAuthentication) {
		this.allowedAuthentication = allowedAuthentication;
	}

	/**
	 * @return the defaultRole
	 */
	public String getDefaultRole() {
		return defaultRole;
	}

	/**
	 * @param defaultRole the defaultRole to set
	 */
	public void setDefaultRole(String defaultRole) {
		this.defaultRole = defaultRole;
	}
}
