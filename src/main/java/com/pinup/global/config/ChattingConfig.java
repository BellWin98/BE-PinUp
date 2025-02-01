package com.pinup.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class ChattingConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        // stomp 접속 주소 URL - ws://localhost:8080/chat
        registry.addEndpoint("/chat")
                .setAllowedOrigins("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        // 메시지를 구독(수신)하는 요청 엔드포인트
        registry.enableSimpleBroker("/topic");

        // 메시지를 발행(송신)하는 엔드포인트
        registry.setApplicationDestinationPrefixes("/app");
    }
}
