package com.sparta.stockhub.chat;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
public class ChatMessageController { //재수정

    private final AlarmUtil alarmUtil;
    private final RedisTemplate redisTemplate;
    private final ChannelTopic channelTopic;

    //websocket "/pub/chat/message"로 들어오는 메시징을 처리한다.
    @MessageMapping("/chat/message") // FE : pub/chat/message
    @SendTo("/sub/public") //구독
    public void message(ChatMessage chatMessage) {
        if (ChatMessage.MessageType.ENTER.equals(chatMessage.getType())) {
           chatMessage.setMessage(chatMessage.getNickName() + "님이 입장하셨습니다.");
        } else if(!chatMessage.isClear() && ChatMessage.MessageType.TALK.equals(chatMessage.getType())) {

            alarmUtil.sendAlarm(chatMessage.getUserId());
        }
        System.out.println(chatMessage.getUserId().toString());
        // webSocket에 발행된 메세지를 redis로 발행한다(publish)
        redisTemplate.convertAndSend(channelTopic.getTopic(), chatMessage);
    }
}
