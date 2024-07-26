package com.example.cc.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocket
@EnableWebSocketMessageBroker // 메시지 브로커가 지원하는 websocket 메시지 처리를 활성화시킴
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 메시지 구독 경로, 브로커->구독자 로 보냄(메시지를 받음)
        config.enableSimpleBroker("/sub");
        // 메시지 발행 경로(@MessageMapping 어노테이션을 향함), 발행자->브로커 로 보냄(메시지를 보냄)
        config.setApplicationDestinationPrefixes("/pub");
    }

    // 소켓 통신을 할 때 사용되는 endpoint 설정
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Client에서 websocket에 연결할 때 사용할 API 경로 설정 - 일반 채팅
        registry.addEndpoint("/stomp/chat")
                //.setAllowedOriginPatterns("*") // 또는 구체적인 도메인
                // http://localhost:8090/stomp/chat 으로 url 연결
                //보안상의 이유로.setAllowedOrigins("*") 대신 경로를 직접 지정해줌
                .setAllowedOriginPatterns("http://localhost:8090")
                .withSockJS();

        // Client에서 websocket에 연결할 때 사용할 API 경로 설정 - 매칭
        registry.addEndpoint("/stomp/match")
                //.setAllowedOriginPatterns("*") // 또는 구체적인 도메인
                .setAllowedOriginPatterns("http://localhost:8090")
                .withSockJS();
    }
}
