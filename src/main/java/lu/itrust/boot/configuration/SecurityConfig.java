package lu.itrust.boot.configuration;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.CompositeSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionFixationProtectionStrategy;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.security.web.session.ConcurrentSessionFilter;
import org.springframework.security.web.session.SimpleRedirectSessionInformationExpiredStrategy;

import jakarta.servlet.Filter;
import lu.itrust.business.permissionevaluator.PermissionEvaluator;
import lu.itrust.business.ts.constants.Constant;
import lu.itrust.business.ts.usermanagement.helper.ApiAuthenticationEntryPoint;
import lu.itrust.business.ts.usermanagement.helper.ApiAuthenticationFilter;
import lu.itrust.business.ts.usermanagement.helper.ApiAuthenticationManager;
import lu.itrust.business.ts.usermanagement.helper.CustomAuthenticationFailureHandler;
import lu.itrust.business.ts.usermanagement.helper.CustomAuthenticationSuccessHandler;
import lu.itrust.business.ts.usermanagement.helper.CustomUsernamePasswordAuthenticationFilter;
import lu.itrust.business.ts.usermanagement.helper.OTPAuthenticationFilter;
import lu.itrust.business.ts.usermanagement.helper.OTPAuthenticationProcessingFilter;

@Configuration
public class SecurityConfig {

    private static final String API_IDS = "/Api/ids/**";

    @Autowired
    private @Lazy AuthenticationManager authenticationManager;

    @Autowired
    private Environment environment;

    ///
    // IDS Api Configuration
    ///
    @Bean
    @Order(1)
    public SecurityFilterChain apiIdsfilterChain(HttpSecurity http) throws Exception {
        http.securityMatchers(matchers -> matchers.requestMatchers(API_IDS))
                .authenticationManager(apiAuthenticationManager())
                .httpBasic(basic -> basic.authenticationEntryPoint(apiAuthenticationEntryPoint())
                        .realmName("TRICK Service application"))
                .authorizeHttpRequests(authz -> authz.anyRequest().hasAuthority(Constant.ROLE_IDS))
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .requiresChannel(ch -> ch.requestMatchers(API_IDS).requiresSecure()).csrf(csrf -> csrf.disable()).cors(e -> e.and())
                .addFilterAt(apiAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    ///
    // Api data configuration
    ///

    @Bean
    @Order(2)
    public SecurityFilterChain apiDatafilterChain(HttpSecurity http) throws Exception {
        http.securityMatchers(matchers -> matchers.requestMatchers("/Api/data/**"))
                .authorizeHttpRequests(authz -> authz.anyRequest().hasAnyAuthority(Constant.ROLE_USER,
                        Constant.ROLE_CONSULTANT, Constant.ROLE_ADMIN, Constant.ROLE_SUPERVISOR))
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .requiresChannel(ch -> ch.requestMatchers(API_IDS).requiresSecure()).csrf(csrf -> csrf.disable())
                .cors(e -> e.and())
                .httpBasic(basic -> basic
                        .realmName("TRICK Service application"))
                .authenticationManager(authenticationManager);
        return http.build();
    }

    @Bean
    @Order(3)
    public SecurityFilterChain webSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(
                e -> e.requestMatchers(
                        "/favicon.ico", "/css/**", "/fonts/**", "/js/**", "/images/**",
                        "/IsAuthenticate", "/Success/**", "/Error/**", "/Unlock-account/**", "/Login/**",
                        "/Signout/**",
                        "/ResetPassword/**", "/ChangePassword/**", "/Api/**" /**
                                                                              * This filterchain will not be used to
                                                                              * protect Api
                                                                              */
                        ,
                        "/Analysis-access-management/**", "/Validate/**")
                        .permitAll())
                .authorizeHttpRequests(e -> e
                        .requestMatchers("/DoRegister/**", "/Register/**")
                        .anonymous())

                .authorizeHttpRequests(
                        e -> e.requestMatchers("/OTP/**").hasAnyAuthority(Constant.ROLE_PRE_AUTHEN))
                .authorizeHttpRequests(
                        e -> e.requestMatchers("/", "/Home/**").hasAnyAuthority(Constant.ROLE_PRE_AUTHEN,
                                Constant.ROLE_USER,
                                Constant.ROLE_CONSULTANT,
                                Constant.ROLE_ADMIN,
                                Constant.ROLE_SUPERVISOR))
                .authorizeHttpRequests(
                        e -> e.requestMatchers("/Analysis/**", "/Account/**").hasAnyAuthority(
                                Constant.ROLE_USER,
                                Constant.ROLE_CONSULTANT,
                                Constant.ROLE_ADMIN,
                                Constant.ROLE_SUPERVISOR))
                .authorizeHttpRequests(
                        e -> e.requestMatchers("/KnowledgeBase/**").hasAnyAuthority(Constant.ROLE_CONSULTANT,
                                Constant.ROLE_ADMIN,
                                Constant.ROLE_SUPERVISOR))
                .authorizeHttpRequests(
                        e -> e.requestMatchers("/Admin/**").hasAnyAuthority(Constant.ROLE_ADMIN,
                                Constant.ROLE_SUPERVISOR))
                .authorizeHttpRequests(e -> e.anyRequest().authenticated())
                .exceptionHandling(e -> e.authenticationEntryPoint(loginUrlAuthenticationEntryPoint()))

                .addFilterAt(usernamePasswordAuthenticationFilter(),
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterAt(otpAuthenticationFilter(),
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterAt(otpAuthenticationProcessingFilter(),
                        UsernamePasswordAuthenticationFilter.class)

                .logout(e -> e.logoutUrl("/Signout").invalidateHttpSession(true).logoutSuccessUrl("/Home")
                        .deleteCookies("TS_SESSION_ID"))
                .authenticationManager(apiAuthenticationManager())
                .sessionManagement(e -> e.sessionFixation().migrateSession()
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));

        return http.build();

    }

    @Bean
    public Filter apiAuthenticationFilter() {
        return new ApiAuthenticationFilter(apiAuthenticationManager(), apiAuthenticationEntryPoint());
    }

    @Bean
    public ApiAuthenticationManager apiAuthenticationManager() {
        return new ApiAuthenticationManager();
    }

    @Bean
    public ApiAuthenticationEntryPoint apiAuthenticationEntryPoint() {
        return new ApiAuthenticationEntryPoint();
    }

    @Bean
    public DefaultWebSecurityExpressionHandler webexpressionHandler() {
        return new DefaultWebSecurityExpressionHandler();
    }

    @Bean
    public DefaultMethodSecurityExpressionHandler methodSecurityExpressionHandler(
            PermissionEvaluator permissionEvaluator) {
        var handler = new DefaultMethodSecurityExpressionHandler();
        handler.setPermissionEvaluator(permissionEvaluator);
        return handler;
    }

    @Bean
    public CustomAuthenticationFailureHandler authenticationFailureHandler() {
        return new CustomAuthenticationFailureHandler("/Login/Error");
    }

    @Bean
    public CustomAuthenticationSuccessHandler authenticationSuccessHandler() {
        return new CustomAuthenticationSuccessHandler("/Home");
    }

    @Bean
    public ConcurrentSessionFilter concurrencyFilter() {
        return new ConcurrentSessionFilter(sessionRegistry(), redirectSessionInformationExpiredStrategy());
    }

    @Bean
    public SimpleRedirectSessionInformationExpiredStrategy redirectSessionInformationExpiredStrategy() {
        return new SimpleRedirectSessionInformationExpiredStrategy("/Login?Error=25");
    }

    @Bean
    public HttpFirewall firewall() {
        return new StrictHttpFirewall();
    }

    @Bean
    public CustomUsernamePasswordAuthenticationFilter usernamePasswordAuthenticationFilter() {
        var filter = new CustomUsernamePasswordAuthenticationFilter("/Login");
        var isEnable2FA = environment.getRequiredProperty("app.settings.otp.enable", Boolean.class);
        filter.setEnable2FA(isEnable2FA);
        filter.setPostOnly(true);
        filter.setAuthenticationManager(authenticationManager);
        filter.setContinueChainBeforeSuccessfulAuthentication(isEnable2FA);
        filter.setSessionAuthenticationStrategy(sessionAuthenticationStrategy());
        filter.setAuthenticationFailureHandler(authenticationFailureHandler());
        filter.setAuthenticationSuccessHandler(authenticationSuccessHandler());
        filter.setSecurityContextRepository(new DelegatingSecurityContextRepository(
                new RequestAttributeSecurityContextRepository(),
                new HttpSessionSecurityContextRepository()));
        filter.setForce2FA(environment.getRequiredProperty("app.settings.otp.force", Boolean.class));
        return filter;

    }

    @Bean
    OTPAuthenticationFilter otpAuthenticationFilter() {
        return new OTPAuthenticationFilter("/Login", "/OTP");
    }

    @Bean
    OTPAuthenticationProcessingFilter otpAuthenticationProcessingFilter() {
        var filter = new OTPAuthenticationProcessingFilter("/OTP/Authorise");
        filter.setAuthenticationFailureHandler(authenticationFailureHandler());
        filter.setAuthenticationSuccessHandler(authenticationSuccessHandler());
        filter.setSecurityContextRepository(new DelegatingSecurityContextRepository(
                new RequestAttributeSecurityContextRepository(),
                new HttpSessionSecurityContextRepository()));
        return filter;
    }

    @Bean
    LoginUrlAuthenticationEntryPoint loginUrlAuthenticationEntryPoint() {
        return new LoginUrlAuthenticationEntryPoint("/Login");
    }

    @Bean
    CompositeSessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new CompositeSessionAuthenticationStrategy(Arrays.asList(new SessionFixationProtectionStrategy(),
                new RegisterSessionAuthenticationStrategy(sessionRegistry())));
    }

    @Bean
    SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}
