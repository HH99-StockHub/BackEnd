package com.sparta.stockhub.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AlarmUtil {
    private final RedisTemplate redisTemplate;
    private final ChannelTopic channelTopic;
    public void sendAlarm(Long userId) {
        ChatMessage alarm = new ChatMessage(ChatMessage.MessageType.ALARM, userId);
        redisTemplate.convertAndSend(channelTopic.getTopic(), alarm);
    }
}
