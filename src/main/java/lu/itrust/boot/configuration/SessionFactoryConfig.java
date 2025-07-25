/**
 *
 */
package lu.itrust.boot.configuration;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.stream.StreamSupport;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author eomar
 *
 */
/**
 * Configuration class for the SessionFactory in the application.
 * This class provides the necessary beans and configurations for creating a
 * Hibernate SessionFactory.
 * It is responsible for setting up the data source, Hibernate properties, and
 * transaction management.
 * The SessionFactoryConfig class is annotated with @Configuration to indicate
 * that it is a configuration class.
 * It is also annotated with @EnableTransactionManagement to enable transaction
 * management for the application.
 * The class is conditionally enabled based on the active profiles specified in
 * the @Profile annotation.
 */
@Profile({ "p-auth-std", "p-auth-ldap", "p-auth-ad" })
@Configuration(proxyBeanMethods = false)
@EnableTransactionManagement
public class SessionFactoryConfig {

	@Bean
	@DependsOn("flyway")
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

	/**
	 * Creates a bean for the PlatformTransactionManager.
	 *
	 * @param sessionFactory the SessionFactory to be used by the transaction
	 *                       manager
	 * @return the PlatformTransactionManager bean
	 */
	@Bean
	public PlatformTransactionManager transactionManager(SessionFactory sessionFactory) {
		return new HibernateTransactionManager(sessionFactory);
	}

	/**
	 * Represents the properties for configuring JPA (Java Persistence API).
	 * These properties are used to customize the behavior of the JPA
	 * implementation.
	 */
	@Bean
	@Primary
	@ConfigurationProperties("app.jpa.first")
	public JpaProperties firstJpaProperties(Environment env, ResourceLoader resourceLoader) throws IOException {
		final String path = "classpath:/persistence/ehcache-" + env.getProperty("app.settings.hibernate.cache.second_level_cache.type")
				+ ".xml";
		final JpaProperties properties = new JpaProperties();
		final Resource resource = resourceLoader.getResource(path);
		loadSpringJpaProperties(env, properties.getProperties());
		if (resource.exists())
			properties.getProperties().put("hibernate.javax.cache.uri", resource.getURI().toString());

		return properties;
	}

	/**
	 * Loads the Spring JPA properties from the given environment and populates them
	 * into the provided properties map.
	 *
	 * @param env        the environment containing the Spring JPA properties
	 * @param properties the map to populate with the Spring JPA properties
	 */
	public static void loadSpringJpaProperties(final Environment env, final Map<String, String> properties) {
		StreamSupport.stream(
				((AbstractEnvironment) env).getPropertySources().spliterator(), false)
				.filter(EnumerablePropertySource.class::isInstance)
				.map(e -> ((EnumerablePropertySource<?>) e).getPropertyNames()).flatMap(Arrays::<String>stream)
				.filter(e -> e.startsWith("spring.jpa") && e.contains("hibernate")).forEach(e -> properties
						.put(e.substring(e.indexOf("hibernate")), env.getProperty(e))

				);

		// translate spring jap properties to hibernate

		var value = properties.remove("hibernate.keyword_auto_quoting_enabled");
		if (value != null)
			properties.put("hibernate.auto_quote_keyword", value);
	}
}
