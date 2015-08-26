/**
 * 
 */
package lu.itrust.business.TS.usermanagement.helper;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import lu.itrust.business.TS.database.dao.DAORole;
import lu.itrust.business.TS.database.dao.DAOUser;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.usermanagement.Role;
import lu.itrust.business.TS.usermanagement.RoleType;
import lu.itrust.business.TS.usermanagement.User;

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

/**
 * Base on org.springframework.security.ldap.userdetails.LdapUserDetailsMapper
 * by Luke Taylor supported role
 * ROLE_USER,ROLE_CONSULTANT,ROLE_ADMIN,ROLE_SUPERVISOR
 * 
 * @author eomar
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

	protected Boolean initialised = false;

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
		synchronized (initialised) {
			if (!initialised) {
				formatRoles(supervisorRoles);
				formatRoles(adminRoles);
				formatRoles(consultantRoles);
				formatRoles(userRoles);
				initialised = true;
			}
		}
	}

	@Transactional
	@Override
	public UserDetails mapUserFromContext(DirContextOperations ctx, String username, Collection<? extends GrantedAuthority> authorities) {

		try {
			if (!initialised)
				initialisation();

			LdapUserDetailsImpl.Essence essence = new LdapUserDetailsImpl.Essence();

			essence.setDn(ctx.getNameInNamespace());

			Object passwordValue = ctx.getObjectAttribute(passwordAttributeName);

			if (passwordValue != null) {
				essence.setPassword(mapPassword(passwordValue));
			}

			String firstName = ctx.getStringAttribute(firstNameAttribute), lastName = ctx.getStringAttribute(lastNameAttribute), email = ctx.getStringAttribute(emailAttribute), fullName = ctx
					.getStringAttribute(fullNameAttribute);

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
				if (!StringUtils.isEmpty(email))
					user = daoUser.getByEmail(email);
				if (user == null)
					user = new User(username, firstName, lastName, email, User.LADP_CONNEXION);
			} else if (!(email == null || email.equalsIgnoreCase(user.getEmail())) && daoUser.existByEmail(email))
				throw new TrickException("error.ldap.conflit.account", "Please contact your administrator, your username and email are both in use by two different people");

			if (!allowedAuthentication || user.getConnexionType() == User.STANDARD_CONNEXION)
				throw new BadCredentialsException("User is not authorised to connect via LDAP");

			if (user.getId() < 1 || alwaysLoadRole)
				loadRoles(ctx, authorities, essence, user);

			if (!user.isEnable())
				throw new DisabledException("User account is disabled");

			essence.setUsername(user.getLogin());

			// Check for PPolicy data
			PasswordPolicyResponseControl ppolicy = (PasswordPolicyResponseControl) ctx.getObjectAttribute(PasswordPolicyControl.OID);

			if (ppolicy != null) {
				essence.setTimeBeforeExpiration(ppolicy.getTimeBeforeExpiration());
				essence.setGraceLoginsRemaining(ppolicy.getGraceLoginsRemaining());
			}
			return essence.createUserDetails();
		} catch (BadCredentialsException | DisabledException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new InternalAuthenticationServiceException(e.getMessage(), e);
		}
	}

	private void loadRoles(DirContextOperations ctx, Collection<? extends GrantedAuthority> authorities, LdapUserDetailsImpl.Essence essence, User user) throws Exception {
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

		if (essence.getGrantedAuthorities().isEmpty() && (userRoles == null || userRoles.isEmpty()))
			essence.addAuthority(new SimpleGrantedAuthority(RoleType.ROLE_USER.name()));

		user.disable();

		for (GrantedAuthority grantedAuthority : essence.getGrantedAuthorities()) {
			Role role = daoRole.getByName(grantedAuthority.getAuthority());
			if (role == null)
				role = new Role(RoleType.valueOf(grantedAuthority.getAuthority()));
			user.addRole(role);
		}
		daoUser.saveOrUpdate(user);

	}

	private void AddRole(LdapUserDetailsImpl.Essence essence, GrantedAuthority authority, List<String> roles, String roleName) {
		if (roles != null && roles.stream().anyMatch(role -> role.equalsIgnoreCase(authority.getAuthority())))
			essence.addAuthority(new SimpleGrantedAuthority(roleName));
	}

	public void mapUserToContext(UserDetails user, DirContextAdapter ctx) {
		throw new UnsupportedOperationException("LdapUserDetailsMapper only supports reading from a context. Please" + "use a subclass if mapUserToContext() is required.");
	}

	/**
	 * Extension point to allow customized creation of the user's password from
	 * the attribute stored in the directory.
	 *
	 * @param passwordValue
	 *            the value of the password attribute
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
	 *            the attribute returned from
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
	 *            true if the roles should be converted to upper case.
	 */
	public void setConvertToUpperCase(boolean convertToUpperCase) {
		this.convertToUpperCase = convertToUpperCase;
	}

	/**
	 * The name of the attribute which contains the user's password. Defaults to
	 * "userPassword".
	 *
	 * @param passwordAttributeName
	 *            the name of the attribute
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
	 *            the names of the role attributes.
	 */
	public void setRoleAttributes(String[] roleAttributes) {
		this.roleAttributes = roleAttributes;
	}

	/**
	 * The prefix that should be applied to the role names
	 * 
	 * @param rolePrefix
	 *            the prefix (defaults to "ROLE_").
	 */
	public void setRolePrefix(String rolePrefix) {
		this.rolePrefix = rolePrefix;
	}

	public String getFirstNameAttribute() {
		return firstNameAttribute;
	}

	public void setFirstNameAttribute(String firstNameAttribute) {
		this.firstNameAttribute = firstNameAttribute;
	}

	public String getLastNameAttribute() {
		return lastNameAttribute;
	}

	public void setLastNameAttribute(String lastNameAttribute) {
		this.lastNameAttribute = lastNameAttribute;
	}

	public List<String> getSupervisorRoles() {
		return supervisorRoles;
	}

	public void setSupervisorRoles(String[] supervisorRoles) {
		if (supervisorRoles != null)
			this.supervisorRoles = Arrays.asList(supervisorRoles);
	}

	private void formatRoles(List<String> roles) {
		if (roles == null || roles.isEmpty())
			return;
		for (int i = 0; i < roles.size(); i++)
			roles.set(i, String.format("%s%s", rolePrefix, convertToUpperCase ? roles.get(i).toUpperCase() : roles.get(i)));
	}

	public List<String> getAdminRoles() {
		return adminRoles;
	}

	public void setAdminRoles(String[] adminRoles) {
		if (adminRoles != null)
			this.adminRoles = Arrays.asList(adminRoles);
	}

	public List<String> getConsultantRoles() {
		return consultantRoles;
	}

	public void setConsultantRoles(String[] consultantRoles) {
		if (consultantRoles != null)
			this.consultantRoles = Arrays.asList(consultantRoles);
	}

	public List<String> getUserRoles() {
		return userRoles;
	}

	public void setUserRoles(String[] userRoles) {
		if (userRoles != null)
			this.userRoles = Arrays.asList(userRoles);
	}

	public String getPasswordAttributeName() {
		return passwordAttributeName;
	}

	public String[] getRoleAttributes() {
		return roleAttributes;
	}

	public String getRolePrefix() {
		return rolePrefix;
	}

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
	 *            the emailAttribute to set
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
	 *            the fullNameAttribute to set
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
	 *            the alwaysLoadRole to set
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
	 *            the allowedAuthentication to set
	 */
	public void setAllowedAuthentication(boolean allowedAuthentication) {
		this.allowedAuthentication = allowedAuthentication;
	}
}
