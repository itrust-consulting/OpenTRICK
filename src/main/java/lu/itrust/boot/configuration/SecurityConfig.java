package lu.itrust.boot.configuration;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.CompositeSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionFixationProtectionStrategy;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.session.ConcurrentSessionFilter;
import org.springframework.security.web.session.SimpleRedirectSessionInformationExpiredStrategy;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import jakarta.servlet.Filter;
import lu.itrust.business.permissionevaluator.PermissionEvaluator;
import lu.itrust.business.ts.constants.Constant;
import lu.itrust.business.ts.usermanagement.helper.ApiAuthenticationEntryPoint;
import lu.itrust.business.ts.usermanagement.helper.ApiAuthenticationFilter;
import lu.itrust.business.ts.usermanagement.helper.ApiAuthenticationManager;
import lu.itrust.business.ts.usermanagement.helper.CustomAuthenticationFailureHandler;
import lu.itrust.business.ts.usermanagement.helper.CustomAuthenticationSuccessHandler;
import lu.itrust.business.ts.usermanagement.helper.CustomRequestCache;
import lu.itrust.business.ts.usermanagement.helper.CustomUsernamePasswordAuthenticationFilter;
import lu.itrust.business.ts.usermanagement.helper.OTPAuthenticationFilter;
import lu.itrust.business.ts.usermanagement.helper.OTPAuthenticationProcessingFilter;

/**
 * This class represents the configuration for the security of the application.
 * It provides the necessary security filters and rules for different API endpoints.
 */
/**
 * Configuration class for security settings.
 */
@EnableMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
@Configuration
public class SecurityConfig {

        private static final String API_DATA = "/Api/data/**";

        private static final String API_IDS = "/Api/ids/**";

        @Autowired
        private @Lazy AuthenticationManager authenticationManager;

        @Autowired
        private Environment environment;

        @Bean
        MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
                return new MvcRequestMatcher.Builder(introspector);
        }

        /**
         * IDS Api Configuration
         * Creates a security filter chain for API IDs.
         *
         * @param http The HttpSecurity object to configure the security.
         * @param mvc  The MvcRequestMatcher.Builder object to build the request
         *             matcher.
         * @return The configured SecurityFilterChain object.
         * @throws Exception if an error occurs during configuration.
         */
        @Bean
        @Order(1)
        public SecurityFilterChain apiIdsfilterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc)
                        throws Exception {
                http.securityMatchers(matchers -> matchers.requestMatchers(mvc.pattern(API_IDS)))
                                .authenticationManager(apiAuthenticationManager())
                                .httpBasic(basic -> basic.authenticationEntryPoint(apiAuthenticationEntryPoint())
                                                .realmName("Open Trick application"))
                                .authorizeHttpRequests(authz -> authz.anyRequest().hasAuthority(Constant.ROLE_IDS))
                                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .requiresChannel(ch -> ch.requestMatchers(mvc.pattern(API_IDS)).requiresSecure())
                                .csrf(csrf -> csrf.disable())
                                .cors(Customizer.withDefaults())
                                .addFilterAt(apiAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
                return http.build();
        }

        /**
         * Api data configuration
         * Configures the security filter chain for API data requests.
         * 
         * @param http The HttpSecurity object used for configuring security.
         * @param mvc  The MvcRequestMatcher.Builder object used for matching MVC
         *             requests.
         * @return The configured SecurityFilterChain object.
         * @throws Exception If an error occurs during configuration.
         */
        @Bean
        @Order(2)
        public SecurityFilterChain apiDatafilterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc)
                        throws Exception {
                http.securityMatchers(matchers -> matchers.requestMatchers(mvc.pattern(API_DATA)))
                                .authorizeHttpRequests(authz -> authz.anyRequest().hasAnyAuthority(Constant.ROLE_USER,
                                                Constant.ROLE_CONSULTANT, Constant.ROLE_ADMIN,
                                                Constant.ROLE_SUPERVISOR))
                                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .requiresChannel(ch -> ch.requestMatchers(mvc.pattern(API_DATA)).requiresSecure())
                                .csrf(csrf -> csrf.disable())
                                .cors(Customizer.withDefaults())
                                .httpBasic(Customizer.withDefaults())
                                .authenticationManager(authenticationManager);
                return http.build();
        }

        /**
         * Configures the security filter chain for web requests.
         *
         * @param http The HttpSecurity object used for configuring security settings.
         * @param mvc  The MvcRequestMatcher.Builder object used for matching request
         *             patterns.
         * @return The configured SecurityFilterChain object.
         * @throws Exception if an error occurs during configuration.
         */
        @Bean
        @Order(3)
        public SecurityFilterChain webSecurityFilterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc)
                        throws Exception {
                http.securityContext(s -> s.securityContextRepository(delegatingSecurityContextRepository()))
                                .csrf(e -> e.ignoringRequestMatchers(mvc.pattern("/Messaging/**")))
                                .headers(e -> e.frameOptions(f -> f.sameOrigin()))
                                .requestCache(cache -> cache.requestCache(customRequestCache()))
                                .authorizeHttpRequests(
                                                e -> e.requestMatchers(
                                                                mvc.pattern("/favicon.ico"), mvc.pattern("/css/**"),
                                                                mvc.pattern("/fonts/**"), mvc.pattern("/js/**"),
                                                                mvc.pattern("/images/**"),
                                                                mvc.pattern("/IsAuthenticate"),
                                                                mvc.pattern("/Success/**"),
                                                                mvc.pattern("/Error/**"),
                                                                mvc.pattern("/Unlock-account/**"),
                                                                mvc.pattern("/Login/**"),
                                                                mvc.pattern("/Signout/**"), mvc.pattern("/Signin/**"),
                                                                mvc.pattern("/ResetPassword/**"),
                                                                mvc.pattern("/ChangePassword/**"),
                                                                mvc.pattern("/Analysis-access-management/**"),
                                                                mvc.pattern("/Validate/**"),
                                                                mvc.pattern("/Api/**"), // Disable for API
                                                                mvc.pattern("/WEB-INF/views/jsp/errors/**"))// Allow
                                                                                                            // errors
                                                                                                            // for
                                                                                                            // anonymous,
                                                                                                            // The 401
                                                                                                            // error is
                                                                                                            // useful
                                                                                                            // for API.
                                                                .permitAll())
                                .authorizeHttpRequests(e -> e
                                                .requestMatchers(mvc.pattern("/DoRegister/**"),
                                                                mvc.pattern("/Register/**"))
                                                .anonymous())

                                .authorizeHttpRequests(
                                                e -> e.requestMatchers(mvc.pattern("/OTP/**"))
                                                                .hasAnyAuthority(Constant.ROLE_PRE_AUTHEN))
                                .authorizeHttpRequests(
                                                e -> e.requestMatchers(mvc.pattern("/"), mvc.pattern("/Home/**"))
                                                                .hasAnyAuthority(
                                                                                Constant.ROLE_PRE_AUTHEN,
                                                                                Constant.ROLE_USER,
                                                                                Constant.ROLE_CONSULTANT,
                                                                                Constant.ROLE_ADMIN,
                                                                                Constant.ROLE_SUPERVISOR))
                                .authorizeHttpRequests(
                                                e -> e.requestMatchers(mvc.pattern("/Analysis/**"),
                                                                mvc.pattern("/Account/**")).hasAnyAuthority(
                                                                                Constant.ROLE_USER,
                                                                                Constant.ROLE_CONSULTANT,
                                                                                Constant.ROLE_ADMIN,
                                                                                Constant.ROLE_SUPERVISOR))
                                .authorizeHttpRequests(
                                                e -> e.requestMatchers(mvc.pattern("/KnowledgeBase/**"))
                                                                .hasAnyAuthority(
                                                                                Constant.ROLE_CONSULTANT,
                                                                                Constant.ROLE_ADMIN,
                                                                                Constant.ROLE_SUPERVISOR))
                                .authorizeHttpRequests(
                                                e -> e.requestMatchers(mvc.pattern("/Admin/**")).hasAnyAuthority(
                                                                Constant.ROLE_ADMIN,
                                                                Constant.ROLE_SUPERVISOR))
                                .authorizeHttpRequests(
                                                e -> e.requestMatchers(mvc.pattern("/Patch/**"))
                                                                .hasAuthority(Constant.ROLE_SUPERVISOR))
                                .authorizeHttpRequests(e -> e.anyRequest().authenticated())
                                .exceptionHandling(e -> e.authenticationEntryPoint(loginUrlAuthenticationEntryPoint()))

                                .addFilterAt(usernamePasswordAuthenticationFilter(),
                                                UsernamePasswordAuthenticationFilter.class)
                                .addFilterAt(otpAuthenticationFilter(),
                                                UsernamePasswordAuthenticationFilter.class)
                                .addFilterAt(otpAuthenticationProcessingFilter(),
                                                UsernamePasswordAuthenticationFilter.class)

                                .logout(e -> e.logoutUrl("/Signout").invalidateHttpSession(true)
                                                .logoutSuccessUrl("/Home")
                                                .deleteCookies(environment.getProperty(
                                                                "server.servlet.session.cookie.name", "TS_SESSION_ID")))
                                .authenticationManager(authenticationManager)
                                .sessionManagement(e -> e.sessionFixation().migrateSession()
                                                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));

                return http.build();

        }

        /**
         * This method creates and configures an instance of the Filter class.
         * The Filter class is responsible for handling API authentication.
         *
         * @return An instance of the Filter class.
         */
        @Bean
        public Filter apiAuthenticationFilter() {
                return new ApiAuthenticationFilter(apiAuthenticationManager(), apiAuthenticationEntryPoint());
        }

        /**
         * Creates and configures the API authentication manager.
         * 
         * @return The configured {@link ApiAuthenticationManager}.
         */
        @Bean
        @Order(99)
        public ApiAuthenticationManager apiAuthenticationManager() {
                return new ApiAuthenticationManager();
        }

        /**
         * This class represents the entry point for API authentication.
         * It is responsible for handling authentication errors and returning
         * appropriate responses.
         */
        @Bean
        public ApiAuthenticationEntryPoint apiAuthenticationEntryPoint() {
                return new ApiAuthenticationEntryPoint();
        }

        /**
         * A default implementation of the {@link MethodSecurityExpressionHandler}
         * interface.
         * This class provides the necessary methods to evaluate method-level security
         * expressions.
         * It uses a {@link PermissionEvaluator} to evaluate permissions for the
         * expressions.
         */
        // @Bean
        public DefaultMethodSecurityExpressionHandler methodSecurityExpressionHandler() {
                return new DefaultMethodSecurityExpressionHandler();
                // handler.setPermissionEvaluator(permissionEvaluator);
                // return handler;
        }

        /**
         * This class represents a custom authentication failure handler.
         * It is responsible for handling authentication failures and redirecting the
         * user to the specified error page.
         */
        @Bean
        public CustomAuthenticationFailureHandler authenticationFailureHandler() {
                return new CustomAuthenticationFailureHandler("/Login/Error");
        }

        /**
         * This class represents a custom authentication success handler.
         * It is responsible for handling successful authentication events.
         */
        @Bean
        public CustomAuthenticationSuccessHandler authenticationSuccessHandler() {
                return new CustomAuthenticationSuccessHandler("/Home");
        }

        /**
         * A filter that handles concurrent session control for Spring Security.
         * This filter is responsible for checking if a user's session is already active
         * and if the maximum number of sessions has been reached.
         * If the maximum number of sessions has been reached, the filter will
         * invalidate the oldest session and allow the new session to proceed.
         * This filter is typically used in combination with session management
         * configuration in Spring Security.
         *
         * @return The ConcurrentSessionFilter instance.
         */
        @Bean
        public ConcurrentSessionFilter concurrencyFilter() {
                return new ConcurrentSessionFilter(sessionRegistry(), redirectSessionInformationExpiredStrategy());
        }

        /**
         * A strategy that redirects the user to a specified URL when their session
         * information has expired.
         * The URL can be customized by providing it as a parameter to the constructor.
         */
        @Bean
        public SimpleRedirectSessionInformationExpiredStrategy redirectSessionInformationExpiredStrategy() {
                return new SimpleRedirectSessionInformationExpiredStrategy("/Login?Error=25");
        }

        /**
         * This class represents a custom implementation of the
         * UsernamePasswordAuthenticationFilter.
         * It extends the UsernamePasswordAuthenticationFilter class and provides
         * additional functionality.
         * 
         * This filter is responsible for handling the authentication process for
         * username and password-based authentication.
         * It is used to authenticate users based on their provided username and
         * password credentials.
         * 
         * The filter can be configured to enable or disable two-factor authentication
         * (2FA) based on the application settings.
         * It can also be configured to force the use of 2FA for all users.
         * 
         * This filter is typically used in the SecurityConfig class to configure the
         * authentication process.
         * 
         * @see org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
         */
        @Bean
        public CustomUsernamePasswordAuthenticationFilter usernamePasswordAuthenticationFilter() {
                var filter = new CustomUsernamePasswordAuthenticationFilter("/Signin");
                var isEnable2FA = environment.getRequiredProperty("app.settings.otp.enable", Boolean.class);
                filter.setEnable2FA(isEnable2FA);
                filter.setPostOnly(true);
                filter.setAuthenticationManager(authenticationManager);
                filter.setContinueChainBeforeSuccessfulAuthentication(isEnable2FA);
                filter.setSessionAuthenticationStrategy(sessionAuthenticationStrategy());
                filter.setAuthenticationFailureHandler(authenticationFailureHandler());
                filter.setAuthenticationSuccessHandler(authenticationSuccessHandler());
                filter.setSecurityContextRepository(delegatingSecurityContextRepository());
                filter.setForce2FA(environment.getRequiredProperty("app.settings.otp.force", Boolean.class));
                filter.afterPropertiesSet();
                return filter;

        }

        /**
         * Creates an instance of the OTPAuthenticationFilter.
         * 
         * @return The OTPAuthenticationFilter instance.
         */
        @Bean
        OTPAuthenticationFilter otpAuthenticationFilter() {
                return new OTPAuthenticationFilter("/Signin", "/OTP");
        }

        /**
         * This class represents a filter used for OTP (One-Time Password)
         * authentication processing.
         * It is responsible for handling the authentication process for OTP requests.
         * The filter sets the authentication failure handler, authentication success
         * handler,
         * security context repository, and initializes the filter properties.
         *
         * @param path The path for OTP authorization requests.
         * @return An instance of the OTPAuthenticationProcessingFilter.
         */
        @Bean
        OTPAuthenticationProcessingFilter otpAuthenticationProcessingFilter() {
                var filter = new OTPAuthenticationProcessingFilter("/OTP/Authorise");
                filter.setAuthenticationFailureHandler(authenticationFailureHandler());
                filter.setAuthenticationSuccessHandler(authenticationSuccessHandler());
                filter.setSecurityContextRepository(delegatingSecurityContextRepository());
                filter.afterPropertiesSet();
                return filter;
        }

        /**
         * Creates a bean for the LoginUrlAuthenticationEntryPoint.
         * This bean is responsible for redirecting unauthenticated users to the login
         * page.
         *
         * @return The LoginUrlAuthenticationEntryPoint bean.
         */
        @Bean
        LoginUrlAuthenticationEntryPoint loginUrlAuthenticationEntryPoint() {
                return new LoginUrlAuthenticationEntryPoint("/Login");
        }

        /**
         * A composite implementation of the {@link SessionAuthenticationStrategy}
         * interface.
         * This class allows multiple session authentication strategies to be combined
         * into a single strategy.
         * When the `onAuthentication` method is called, each strategy in the composite
         * is invoked in the order they were added.
         * This allows for a flexible and customizable session authentication strategy.
         */
        @Bean
        CompositeSessionAuthenticationStrategy sessionAuthenticationStrategy() {
                return new CompositeSessionAuthenticationStrategy(Arrays.asList(new SessionFixationProtectionStrategy(),
                                new RegisterSessionAuthenticationStrategy(sessionRegistry())));
        }

        /**
         * Creates a bean for managing session registries.
         *
         * @return the SessionRegistry bean
         */
        @Bean
        SessionRegistry sessionRegistry() {
                return new SessionRegistryImpl();
        }

        /**
         * Creates and returns a PasswordEncoder instance.
         * The PasswordEncoder is responsible for encoding passwords and verifying
         * encoded passwords.
         *
         * @return the PasswordEncoder instance
         */
        @Bean
        public PasswordEncoder passwordEncoder() {
                return PasswordEncoderFactories.createDelegatingPasswordEncoder();
        }

        /**
         * A security context repository that delegates to multiple other security
         * context repositories.
         * This class allows you to chain multiple security context repositories
         * together, so that each repository can handle a specific aspect of security
         * context management.
         * When a security context is requested, the
         * `DelegatingSecurityContextRepository` will iterate over the configured
         * repositories and delegate the request to the first repository that can
         * provide a security context.
         * If none of the repositories can provide a security context, a new empty
         * security context will be created.
         * This class is typically used in conjunction with other security context
         * repositories to provide additional functionality or customization.
         */
        @Bean
        DelegatingSecurityContextRepository delegatingSecurityContextRepository() {
                return new DelegatingSecurityContextRepository(
                                new RequestAttributeSecurityContextRepository(),
                                new HttpSessionSecurityContextRepository());
        }

        @Bean
        public RequestCache customRequestCache() {
                return new CustomRequestCache();
        }

}
