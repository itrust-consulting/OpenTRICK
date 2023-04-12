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
import org.springframework.core.io.support.ResourcePropertySource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author eomar
 *
 */
@Profile({ "p-auth-all", "p-auth-std-ad", "p-auth-std-ldap", "p-auth-std", "p-auth-ldap", "p-auth-ad" })
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
		loadSpringJpaProperties(env, properties.getProperties());
		properties.getProperties().put("hibernate.dialect", env.getProperty("jdbc.dialect"));
		properties.getProperties().put("hibernate.show_sql", env.getProperty("jdbc.show_sql"));
		properties.getProperties().put("hibernate.jdbc.time_zone", env.getProperty("jdbc.time_zone"));
		properties.getProperties().put("hibernate.hbm2ddl.auto", env.getProperty("jdbc.hbm2ddl.auto"));
		properties.getProperties().put("hibernate.javax.cache.missing_cache_strategy",
				env.getProperty("jdbc.cache.missing_cache_strategy"));
		properties.getProperties().put("hibernate.javax.cache.provider",
				env.getProperty("jdbc.cache.provider"));
		properties.getProperties().put("hibernate.cache.use_query_cache",
				env.getProperty("jdbc.cache.use_query_cache"));
		properties.getProperties().put("hibernate.cache.region.factory_class",
				env.getProperty("jdbc.cache.factory_class"));
		properties.getProperties().put("hibernate.cache.use_second_level_cache",
				env.getProperty("jdbc.cache.use_second_level"));
		if (resource.exists())
			properties.getProperties().put("hibernate.javax.cache.uri", resource.getURI().toString());

		return properties;
	}

	public static void loadSpringJpaProperties(final Environment env, final Map<String, String> properties) {
		StreamSupport.stream(
				((AbstractEnvironment) env).getPropertySources().spliterator(), false)
				.filter(EnumerablePropertySource.class::isInstance)
				.map(e -> ((EnumerablePropertySource<?>) e).getPropertyNames()).flatMap(Arrays::<String>stream)
				.filter(e -> e.startsWith("spring.jpa") && e.contains("hibernate")).forEach(e -> properties
						.put(e.substring(e.indexOf("hibernate")), env.getProperty(e)));

		//translate spring jap properties to hibernate
		
		var value = properties.remove("hibernate.keyword_auto_quoting_enabled");
		if (value != null)
			properties.put("hibernate.auto_quote_keyword", value);
	}
}
