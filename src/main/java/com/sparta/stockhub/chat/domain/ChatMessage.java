package com.sparta.stockhub.chat.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class ChatMessage {
    public enum MessageType {   //메세지 타입 : 입장, 채팅
        ENTER, TAIL
    }

    private MessageType type; //메세지 타입
    private String roomId;  // 채팅방 번호
    private String sender;  // 메세지 보낸 사람 nickname? userId?
    private String nickname; // 메세지 보낸 사람 nickname
    private String message; //메세지 내용
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt; //날짜, 시간
}
