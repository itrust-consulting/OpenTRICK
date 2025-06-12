/**
 *
 */
package lu.itrust.boot.configuration;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.tomcat.util.descriptor.web.ServletDef;
import org.apache.tomcat.util.descriptor.web.WebXml;
import org.apache.tomcat.util.descriptor.web.WebXmlParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;
import org.springframework.web.servlet.resource.ResourceUrlEncodingFilter;
import org.xml.sax.InputSource;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRegistration;
import lu.itrust.business.ts.component.DataCleaner;
import lu.itrust.business.ts.component.TrickLogManager;
import lu.itrust.business.ts.database.service.ServiceStorage;
import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.helper.InstanceManager;
import lu.itrust.business.ts.usermanagement.listner.helper.PasswordEncryptionHelper;


/**
 * Configuration class for the application.
 * This class defines various beans and configurations required for the application.
 */
@Configuration
public class AppConfig {

	/**
	 * Initialize the database with some data.
	 * 
	 * @param serviceStorage the service storage
	 * @param password       the password
	 * @return the command line runner
	 */

	@Bean
	public CommandLineRunner init(ServiceStorage serviceStorage,
			@Value("${app.settings.password.ecryption.key}") String password) {
		return (args) -> {
			serviceStorage.deleteAll();
			serviceStorage.init();
			PasswordEncryptionHelper.setPassword(password);
		};
	}

	/**
	 * Returns the singleton instance of the InstanceManager.
	 *
	 * @return the singleton instance of the InstanceManager
	 */
	@Bean
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public InstanceManager instanceManager() {
		return InstanceManager.getInstance();
	}

	/**
	 * Returns an instance of TrickLogManager.
	 * 
	 * @return the TrickLogManager instance
	 */
	@Bean
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public TrickLogManager trickLogManager() {
		return TrickLogManager.getInstance();
	}

	/**
	 * This class represents a data cleaner that can be used to clean data.
	 */
	@Bean
	public DataCleaner dataCleaner() {
		return new DataCleaner();
	}

	/**
	 * Creates and configures a DataSource object based on the provided environment properties.
	 * The DataSource object is used for establishing a connection to the database.
	 *
	 * @param environment the environment object containing the necessary properties for configuring the DataSource
	 * @return the configured DataSource object
	 */
	@Bean
	public DataSource dataSource(Environment environment) {
		var dataSourceBuilder = DataSourceBuilder.create();
		dataSourceBuilder.driverClassName(environment.getRequiredProperty("jdbc.driverClassName"));
		dataSourceBuilder.url(environment.getRequiredProperty("jdbc.databaseurl"));
		dataSourceBuilder.username(environment.getRequiredProperty("jdbc.username"));
		dataSourceBuilder.password(environment.getRequiredProperty("jdbc.password"));
		return dataSourceBuilder.build();
	}

	/**
	 * Creates and configures a MessageSource bean.
	 * The MessageSource is responsible for resolving messages from different sources,
	 * such as property files or databases, based on a given message key.
	 * 
	 * @return the configured MessageSource bean
	 */
	@Bean
	public MessageSource messageSource() {
		var messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.addBasenames("classpath:/languages/messages");
		messageSource.setUseCodeAsDefaultMessage(true);
		messageSource.setFallbackToSystemLocale(false);
		messageSource.setDefaultEncoding("UTF-8");
		return messageSource;
	}

	/**
	 * This class represents a filter that encodes resource URLs.
	 * It is used to ensure that resource URLs are properly encoded when accessed.
	 */
	@Bean
	public ResourceUrlEncodingFilter resourceUrlEncodingFilter() {
		return new ResourceUrlEncodingFilter();
	}

	/**
	 * Configures the MultipartResolver bean for handling multipart requests.
	 * This bean is responsible for resolving multipart requests and converting them into a format that can be processed by the application.
	 * It uses the StandardServletMultipartResolver implementation.
	 */
	@Bean
	public MultipartResolver multipartResolver() {
		return new StandardServletMultipartResolver();
	}

	/**
	 * A factory bean for creating FreeMarker configuration instances.
	 * This bean is used to configure the FreeMarker template engine.
	 */
	@Bean
	public FreeMarkerConfigurationFactoryBean freemarkerConfiguration(
			@Value("${app.settings.email.template}") String path) {
		var bean = new FreeMarkerConfigurationFactoryBean();
		bean.setTemplateLoaderPath(path);
		return bean;
	}

	/**
	 * Creates and configures a JavaMailSender instance based on the provided environment settings.
	 * 
	 * @param env the environment object containing the necessary properties for configuring the JavaMailSender
	 * @return a configured JavaMailSender instance
	 */
	@Bean
	public JavaMailSender javaMailSender(Environment env) {
		var jmail = new JavaMailSenderImpl();
		jmail.setHost(env.getRequiredProperty("app.settings.smtp.host"));
		jmail.setPort(env.getRequiredProperty("app.settings.smtp.port", Integer.class));
		jmail.setUsername(env.getRequiredProperty("app.settings.smtp.username"));
		jmail.setPassword(env.getRequiredProperty("app.settings.smtp.password"));
		jmail.getJavaMailProperties().setProperty("mail.smtp.auth", env.getRequiredProperty("app.settings.smtp.auth"));
		jmail.getJavaMailProperties().setProperty("mail.smtp.starttls.enable",
				env.getRequiredProperty("app.settings.smtp.starttls"));
		return jmail;
	}

	/**
	 * Configures a handler for serving favicon requests.
	 *
	 * This handler uses the {@link ResourceHttpRequestHandler} class to handle requests for favicon resources.
	 * It sets the locations where the favicon resources are located and returns the configured request handler.
	 */
	@Bean
	protected ResourceHttpRequestHandler faviconRequestHandler() {
		final ResourceHttpRequestHandler requestHandler = new ResourceHttpRequestHandler();
		requestHandler
				.setLocations(Arrays.<Resource>asList(new ClassPathResource("classpath:/WEB-INF/static/images/")));
		return requestHandler;
	}

	/**
	 * This class is responsible for mapping URLs to handlers based on a URL map.
	 * It is used to handle requests for the favicon.ico file.
	 */
	@Bean
	public SimpleUrlHandlerMapping faviconHandlerMapping() {
		final SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
		mapping.setOrder(Integer.MIN_VALUE);
		mapping.setUrlMap(Collections.singletonMap("favicon.ico", faviconRequestHandler()));
		return mapping;
	}

	/**
	 * Creates a bean for registering the HttpSessionEventPublisher servlet listener.
	 * This listener publishes HttpSession events to the Spring application context.
	 * It is required for session management in a Spring Boot application.
	 *
	 * @return The ServletListenerRegistrationBean for the HttpSessionEventPublisher.
	 */
	@Bean
	public ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher() {
		return new ServletListenerRegistrationBean<>(new HttpSessionEventPublisher());
	}

	/**
	 * Interface that can be implemented by any object that needs to be initialized with a {@link ServletContext}.
	 * Typically used in the context of configuring a web application.
	 */
	@Bean
	public ServletContextInitializer initializer() {
		return servletContext -> {
			try (InputStream inputStream = servletContext.getResourceAsStream("/WEB-INF/web.xml")) {
				if (inputStream == null)
					throw new TrickException("error.web.xml.load", "Web XML cannot be load");

				final WebXml webXml = new WebXml();
				final WebXmlParser parser = new WebXmlParser(false, false, true);
				final boolean success = parser.parseWebXml(new InputSource(inputStream), webXml, false);
				if (success) {
					webXml.getContextParams().forEach(servletContext::setInitParameter);
					for (ServletDef def : webXml.getServlets().values()) {
						ServletRegistration.Dynamic reg = servletContext.addServlet(def.getServletName(),
								def.getServletClass());
						reg.setLoadOnStartup(1);
					}
					for (Map.Entry<String, String> mapping : webXml.getServletMappings().entrySet())
						servletContext.getServletRegistration(mapping.getValue()).addMapping(mapping.getKey());
				} else
					throw new TrickException("error.web.xml.parse", "Error parsing Web XML");
			} catch (Exception e) {
				TrickLogManager.persist(e);
			}
		};
	}

}
