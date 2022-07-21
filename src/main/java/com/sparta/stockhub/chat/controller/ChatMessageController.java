package com.sparta.stockhub.chat.controller;

import com.sparta.stockhub.chat.domain.ChatMessage;
import com.sparta.stockhub.chat.dto.ChatMessageDto;
import com.sparta.stockhub.chat.service.ChatMessageService;
import com.sparta.stockhub.security.UserDetailsImpl;
import com.sparta.stockhub.security.jwt.JwtAuthProvider;
import com.sparta.stockhub.security.jwt.JwtDecoder;
import com.sparta.stockhub.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


@CrossOrigin(origins = "*")
@RestController
//@RequiredArgsConstructor
public class ChatMessageController { //재수정

    private final ChatMessageService chatMessageService;
    private final JwtDecoder jwtDecoder;
    private final UserService userService;
    private final JwtAuthProvider jwtAuthProvider;
    private UserDetailsImpl userDetails;

    @Autowired
    public ChatMessageController(ChatMessageService chatMessageService, UserService userService, JwtDecoder jwtDecoder, JwtAuthProvider jwtAuthProvider) {
        this.chatMessageService = chatMessageService;
        this.userService = userService;
        this.jwtDecoder = jwtDecoder;
        this.jwtAuthProvider = jwtAuthProvider;
    }
    // 채팅 메시지를 @MessageMapping 형태로 받는다
    // 웹소켓으로 publish 된 메시지를 받는 곳이다
    @MessageMapping("/api/chat/message")
    public void message(@RequestBody ChatMessageDto chatMessageDto, @Header("token") String token) { //@AuthenticationPrincipal
        String username = jwtDecoder.decodeUsername(token);
//        chatMessageDto.setUserId(user.getUserId);
        chatMessageDto.setSender(username); //

        // 메시지 생성 시간 삽입
        SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        String dateResult = dateFormat.format(date);
        chatMessageDto.setCreatedAt(dateResult);

         //DTO 로 채팅 메시지 객체 생성
        ChatMessage chatMessage = new ChatMessage(chatMessageDto, userDetails);

         //웹소켓 통신으로 채팅방 토픽 구독자들에게 메시지 보내기
        chatMessageService.sendChatMessage(chatMessage);

        // MySql DB에 채팅 메시지 저장
        chatMessageService.save(chatMessage);
    }
}
