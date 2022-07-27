package com.sparta.stockhub.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisSubscriber { // 2차
    private final ObjectMapper objectMapper;
    private final SimpMessageSendingOperations messagingTemplate;

    // Redis에서 메시지가 발행(publish)되면 대기하고 있던 onMessage가 해당 메시지를 받아 처리한다.
    public void sendMessage(String publishMessage) {
        try {
            // ChatMessage 객채로 맵핑
            ChatMessage roomMessage = objectMapper.readValue(publishMessage, ChatMessage.class);
            // Websocket 구독자에게 채팅 메시지 Send
            messagingTemplate.convertAndSend("/sub/topic/stockhub", roomMessage);
            System.out.println(roomMessage.getNickName());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
