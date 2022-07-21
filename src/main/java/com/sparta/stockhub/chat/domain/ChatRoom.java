package com.sparta.stockhub.chat.domain;

import com.sparta.stockhub.chat.dto.ChatRoomDto;
import com.sparta.stockhub.domain.Timestamped;
import com.sparta.stockhub.service.UserService;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
public class ChatRoom extends Timestamped { //재수정
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;

   private String userName;

    public ChatRoom(ChatRoomDto chatRoomDto, UserService userService) {
        this.userName= String.valueOf(userService.findByUsername(chatRoomDto.getUserName()));
    }
}
