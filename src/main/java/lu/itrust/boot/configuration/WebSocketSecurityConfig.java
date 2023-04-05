package lu.itrust.boot.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;

import lu.itrust.business.ts.constants.Constant;

@Configuration
@EnableWebSocketSecurity
public class WebSocketSecurityConfig {

    @Bean
    public AuthorizationManager<Message<?>> messageAuthorizationManager(
            MessageMatcherDelegatingAuthorizationManager.Builder messages) {
        messages.simpTypeMatchers(SimpMessageType.CONNECT, SimpMessageType.CONNECT_ACK,
                SimpMessageType.DISCONNECT,
                SimpMessageType.DISCONNECT_ACK, SimpMessageType.UNSUBSCRIBE)
                .permitAll()
                .simpDestMatchers("/User/**", "/Application/**").hasAnyAuthority(Constant.ROLE_USER,
                        Constant.ROLE_CONSULTANT,
                        Constant.ROLE_ADMIN,
                        Constant.ROLE_SUPERVISOR)
                .simpTypeMatchers(SimpMessageType.MESSAGE, SimpMessageType.SUBSCRIBE)
                .hasAnyAuthority(Constant.ROLE_USER,
                        Constant.ROLE_CONSULTANT,
                        Constant.ROLE_ADMIN,
                        Constant.ROLE_SUPERVISOR)
                .anyMessage().denyAll();

        return messages.build();
    }

}
