package com.sparta.stockhub.chat.domain;

import com.sparta.stockhub.chat.dto.ChatMessageDto;
import com.sparta.stockhub.security.UserDetailsImpl;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessage {

    public enum MessageType {   //메세지 타입 : 입장, 채팅
        ENTER, TAIL, QUIT
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private MessageType type; //메세지 타입
    @Column
    private String userName;
    @Column
    private String roomId;  // 채팅방 번호
    @Column
    private String sender;  // 메세지 보낸 사람 nickname이어야 함
    @Column
    private String message; //메세지 내용
    //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    //private Date createdAt; //날짜, 시간
    @Column
    private String createdAt;

    @Builder
    public ChatMessage(ChatMessageDto chatMessageDto, UserDetailsImpl userDetails) {
        this.type = chatMessageDto.getType();
        this.userName = userDetails.getUsername();
        this.roomId = chatMessageDto.getRoomId();
        this.sender = userDetails.getUsername();
        this.message = chatMessageDto.getMessage();
        this.createdAt = chatMessageDto.getCreatedAt();
    }

//    @Builder
//    public ChatMessage(MessageType type, String roomId, String userName, String sender, String message, String createdAt){
//        this.type = type;
//        this.roomId = roomId;
//        this.userName = userName;
//        this.sender = sender;
//        this.message = message;
//        this.createdAt = createdAt;
//
//    }
}

