package com.sparta.stockhub.chat.controller;

import com.sparta.stockhub.chat.domain.ChatMessage;
import com.sparta.stockhub.chat.dto.ChatMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatMessageController {

    private ChatMessageService chatMessageService;

    @MessageMapping("/chat/message") // // 채팅 메시지를 @MessageMapping 형태로 받는다
    public void message(ChatMessageDto message, @Header("Authorization") String token) {
        log.info("요청 메서드 [Message] / chat/message");
        chatMessageService.save(message, token);
    }

    @GetMapping("/chat/message/{roomId}")
    @ResponseBody
    public List<ChatMessage> getMessages(@PathVariable String roomId) {
        log.info("요청 메서드 [GET] /chat/message/{roomId}");
        return chatMessageService.getMessages(roomId);
    }
}
