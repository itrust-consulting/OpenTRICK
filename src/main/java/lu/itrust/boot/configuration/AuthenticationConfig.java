package lu.itrust.boot.configuration;

import java.util.Arrays;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.ldap.LdapAuthenticationProviderConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;

import lu.itrust.business.ts.usermanagement.helper.CustomerDaoAuthenticationProvider;
import lu.itrust.business.ts.usermanagement.helper.TRICKLdapUserDetailsMapper;

/**
 * This class represents the configuration for authentication in the
 * application.
 * It provides beans and methods for configuring the authentication manager,
 * user details service,
 * and authentication providers based on the active profiles.
 * The configuration includes settings for standard authentication, LDAP
 * authentication, and Active Directory authentication.
 * The class also uses a data source, environment variables, and a password
 * encoder for authentication purposes.
 */
@Configuration
public class AuthenticationConfig {

        private static final String SELECT_USER_DT_LOGIN_AS_USERNAME_ROLE_DT_TYPE_FROM_USER_ROLE_USER_ROLE_WHERE_USER_ROLE_FI_USER_USER = "SELECT User.dtLogin as username, Role.dtType FROM UserRole, User, Role WHERE UserRole.fiUser = User.idUser AND UserRole.fiRole = Role.idRole AND User.dtLogin = ?";
        private static final String SELECT_DT_LOGIN_AS_USERNAME_DT_PASSWORD_AS_PASSWORD_DT_ENABLED_AS_ENABLE_FROM_USER_WHERE_1_DT_CONNEX = "SELECT dtLogin as username, dtPassword as password, dtEnabled as enable from User where 1> dtConnexionType and dtLogin=?";

        @Autowired
        private DataSource dataSource;

        @Autowired
        private Environment environment;

        @Autowired
        private PasswordEncoder passwordEncoder;

        @Autowired
        private @Lazy AuthenticationManager authenticationManager;

        /**
         * Returns the authentication manager for the application.
         * This method is annotated with @Bean to indicate that it should be managed by
         * the Spring container.
         * It is also annotated with @Primary to indicate that it is the primary
         * authentication manager when multiple are available.
         * The authentication manager is configured based on the provided HttpSecurity
         * object.
         *
         * @param http the HttpSecurity object used to configure the authentication
         *             manager
         * @return the configured authentication manager
         * @throws Exception if an error occurs while building the authentication
         *                   manager
         */
        @Bean
        @Primary
        @Profile({ "p-auth-std", "p-auth-ldap", "p-auth-ad" })
        public AuthenticationManager authenticationManager(HttpSecurity http)
                        throws Exception {
                return http.getSharedObject(AuthenticationManagerBuilder.class).build();
        }

        /**
         * Configures the authentication manager builder based on the active profiles.
         * 
         * @param builder the AuthenticationManagerBuilder instance to configure
         * @throws Exception if an error occurs during configuration
         */
        @Autowired
        @Profile({ "p-auth-std", "p-auth-ldap", "p-auth-ad" })
        public void configure(AuthenticationManagerBuilder builder) throws Exception {
                var activeProfiles = Arrays.asList(environment.getActiveProfiles());
                if (activeProfiles.contains("p-auth-std"))
                        builder.authenticationProvider(customDaoAuthenticationProvider());
                if (activeProfiles.contains("p-auth-ldap"))
                        initLdapAuthenticationProvider(builder.ldapAuthentication());
                if (activeProfiles.contains("p-auth-ad"))
                        builder.authenticationProvider(adAuthenticationProvider());

        }

        /**
         * Creates a custom instance of {@link DaoAuthenticationProvider}.
         * This bean is only active when the "p-auth-std" profile is active.
         *
         * @return The custom {@link DaoAuthenticationProvider} instance.
         */
        @Bean
        @Profile("p-auth-std")
        public DaoAuthenticationProvider customDaoAuthenticationProvider() {
                var dao = new CustomerDaoAuthenticationProvider();
                dao.setUserDetailsService(userDetailsService());
                dao.setPasswordEncoder(passwordEncoder);
                return dao;
        }

        /**
         * Creates and configures a UserDetailsManager bean.
         * 
         * @return The configured UserDetailsManager bean.
         */
        @Bean
        @Profile("p-auth-std")
        public UserDetailsManager userDetailsService() {
                var userDetailsService = new JdbcUserDetailsManager(dataSource);
                userDetailsService.setUsersByUsernameQuery(
                                SELECT_DT_LOGIN_AS_USERNAME_DT_PASSWORD_AS_PASSWORD_DT_ENABLED_AS_ENABLE_FROM_USER_WHERE_1_DT_CONNEX);
                userDetailsService.setAuthoritiesByUsernameQuery(
                                SELECT_USER_DT_LOGIN_AS_USERNAME_ROLE_DT_TYPE_FROM_USER_ROLE_USER_ROLE_WHERE_USER_ROLE_FI_USER_USER);
                userDetailsService.setAuthenticationManager(authenticationManager);
                return userDetailsService;
        }

        /**
         * Creates an instance of the ActiveDirectoryLdapAuthenticationProvider class to provide authentication using Active Directory.
         * This method is annotated with @Bean to indicate that the returned object should be managed by the Spring container.
         * This method is also annotated with @Profile("p-auth-ad") to specify that this bean should be created only when the "p-auth-ad" profile is active.
         * 
         * @return The ActiveDirectoryLdapAuthenticationProvider instance configured with the necessary properties.
         */
        @Bean
        @Profile("p-auth-ad")
        public AuthenticationProvider adAuthenticationProvider() {
                var authenticationProvider = new ActiveDirectoryLdapAuthenticationProvider(
                                environment.getRequiredProperty("app.settings.ldap.server.manager_dn"),
                                environment.getRequiredProperty("app.settings.ldap.server.url"));
                authenticationProvider.setUserDetailsContextMapper(userDetailsContextMapper());
                return authenticationProvider;
        }

        /**
         * This class is responsible for mapping LDAP user details to the TRICK application's user details.
         * It provides methods to set various properties related to LDAP user mapping.
         */
        @Bean
        @Profile({ "p-auth-ad", "p-auth-ldap" })
        public TRICKLdapUserDetailsMapper userDetailsContextMapper() {
                var userDetailsContextMapper = new TRICKLdapUserDetailsMapper();

                userDetailsContextMapper
                                .setAdminRoles(environment.getRequiredProperty("app.settings.ldap.role.admin",
                                                String[].class));
                userDetailsContextMapper.setAllowedAuthentication(
                                environment.getRequiredProperty("app.settings.ldap.allowed.authentication",
                                                Boolean.class));
                userDetailsContextMapper.setAlwaysLoadRole(
                                environment.getRequiredProperty("app.settings.ldap.always.load.role", Boolean.class));
                userDetailsContextMapper.setConsultantRoles(
                                environment.getRequiredProperty("app.settings.ldap.role.consultant", String[].class));
                userDetailsContextMapper
                                .setDefaultRole(environment.getRequiredProperty("app.settings.ldap.default.role"));
                userDetailsContextMapper
                                .setFirstNameAttribute(environment
                                                .getRequiredProperty("app.settings.ldap.attribute.firstname"));
                userDetailsContextMapper
                                .setLastNameAttribute(environment
                                                .getRequiredProperty("app.settings.ldap.attribute.lastname"));
                userDetailsContextMapper
                                .setPasswordAttributeName(environment
                                                .getRequiredProperty("app.settings.ldap.attribute.user_password"));
                userDetailsContextMapper.setSupervisorRoles(
                                environment.getRequiredProperty("app.settings.ldap.role.supervisor", String[].class));
                userDetailsContextMapper.setRoleAttributes(
                                environment.getRequiredProperty("app.settings.ldap.role.attributes", String[].class));
                userDetailsContextMapper
                                .setUserRoles(environment.getRequiredProperty("app.settings.ldap.role.user",
                                                String[].class));
                userDetailsContextMapper
                                .setRolePrefix(environment.getRequiredProperty("app.settings.ldap.role.prefix"));

                return userDetailsContextMapper;

        }

        /**
         * Initializes the LDAP authentication provider.
         *
         * @param ldap the LdapAuthenticationProviderConfigurer instance
         */
        private void initLdapAuthenticationProvider(
                        LdapAuthenticationProviderConfigurer<?> ldap) {

                ldap.userDetailsContextMapper(userDetailsContextMapper())
                                .contextSource().url(environment.getRequiredProperty("app.settings.ldap.server.url"))
                                .managerDn(environment.getRequiredProperty("app.settings.ldap.server.manager_dn"))
                                .managerPassword(environment
                                                .getRequiredProperty("app.settings.ldap.server.manager_password"))
                                .and()
                                .userDnPatterns(environment.getRequiredProperty("app.settings.ldap.user_dn_pattern"))
                                .userSearchBase(environment.getRequiredProperty("app.settings.ldap.user_search_base"))
                                .userSearchFilter(
                                                environment.getRequiredProperty("app.settings.ldap.user_search_filter"))

                                .groupRoleAttribute(environment.getRequiredProperty(
                                                "app.settings.ldap.group_role_attribute"))
                                .groupSearchBase(environment.getRequiredProperty(
                                                "app.settings.ldap.group_search_base"))
                                .groupSearchFilter(environment.getRequiredProperty(
                                                "app.settings.ldap.group_search_filter"));

        }

}
