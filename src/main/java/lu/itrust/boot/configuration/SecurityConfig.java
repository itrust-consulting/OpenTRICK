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
import org.springframework.security.web.authentication.session.SessionFixationProtectionStrategy;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
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
import lu.itrust.business.ts.usermanagement.helper.CustomUsernamePasswordAuthenticationFilter;
import lu.itrust.business.ts.usermanagement.helper.OTPAuthenticationFilter;
import lu.itrust.business.ts.usermanagement.helper.OTPAuthenticationProcessingFilter;

@EnableMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
@Configuration
public class SecurityConfig {

        private static final String API_IDS = "/Api/ids/**";

        @Autowired
        private @Lazy AuthenticationManager authenticationManager;

        @Autowired
        private Environment environment;

        @Bean
        MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
                return new MvcRequestMatcher.Builder(introspector);
        }

        ///
        // IDS Api Configuration
        ///
        @Bean
        @Order(1)
        public SecurityFilterChain apiIdsfilterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc)
                        throws Exception {
                http.securityMatchers(matchers -> matchers.requestMatchers(mvc.pattern(API_IDS)))
                                .authenticationManager(apiAuthenticationManager())
                                .httpBasic(basic -> basic.authenticationEntryPoint(apiAuthenticationEntryPoint())
                                                .realmName("TRICK Service application"))
                                .authorizeHttpRequests(authz -> authz.anyRequest().hasAuthority(Constant.ROLE_IDS))
                                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .requiresChannel(ch -> ch.requestMatchers(mvc.pattern(API_IDS)).requiresSecure())
                                .csrf(csrf -> csrf.disable())
                                .cors(Customizer.withDefaults())
                                .addFilterAt(apiAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
                return http.build();
        }

        ///
        // Api data configuration
        ///

        @Bean
        @Order(2)
        public SecurityFilterChain apiDatafilterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc)
                        throws Exception {
                http.securityMatchers(matchers -> matchers.requestMatchers(mvc.pattern("/Api/data/**")))
                                .authorizeHttpRequests(authz -> authz.anyRequest().hasAnyAuthority(Constant.ROLE_USER,
                                                Constant.ROLE_CONSULTANT, Constant.ROLE_ADMIN,
                                                Constant.ROLE_SUPERVISOR))
                                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .requiresChannel(ch -> ch.requestMatchers(mvc.pattern(API_IDS)).requiresSecure())
                                .csrf(csrf -> csrf.disable())
                                .cors(Customizer.withDefaults())
                                .httpBasic(basic -> basic
                                                .realmName("TRICK Service application"))
                                .authenticationManager(authenticationManager);
                return http.build();
        }

        @Bean
        @Order(3)
        public SecurityFilterChain webSecurityFilterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc)
                        throws Exception {
                http.securityContext(s -> s.securityContextRepository(delegatingSecurityContextRepository()))
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
                                                                mvc.pattern("/Api/**") /**
                                                                                        * This filterchain
                                                                                        * will not be used
                                                                                        * to
                                                                                        * protect Api
                                                                                        */
                                                                ,
                                                                mvc.pattern("/Analysis-access-management/**"),
                                                                mvc.pattern("/Validate/**"))
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

        @Bean
        public Filter apiAuthenticationFilter() {
                return new ApiAuthenticationFilter(apiAuthenticationManager(), apiAuthenticationEntryPoint());
        }

        @Bean
        @Order(99)
        public ApiAuthenticationManager apiAuthenticationManager() {
                return new ApiAuthenticationManager();
        }

        @Bean
        public ApiAuthenticationEntryPoint apiAuthenticationEntryPoint() {
                return new ApiAuthenticationEntryPoint();
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

        @Bean
        OTPAuthenticationFilter otpAuthenticationFilter() {
                return new OTPAuthenticationFilter("/Signin", "/OTP");
        }

        @Bean
        OTPAuthenticationProcessingFilter otpAuthenticationProcessingFilter() {
                var filter = new OTPAuthenticationProcessingFilter("/OTP/Authorise");
                filter.setAuthenticationFailureHandler(authenticationFailureHandler());
                filter.setAuthenticationSuccessHandler(authenticationSuccessHandler());
                filter.setSecurityContextRepository(delegatingSecurityContextRepository());
                filter.afterPropertiesSet();
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

        @Bean
        DelegatingSecurityContextRepository delegatingSecurityContextRepository() {
                return new DelegatingSecurityContextRepository(
                                new RequestAttributeSecurityContextRepository(),
                                new HttpSessionSecurityContextRepository());
        }

}
