package com.sparta.stockhub.chat.utils;

import com.sparta.stockhub.chat.domain.ChatMessage;
import com.sparta.stockhub.chat.service.ChatMessageService;
import com.sparta.stockhub.chat.service.ChatRoomService;
import com.sparta.stockhub.security.jwt.JwtAuthProvider;
import com.sparta.stockhub.security.jwt.JwtDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class StompHandler implements ChannelInterceptor { //재수정

    private final JwtDecoder jwtDecoder;
    private final ChatRoomService chatRoomService;
    private final JwtAuthProvider jwtAuthProvider;
    private final ChatMessageService chatService;

    @Override   // websocket을 통해 들어온 요청이 처리 되기 전 실행된다.
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        // websocket 연결시 헤더의 jwt token 검증
        if (StompCommand.CONNECT == accessor.getCommand()) {
            String jwtToken = accessor.getFirstNativeHeader("token");
            log.info("CONNECT {}" , jwtToken);
            System.out.println("StompHandler token = " + jwtToken);
            // Header의 jwt token 검증
            jwtDecoder.isValidToken(jwtToken);
        } else if (StompCommand.SUBSCRIBE == accessor.getCommand()) { //// 채팅룸 구독요청
            // header정보에서 구독 destination정보를 얻고, roomId를 추출한다.
            // roomId를 URL로 전송해주고 있어 추출 필요
            String roomId = chatService.getRoomId(Optional.ofNullable((String) message.getHeaders().get("simpDestination")).orElse("InvalidRoomId"));

            // 클라이언트 입장 메시지를 채팅방에 발송한다.(redis publish)
            String token = Optional.ofNullable(accessor.getFirstNativeHeader("token")).orElse("UnknownUser");
            String name = jwtDecoder.decodeUsername(token);
            chatService.sendChatMessage(ChatMessage.builder()
                    .type(ChatMessage.MessageType.ENTER)
                    .roomId(roomId)
                    .sender(name)
                    .build());

            log.info("SUBSCRIBED {}, {}", name, roomId);

        } else if (StompCommand.DISCONNECT == accessor.getCommand()) {// Websocket 연결 종료

        }
        return message;
    }
}
