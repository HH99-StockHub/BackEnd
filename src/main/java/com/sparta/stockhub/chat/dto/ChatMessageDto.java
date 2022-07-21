package com.sparta.stockhub.chat.dto;

import com.sparta.stockhub.chat.domain.ChatMessage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageDto { //재수정

    private ChatMessage.MessageType type; //메세지 타입
    private String roomId;
    private Long userId;
    private String sender;
    private String message;
    private String createdAt;
}
