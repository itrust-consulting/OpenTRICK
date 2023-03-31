package lu.itrust.business.integration;

import java.util.concurrent.TimeUnit;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.orm.hibernate5.support.OpenSessionInViewInterceptor;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.CssLinkResourceTransformer;
import org.springframework.web.servlet.resource.VersionResourceResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

@Configuration
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        var openSessionInViewInterceptor = new OpenSessionInViewInterceptor();
        openSessionInViewInterceptor.setSessionFactory(sessionFactory);
        registry.addWebRequestInterceptor(openSessionInViewInterceptor);
        WebMvcConfigurer.super.addInterceptors(registry);
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
        InternalResourceViewResolver bean = new InternalResourceViewResolver();
        bean.setViewClass(JstlView.class);
        bean.setPrefix("/WEB-INF/views/");
        bean.setViewNames("jsp/*");
        bean.setSuffix(".jsp");
        return bean;
    }

    @Bean
    public VersionResourceResolver versionResourceResolver() {
        return new VersionResourceResolver().addContentVersionStrategy("/**");
    }

}
