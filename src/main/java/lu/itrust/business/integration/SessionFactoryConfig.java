/**
 *
 */
package lu.itrust.business.integration;

import java.io.IOException;
import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.hibernate5.support.OpenSessionInViewInterceptor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author eomar
 *
 */
@Configuration(proxyBeanMethods = false)
@EnableTransactionManagement
public class SessionFactoryConfig {

	@Bean
	// @DependsOn("flyway")
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
	public JpaProperties firstJpaProperties(Environment environment, ResourceLoader resourceLoader) throws IOException {
		final String path = "classpath:/persistence/ehcache-" + environment.getProperty("jdbc.cache.storage.type")
				+ ".xml";
		final JpaProperties properties = new JpaProperties();
		final Resource resource = resourceLoader.getResource(path);
		properties.getProperties().put("hibernate.dialect", environment.getProperty("jdbc.dialect"));
		properties.getProperties().put("hibernate.show_sql", environment.getProperty("jdbc.show_sql"));
		properties.getProperties().put("hibernate.jdbc.time_zone", environment.getProperty("jdbc.time_zone"));
		properties.getProperties().put("hibernate.hbm2ddl.auto", environment.getProperty("jdbc.hbm2ddl.auto"));
		properties.getProperties().put("hibernate.javax.cache.missing_cache_strategy",
				environment.getProperty("jdbc.cache.missing_cache_strategy"));
		properties.getProperties().put("hibernate.javax.cache.provider",
				environment.getProperty("jdbc.cache.provider"));
		properties.getProperties().put("hibernate.cache.use_query_cache",
				environment.getProperty("jdbc.cache.use_query_cache"));
		properties.getProperties().put("hibernate.cache.region.factory_class",
				environment.getProperty("jdbc.cache.factory_class"));
		properties.getProperties().put("hibernate.cache.use_second_level_cache",
				environment.getProperty("jdbc.cache.use_second_level"));
		if (resource.exists())
			properties.getProperties().put("hibernate.javax.cache.uri", resource.getURI().toString());
		return properties;
	}
}
