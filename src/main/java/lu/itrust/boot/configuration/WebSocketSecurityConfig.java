package lu.itrust.boot.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;

import lu.itrust.business.ts.constants.Constant;

/**
 * Configuration class for WebSocket security.
 */
@Configuration
@EnableWebSocketSecurity
public class WebSocketSecurityConfig {

        /**
         * Creates an AuthorizationManager for WebSocket messages.
         *
         * @param messages The builder for configuring message matchers and authorities.
         * @return The AuthorizationManager for WebSocket messages.
         */
        @Bean
        public AuthorizationManager<Message<?>> messageAuthorizationManager(
                        MessageMatcherDelegatingAuthorizationManager.Builder messages) {
                messages
                                .nullDestMatcher().authenticated()
                                .simpDestMatchers("/User/**", "/Application/**", "/Notification/**")
                                .hasAnyAuthority(Constant.ROLE_USER,
                                                Constant.ROLE_CONSULTANT,
                                                Constant.ROLE_ADMIN,
                                                Constant.ROLE_SUPERVISOR)
                                .simpTypeMatchers(SimpMessageType.MESSAGE, SimpMessageType.SUBSCRIBE).denyAll()
                                .anyMessage().denyAll();

                return messages.build();
        }

}
