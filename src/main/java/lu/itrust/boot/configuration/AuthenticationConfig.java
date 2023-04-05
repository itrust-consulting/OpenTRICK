package lu.itrust.boot.configuration;

import javax.sql.DataSource;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.ldap.LdapAuthenticationProviderConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;

import lu.itrust.business.ts.usermanagement.helper.CustomerDaoAuthenticationProvider;
import lu.itrust.business.ts.usermanagement.helper.TRICKLdapUserDetailsMapper;

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

    @Order(1)
    @Bean("authenticationManager")
    @Profile("p-auth-all")
    public AuthenticationManager fullAuthenticationManager(HttpSecurity http)
            throws Exception {
        var builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.authenticationProvider(customDaoAuthenticationProvider());
        builder.authenticationProvider(adAuthenticationProvider());
        initLdapAuthenticationProvider(builder.ldapAuthentication());
        return builder.build();
    }

    @Order(1)
    @Bean("authenticationManager")
    @Profile("p-auth-std")
    public AuthenticationManager stdAuthenticationManager(HttpSecurity http)
            throws Exception {
        var builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.authenticationProvider(customDaoAuthenticationProvider());
        return builder.build();
    }

    @Order(1)
    @Bean("authenticationManager")
    @Profile("p-auth-std-ldap")
    public AuthenticationManager stdAndLdapAuthenticationManager(HttpSecurity http)
            throws Exception {
        var builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.authenticationProvider(customDaoAuthenticationProvider());
        initLdapAuthenticationProvider(builder.ldapAuthentication());
        return builder.build();
    }

    @Order(1)
    @Bean("authenticationManager")
    @Profile("p-auth-ad")
    public AuthenticationManager adAuthenticationManager(HttpSecurity http)
            throws Exception {
        var builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.authenticationProvider(adAuthenticationProvider());
        return builder.build();
    }

    @Order(1)
    @Bean("authenticationManager")
    @Profile("p-auth-ldap")
    public AuthenticationManager ldapAuthenticationManager(HttpSecurity http)
            throws Exception {
        var builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        initLdapAuthenticationProvider(builder.ldapAuthentication());
        return builder.build();
    }

    @Bean
    @Profile({ "p-auth-all", "p-auth-std-ldap", "p-auth-std" })
    public DaoAuthenticationProvider customDaoAuthenticationProvider() {
        var dao = new CustomerDaoAuthenticationProvider();
        dao.setUserDetailsService(userDetailsService(dataSource));
        dao.setPasswordEncoder(passwordEncoder);
        return dao;
    }

    @Bean
    @Profile({ "p-auth-all", "p-auth-std-ldap", "p-auth-std" })
    public UserDetailsManager userDetailsService(DataSource dataSource) {
        var userDetailsService = new JdbcUserDetailsManager(dataSource);
        userDetailsService.setUsersByUsernameQuery(
                SELECT_DT_LOGIN_AS_USERNAME_DT_PASSWORD_AS_PASSWORD_DT_ENABLED_AS_ENABLE_FROM_USER_WHERE_1_DT_CONNEX);
        userDetailsService.setAuthoritiesByUsernameQuery(
                SELECT_USER_DT_LOGIN_AS_USERNAME_ROLE_DT_TYPE_FROM_USER_ROLE_USER_ROLE_WHERE_USER_ROLE_FI_USER_USER);
        return userDetailsService;
    }

    @Bean
    @Profile({ "p-auth-all", "p-auth-std-ad", "p-auth-ad" })
    public AuthenticationProvider adAuthenticationProvider() {
        var authenticationProvider = new ActiveDirectoryLdapAuthenticationProvider(
                environment.getRequiredProperty("app.settings.ldap.server.manager_dn"),
                environment.getRequiredProperty("app.settings.ldap.server.url"));
        authenticationProvider.setUserDetailsContextMapper(userDetailsContextMapper());
        return authenticationProvider;
    }

    @Bean
    @Profile({ "p-auth-all", "p-auth-std-ldap", "p-auth-ldap" })
    public BaseLdapPathContextSource contextSource() {
        var contextSource = new DefaultSpringSecurityContextSource(
                environment.getRequiredProperty("app.settings.ldap.server.url"));
        contextSource.setUserDn(environment.getRequiredProperty("app.settings.ldap.server.manager_dn"));
        contextSource.setPassword(environment.getRequiredProperty("app.settings.ldap.server.manager_password"));
        return contextSource;
    }

    @Bean
    @Profile({ "p-auth-all", "p-auth-std-ad", "p-auth-ad", "p-auth-std-ldap", "p-auth-ldap" })
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

    private void initLdapAuthenticationProvider(
            LdapAuthenticationProviderConfigurer<?> ldap) {
        ldap.userDetailsContextMapper(userDetailsContextMapper());
        ldap.userDnPatterns(environment.getRequiredProperty("app.settings.ldap.user_dn_pattern"));
        ldap.userSearchBase(environment.getRequiredProperty("app.settings.ldap.user_search_base"));
        ldap.userSearchFilter(environment.getRequiredProperty("app.settings.ldap.user_search_filter"));
        ldap.groupRoleAttribute(environment.getRequiredProperty(
                "app.settings.ldap.group_role_attribute"));
        ldap.groupSearchBase(environment.getRequiredProperty(
                "app.settings.ldap.group_search_base"));
        ldap.groupSearchFilter(environment.getRequiredProperty(
                "app.settings.ldap.group_search_filter"));

    }

}
