package com.sparta.stockhub.chat.service;

import com.sparta.stockhub.chat.domain.ChatMessage;
import com.sparta.stockhub.chat.dto.ChatMessageDto;
import com.sparta.stockhub.chat.utils.RedisPublisher;
import com.sparta.stockhub.security.jwt.JwtDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final JwtDecoder jwtDecoder;
    private final ChatRoomRepository chatRoomRepository;
    private final RedisPublisher redisPublisher;

    public void save(ChatMessageDto messageDto, String token) {
        log.info("save Message : {}", messageDto.getMessage());
        String username = "";   // username 세팅
        String sender = "";

        if (messageDto.getMessage().trim().equals("") && messageDto.getType() != ChatMessage.MessageType.ENTER) {
            throw new IllegalArgumentException("메세지 입력 안됨");
        }

        if (!(String.valueOf(token).equals("Authorization") || String.valueOf(token).equals("null"))) {
            String tokenInfo = token.substring(7); // Bearer빼고
            username = jwtDecoder.decodeUsername(tokenInfo);
            sender = jwtDecoder.decodeUsername(tokenInfo);
        }

        ChatMessage message = new ChatMessage(messageDto);

        message.setSender(sender);
        message.setNickname(username);

        //시간 세팅
        Date date = new Date();
        message.setCreatedAt(date);
        if (ChatMessage.MessageType.ENTER.equals(message.getType())) {
            chatRoomRepository.enterChatRoom(message.getRoomId());
            message.setMessage(message.getSender() + "님이 입장 하셨습니다.");
        } else {
            chatMessageRepository.save(message);
        }
        //websocket에 발행된 메세지를 redis로 발행한다. (publish)
        redisPublisher.publish(chatRoomRepository.getTopic(message.getRoomId()).message);
    }

    // redis에 저장되어 있는 message 출력
    public List<ChatMessage> getMessages(String roomId) {
        log.info("getMessages roomId : {}", roomId);
        List<ChatMessage> chatMessageList = chatMessageRepository.findAllMessage(roomId);
        return chatMessageList;
    }
}
