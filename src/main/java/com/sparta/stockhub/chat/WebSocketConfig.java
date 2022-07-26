package com.sparta.stockhub.chat;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@RequiredArgsConstructor
@EnableWebSocketMessageBroker //Stomp를 사용하기위해 선언하는 어노테이션
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer { // 메시지를 중개 / 라우팅하는 브로커 설정.

    private final StompHandler stompHandler;

    //어플리케이션 내부에서 사용할  path를 지정할 수 있다.
    @Override // Spring docs에서는 /topic, /queue로 나오나 편의상 /pub, /sub로 변경
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/sub"); // prefix /sub 로 수신 메시지 구분   enableSimpleBroker: 해당 경로로 SimpleBroker를 등록. SimpleBroker는 해당하는 경로를 SUBSCRIBE하는 Client에게 메세지를 전달하는 간단한 작업을 수행
        config.setApplicationDestinationPrefixes("/pub"); // prefix /pub 로 발행 요청         setApplicationDestinationPrefixes: Client에서 SEND 요청을 처리 / 클라이언트가 서버로 메세지를 보낼때 붙여야 하는 url prefix

    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/stomp") // 서버와 클라이언트가 webSocket 통신을 하기 위한 엔드포인트
                .setAllowedOriginPatterns("*")  //"*://*"
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompHandler);
    }
}
