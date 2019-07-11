/**
 * 
 */
package lu.itrust.business.integration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.ServletRegistration;

import org.apache.tomcat.util.descriptor.web.ServletDef;
import org.apache.tomcat.util.descriptor.web.WebXml;
import org.apache.tomcat.util.descriptor.web.WebXmlParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;
import org.springframework.web.servlet.resource.ResourceUrlEncodingFilter;
import org.xml.sax.InputSource;

import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.database.service.ServiceStorage;
import lu.itrust.business.TS.helper.InstanceManager;

/**
 * @author eomar
 *
 */
@Configuration
public class TSConfig {

	@Autowired
	private Environment environment;

	@Autowired
	private ResourceLoader resourceLoader;

	@Autowired
	private DriverManagerDataSource dataSource;

	@Bean
	public CommandLineRunner init(ServiceStorage serviceStorage) {
		return (args) -> {
			serviceStorage.deleteAll();
			serviceStorage.init();
		};
	}

	@Bean
	public FilterRegistrationBean<?> encodingLilter() {
		FilterRegistrationBean<Filter> bean = new FilterRegistrationBean<>();
		bean.setName("encoding-filter");
		bean.setFilter(new CharacterEncodingFilter());
		bean.setAsyncSupported(true);
		bean.addInitParameter("encoding", "UTF-8");
		bean.addInitParameter("forceEncoding", "true");
		bean.addUrlPatterns("/**");
		return bean;
	}

	@Bean
	public InstanceManager instanceManager() {
		return InstanceManager.getInstance();
	}

	@Bean
	public TrickLogManager trickLogManager() {
		return TrickLogManager.getInstance();
	}

	@Bean
	public ResourceUrlEncodingFilter resourceUrlEncodingFilter() {
		return new ResourceUrlEncodingFilter();
	}

	@Bean
	protected ResourceHttpRequestHandler faviconRequestHandler() {
		final ResourceHttpRequestHandler requestHandler = new ResourceHttpRequestHandler();
		requestHandler.setLocations(Arrays.<Resource>asList(new ClassPathResource("classpath:/WEB-INF/static/images/")));
		return requestHandler;
	}

	@Bean
	public SimpleUrlHandlerMapping faviconHandlerMapping() {
		final SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
		mapping.setOrder(Integer.MIN_VALUE);
		mapping.setUrlMap(Collections.singletonMap("favicon.ico", faviconRequestHandler()));
		return mapping;
	}

	@Bean
	public FilterRegistrationBean<?> springSecurityFilterChain() {
		final FilterRegistrationBean<Filter> bean = new FilterRegistrationBean<>();
		bean.setName("springSecurityFilterChain");
		bean.setFilter(new DelegatingFilterProxy());
		bean.setAsyncSupported(true);
		bean.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ASYNC, DispatcherType.ERROR);
		bean.addUrlPatterns("/**");
		return bean;
	}

	@Bean
	//@DependsOn("flyway")
	public LocalSessionFactoryBean sessionFactory() throws IOException {
		final String path = "classpath:/persistence/ehcache-" + environment.getProperty("jdbc.cache.storage.type") + ".xml";
		final Resource resource = resourceLoader.getResource(path);
		final LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
		final Properties properties = new Properties();
		sessionFactory.setDataSource(dataSource);
		sessionFactory.setPackagesToScan("lu.itrust.business.TS");
		properties.put("hibernate.dialect", environment.getProperty("jdbc.dialect"));
		properties.put("hibernate.show_sql", environment.getProperty("jdbc.show_sql"));
		properties.put("hibernate.hbm2ddl.auto", environment.getProperty("jdbc.hbm2ddl.auto"));
		properties.put("hibernate.javax.cache.provider", environment.getProperty("jdbc.cache.provider"));
		properties.put("hibernate.cache.use_query_cache", environment.getProperty("jdbc.cache.use_query_cache"));
		properties.put("hibernate.cache.region.factory_class", environment.getProperty("jdbc.cache.factory_class"));
		properties.put("hibernate.cache.use_second_level_cache", environment.getProperty("jdbc.cache.use_second_level"));
		if (resource.exists())
			properties.put("hibernate.javax.cache.uri", resource.getURI().toString());
		sessionFactory.setHibernateProperties(properties);
		return sessionFactory;
	}

	@Bean
	public ServletContextInitializer initializer() {
		return servletContext -> {
			try (InputStream inputStream = servletContext.getResourceAsStream("/WEB-INF/web.xml")) {
				if (inputStream == null)
					throw new RuntimeException("Web XML cannot be load");
				
				final WebXml webXml = new WebXml();
				final WebXmlParser parser = new WebXmlParser(false, false, true);
				final boolean success = parser.parseWebXml(new InputSource(inputStream), webXml, false);
				if (success) {
					webXml.getContextParams().forEach((name, value) -> servletContext.setInitParameter(name, value));
					for (ServletDef def : webXml.getServlets().values()) {
						ServletRegistration.Dynamic reg = servletContext.addServlet(def.getServletName(), def.getServletClass());
						reg.setLoadOnStartup(1);
					}
					for (Map.Entry<String, String> mapping : webXml.getServletMappings().entrySet())
						servletContext.getServletRegistration(mapping.getValue()).addMapping(mapping.getKey());
				} else
					throw new RuntimeException("Error parsing Web XML");
			} catch (Exception e) {
				TrickLogManager.Persist(e);
			}
		};
	}

}
