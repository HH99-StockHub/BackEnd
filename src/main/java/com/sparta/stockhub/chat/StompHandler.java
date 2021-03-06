package com.sparta.stockhub.chat;

import com.sparta.stockhub.security.jwt.HeaderTokenExtractor;
import com.sparta.stockhub.security.jwt.JwtDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;

@Configuration
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {
    private final HeaderTokenExtractor headerTokenExtractor;
    private final JwtDecoder jwtDecoder;

    // websocket을 통해 들어온 요청이 처리 되기전 실행된다.
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (StompCommand.CONNECT == accessor.getCommand()) {
           // jwtDecoder.decodeUsername(headerTokenExtractor.extract(accessor.getFirstNativeHeader("token"))); // FE token 삭제 요청으로 수정
        }
        return message;
    }
}
