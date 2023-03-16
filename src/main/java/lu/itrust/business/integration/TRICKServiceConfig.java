/**
 *
 */
package lu.itrust.business.integration;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.ServletRegistration;

import org.apache.tomcat.util.descriptor.web.ServletDef;
import org.apache.tomcat.util.descriptor.web.WebXml;
import org.apache.tomcat.util.descriptor.web.WebXmlParser;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
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
public class TRICKServiceConfig {

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
	public ServletContextInitializer initializer() {
		return servletContext -> {
			try (InputStream inputStream = servletContext.getResourceAsStream("/WEB-INF/web.xml")) {
				if (inputStream == null)
					throw new RuntimeException("Web XML cannot be load");

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
					throw new RuntimeException("Error parsing Web XML");
			} catch (Exception e) {
				TrickLogManager.Persist(e);
			}
		};
	}

}
