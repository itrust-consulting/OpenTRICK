package lu.itrust.boot.configuration;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.orm.hibernate5.support.OpenSessionInViewInterceptor;
import org.springframework.web.bind.annotation.RequestMethod;
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

@Configuration
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addWebRequestInterceptor(openSessionInViewInterceptor());
        registry.addInterceptor(localeChangeInterceptor());
        WebMvcConfigurer.super.addInterceptors(registry);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        WebMvcConfigurer.super.addCorsMappings(registry);
        registry.addMapping("/Api/**").allowCredentials(true).allowedMethods("GET","POST", "DELETE",
		"HEAD", "OPTIONS").allowedOriginPatterns("*");
    }


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

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        var interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang");
        return interceptor;
    }

    public OpenSessionInViewInterceptor openSessionInViewInterceptor() {
        var openSessionInViewInterceptor = new OpenSessionInViewInterceptor();
        openSessionInViewInterceptor.setSessionFactory(sessionFactory);
        return openSessionInViewInterceptor;
    }

    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver slr = new SessionLocaleResolver();
        slr.setDefaultLocale(Locale.ENGLISH);
        slr.setLocaleAttributeName("session.current.locale");
        slr.setTimeZoneAttributeName("session.current.timezone");
        return slr;
    }

    @Bean
    public VersionResourceResolver versionResourceResolver() {
        return new VersionResourceResolver().addContentVersionStrategy("/**");
    }

}
