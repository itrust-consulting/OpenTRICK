/**
 *
 */
package lu.itrust.boot.configuration;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.apache.tomcat.util.descriptor.web.ServletDef;
import org.apache.tomcat.util.descriptor.web.WebXml;
import org.apache.tomcat.util.descriptor.web.WebXmlParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;
import org.springframework.web.servlet.resource.ResourceUrlEncodingFilter;
import org.xml.sax.InputSource;

import jakarta.servlet.ServletRegistration;
import lu.itrust.business.ts.component.DataCleaner;
import lu.itrust.business.ts.component.TrickLogManager;
import lu.itrust.business.ts.database.service.ServiceStorage;
import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.helper.InstanceManager;
import lu.itrust.business.ts.usermanagement.listner.helper.PasswordEncryptionHelper;

/**
 * @author eomar
 *
 */

@Configuration
@EnableWebSecurity
public class AppConfig {

	@Bean
	public CommandLineRunner init(ServiceStorage serviceStorage,
			@Value("${app.settings.password.ecryption.key}") String password) {
		return (args) -> {
			serviceStorage.deleteAll();
			serviceStorage.init();
			PasswordEncryptionHelper.setPassword(password);
		};
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
	public DataCleaner dataCleaner() {
		return new DataCleaner();
	}

	@Bean
	public DataSource dataSource(Environment environment) {
		var dataSource = new BasicDataSource();
		dataSource.setDriverClassName(environment.getRequiredProperty("jdbc.driverClassName"));
		dataSource.setUrl(environment.getRequiredProperty("jdbc.databaseurl"));
		dataSource.setUsername(environment.getRequiredProperty("jdbc.username"));
		dataSource.setPassword(environment.getRequiredProperty("jdbc.password"));
		return dataSource;
	}

	@Bean
	public MessageSource messageSource() {
		var messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.addBasenames("classpath:/languages/messages");
		messageSource.setUseCodeAsDefaultMessage(true);
		messageSource.setFallbackToSystemLocale(false);
		messageSource.setDefaultEncoding("UTF-8");
		return messageSource;
	}

	@Bean
	public ResourceUrlEncodingFilter resourceUrlEncodingFilter() {
		return new ResourceUrlEncodingFilter();
	}

	@Bean
	public MultipartResolver multipartResolver() {
		return new StandardServletMultipartResolver();
	}

	@Bean
	public FreeMarkerConfigurationFactoryBean freemarkerConfiguration(
			@Value("${app.settings.email.template}") String path) {
		var bean = new FreeMarkerConfigurationFactoryBean();
		bean.setTemplateLoaderPath(path);
		return bean;
	}

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

	@Bean
	protected ResourceHttpRequestHandler faviconRequestHandler() {
		final ResourceHttpRequestHandler requestHandler = new ResourceHttpRequestHandler();
		requestHandler
				.setLocations(Arrays.<Resource>asList(new ClassPathResource("classpath:/WEB-INF/static/images/")));
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
	public ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher() {
		return new ServletListenerRegistrationBean<>(new HttpSessionEventPublisher());
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Bean
	public ServletContextInitializer initializer() {
		return servletContext -> {
			System.out.println(servletContext.getRealPath("/"));
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
				TrickLogManager.Persist(e);
			}
		};
	}

}
