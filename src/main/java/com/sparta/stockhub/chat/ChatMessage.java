package com.sparta.stockhub.chat;

import lombok.*;
import java.io.Serializable;


@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage implements Serializable{
        private static final long serialVersionUID = -1446398935944895849L;

    public enum MessageType { //메세지 타입 /참여, 채팅
        ENTER, TALK, ALARM
    }
    private MessageType type; //
    private String sendTime; //
    private String nickName; //
    private Long userId;
    private String message; //
    private String imageUrl; //
    private String rank;
    private boolean clear;

    public ChatMessage(MessageType type, Long userId) {
        this.type = type;
        this.userId = userId;
    }
}

