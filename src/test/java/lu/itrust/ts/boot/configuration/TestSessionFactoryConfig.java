package lu.itrust.ts.boot.configuration;

import java.io.IOException;
import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import lu.itrust.boot.configuration.SessionFactoryConfig;
import lu.itrust.business.ts.usermanagement.helper.CustomerDaoAuthenticationProvider;

@Configuration
@EnableTransactionManagement
public class TestSessionFactoryConfig {

    private static final String SELECT_USER_DT_LOGIN_AS_USERNAME_ROLE_DT_TYPE_FROM_USER_ROLE_USER_ROLE_WHERE_USER_ROLE_FI_USER_USER = "SELECT User.dtLogin as username, Role.dtType FROM UserRole, User, Role WHERE UserRole.fiUser = User.idUser AND UserRole.fiRole = Role.idRole AND User.dtLogin = ?";
    private static final String SELECT_DT_LOGIN_AS_USERNAME_DT_PASSWORD_AS_PASSWORD_DT_ENABLED_AS_ENABLE_FROM_USER_WHERE_1_DT_CONNEX = "SELECT dtLogin as username, dtPassword as password, dtEnabled as enable from User where 1> dtConnexionType and dtLogin=?";

    @Autowired
    private DataSource dataSource;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private @Lazy AuthenticationManager authenticationManager;

    @Order(1)
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http)
            throws Exception {
        var builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.authenticationProvider(customDaoAuthenticationProvider());
        return builder.build();
    }

    @Bean
    @ConditionalOnMissingBean
    public LocalSessionFactoryBean sessionFactory(DataSource firstDataSource, JpaProperties firstJpaProperties) {
        final LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();
        final Properties properties = new Properties();
        firstJpaProperties.getProperties().forEach(properties::put);
        sessionFactoryBean.setDataSource(firstDataSource);
        sessionFactoryBean.setPackagesToScan("lu.itrust.business.ts");
        sessionFactoryBean.setHibernateProperties(properties);
        return sessionFactoryBean;
    }

    @Bean
    public PlatformTransactionManager transactionManager(SessionFactory sessionFactory) {
        return new HibernateTransactionManager(sessionFactory);
    }

    @Bean
    @Primary
    @ConfigurationProperties("app.jpa.first")
    public JpaProperties firstJpaProperties(Environment env, ResourceLoader resourceLoader) throws IOException {
        final String path = "classpath:/persistence/ehcache-" + env.getProperty("jdbc.cache.storage.type")
                + ".xml";
        final JpaProperties properties = new JpaProperties();
        final Resource resource = resourceLoader.getResource(path);
        SessionFactoryConfig.loadSpringJpaProperties(env, properties.getProperties());
        properties.getProperties().put("hibernate.dialect", env.getProperty("jdbc.dialect"));
        properties.getProperties().put("hibernate.show_sql", env.getProperty("jdbc.show_sql"));
        properties.getProperties().put("hibernate.jdbc.time_zone", env.getProperty("jdbc.time_zone"));
        properties.getProperties().put("hibernate.Impl2ddl.auto", env.getProperty("jdbc.Impl2ddl.auto"));
        properties.getProperties().put("hibernate.javax.cache.missing_cache_strategy",
                env.getProperty("jdbc.cache.missing_cache_strategy"));
        properties.getProperties().put("hibernate.javax.cache.provider",
                env.getProperty("jdbc.cache.provider"));
        properties.getProperties().put("hibernate.cache.use_query_cache",
                env.getProperty("jdbc.cache.use_query_cache"));
        properties.getProperties().put("hibernate.cache.region.factory_class",
                env.getProperty("jdbc.cache.factory_class"));
        properties.getProperties().put("hibernate.enable_lazy_load_no_trans", "true");
        properties.getProperties().put("hibernate.cache.use_second_level_cache",
                env.getProperty("jdbc.cache.use_second_level"));
        if (resource.exists())
            properties.getProperties().put("hibernate.javax.cache.uri", resource.getURI().toString());
        return properties;
    }

    @Bean
    public DaoAuthenticationProvider customDaoAuthenticationProvider() {
        var dao = new CustomerDaoAuthenticationProvider();
        dao.setUserDetailsService(userDetailsService(dataSource,authenticationManager));
        dao.setPasswordEncoder(passwordEncoder);
        return dao;
    }

    @Bean
    public UserDetailsManager userDetailsService(DataSource dataSource, AuthenticationManager authenticationManager) {
        var userDetailsService = new JdbcUserDetailsManager(dataSource);
        userDetailsService.setUsersByUsernameQuery(
                SELECT_DT_LOGIN_AS_USERNAME_DT_PASSWORD_AS_PASSWORD_DT_ENABLED_AS_ENABLE_FROM_USER_WHERE_1_DT_CONNEX);
        userDetailsService.setAuthoritiesByUsernameQuery(
                SELECT_USER_DT_LOGIN_AS_USERNAME_ROLE_DT_TYPE_FROM_USER_ROLE_USER_ROLE_WHERE_USER_ROLE_FI_USER_USER);
        userDetailsService.setAuthenticationManager(authenticationManager);
        return userDetailsService;
    }

}
