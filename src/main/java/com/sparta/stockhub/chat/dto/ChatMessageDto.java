package com.sparta.stockhub.chat.dto;

import com.sparta.stockhub.chat.domain.ChatMessage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageDto {

    private ChatMessage.MessageType type; //메세지 타입
    private String roomId;
    private String message;
}
