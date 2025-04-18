package lu.itrust.boot.configuration;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.orm.hibernate5.support.OpenSessionInViewInterceptor;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.resource.CssLinkResourceTransformer;
import org.springframework.web.servlet.resource.VersionResourceResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

/**
 * Configuration class for Web MVC.
 * This class implements the WebMvcConfigurer interface to customize the behavior of the Spring MVC framework.
 */
@Configuration
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private SessionFactory sessionFactory;

    /**
     * Adds interceptors to the interceptor registry.
     *
     * @param registry the interceptor registry to add the interceptors to
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addWebRequestInterceptor(openSessionInViewInterceptor());
        registry.addInterceptor(localeChangeInterceptor());
        WebMvcConfigurer.super.addInterceptors(registry);
    }

    /**
     * Configures Cross-Origin Resource Sharing (CORS) mappings for the application.
     * Allows cross-origin requests from specified origins and methods.
     *
     * @param registry the CorsRegistry object used to configure CORS mappings
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        WebMvcConfigurer.super.addCorsMappings(registry);
        registry.addMapping("/Api/**").allowCredentials(true).allowedMethods("GET","POST", "DELETE",
		"HEAD", "OPTIONS").allowedOriginPatterns("*");
    }


    /**
     * Configures the resource handlers for serving static resources.
     *
     * @param registry the resource handler registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/static/**")
                .addResourceLocations("/WEB-INF/static/views/")
                .setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS))
                .resourceChain(true).addResolver(versionResourceResolver())
                .addTransformer(new CssLinkResourceTransformer());
        registry
                .addResourceHandler("/images/**")
                .addResourceLocations("/WEB-INF/static/images/")
                .setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS)).resourceChain(true)
                .addResolver(versionResourceResolver());

        registry
                .addResourceHandler("/fonts/**")
                .addResourceLocations("/WEB-INF/static/fonts/")
                .setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS))
                .resourceChain(true).addResolver(versionResourceResolver());

        registry
                .addResourceHandler("/css/**")
                .addResourceLocations("/WEB-INF/static/css/")
                .setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS))
                .resourceChain(true).addResolver(versionResourceResolver())
                .addTransformer(new CssLinkResourceTransformer());

        registry
                .addResourceHandler("/js/**")
                .addResourceLocations("/WEB-INF/static/js/")
                .setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS))
                .resourceChain(true).addResolver(versionResourceResolver());

    }

    /**
     * Creates and configures a ViewResolver bean for resolving views in the application.
     * The ViewResolver is responsible for mapping view names to actual view implementations.
     * This method specifically creates an instance of UrlBasedViewResolver and configures it
     * to resolve JSP views located in the "/WEB-INF/views/" directory with a ".jsp" suffix.
     * It also sets the view names and prefixes for forwarding and redirecting URLs.
     *
     * @return The configured ViewResolver bean.
     */
    @Bean
    public ViewResolver internalResourceViewResolver() {
        UrlBasedViewResolver bean = new InternalResourceViewResolver();
        bean.setViewClass(JstlView.class);
        bean.setPrefix("/WEB-INF/views/");
        bean.setViewNames("jsp/*", UrlBasedViewResolver.FORWARD_URL_PREFIX + "*",
                UrlBasedViewResolver.REDIRECT_URL_PREFIX + "*");
        bean.setSuffix(".jsp");
        return bean;
    }

    /**
     * Creates a new instance of the LocaleChangeInterceptor class.
     * The LocaleChangeInterceptor class is used to intercept requests and change the locale based on a request parameter.
     * The parameter name used to determine the new locale value is set using the setParamName method.
     * @return The LocaleChangeInterceptor instance.
     */
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        var interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang");
        interceptor.setIgnoreInvalidLocale(true);
        return interceptor;
    }

    /**
     * Interceptor that binds a Hibernate Session to the thread for the entire processing of the request.
     * This allows for lazy loading of persistent objects in web views, even after the session has been closed.
     */
    public OpenSessionInViewInterceptor openSessionInViewInterceptor() {
        var openSessionInViewInterceptor = new OpenSessionInViewInterceptor();
        openSessionInViewInterceptor.setSessionFactory(sessionFactory);
        return openSessionInViewInterceptor;
    }

    /**
     * Creates and configures a LocaleResolver bean.
     * The LocaleResolver is responsible for resolving the user's locale.
     * This method creates a SessionLocaleResolver and sets the default locale to English.
     * It also sets the attribute names for the locale and timezone in the session.
     *
     * @return the configured LocaleResolver bean
     */
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver slr = new SessionLocaleResolver();
        slr.setDefaultLocale(Locale.ENGLISH);
        slr.setLocaleAttributeName("session.current.locale");
        slr.setTimeZoneAttributeName("session.current.timezone");
        return slr;
    }

    /**
     * A resource resolver that adds a version to the resource URLs.
     * This allows for cache busting and ensures that clients always receive the latest version of the resource.
     */
    @Bean
    public VersionResourceResolver versionResourceResolver() {
        return new VersionResourceResolver().addContentVersionStrategy("/**");
    }

}
