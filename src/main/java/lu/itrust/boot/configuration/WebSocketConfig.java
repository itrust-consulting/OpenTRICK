package lu.itrust.boot.configuration;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.messaging.context.AuthenticationPrincipalArgumentResolver;
import org.springframework.security.messaging.web.csrf.XorCsrfChannelInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

/**
 * Configuration class for WebSocket messaging.
 * This class implements the WebSocketMessageBrokerConfigurer interface
 * to configure the WebSocket message broker and endpoints.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("#{'${app.settings.trusted.proxy}'.split(',')}")
    private String[] trustedProxies;

    /**
     * Configure the message broker for WebSocket communication.
     *
     * @param registry the MessageBrokerRegistry to configure
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/Task", "/Notification");
        registry.setApplicationDestinationPrefixes("/Application");
        registry.setUserDestinationPrefix("/User");
    }

    /**
     * Registers STOMP endpoints with the specified StompEndpointRegistry.
     * 
     * @param registry the StompEndpointRegistry to register the endpoints with
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/Messaging/", "/Messaging")
                .setAllowedOriginPatterns(trustedProxies).withSockJS();
    }

    /**
     * Configures the WebSocket transport for the application.
     *
     * @param registry The WebSocket transport registration object.
     */
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        WebSocketMessageBrokerConfigurer.super.configureWebSocketTransport(registry);
        registry.setSendTimeLimit(15 * 1000).setSendBufferSizeLimit(512 * 1024);
    }

    /**
     * Adds custom argument resolvers to the list of argument resolvers.
     * These resolvers are used to resolve method arguments in WebSocket handlers.
     *
     * @param argumentResolvers the list of argument resolvers to add to
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new AuthenticationPrincipalArgumentResolver());
        WebSocketMessageBrokerConfigurer.super.addArgumentResolvers(argumentResolvers);
    }

    /**
     * A ChannelInterceptor is responsible for intercepting messages sent over a WebSocket channel.
     * It can be used to perform additional processing or apply custom logic before or after a message is sent or received.
     * Implementations of this interface should override the methods defined in the interface to provide the desired behavior.
     */
    @Bean
    public ChannelInterceptor csrfChannelInterceptor() {
        return new XorCsrfChannelInterceptor();
    }

}
