package com.sparta.stockhub.chat.service;

import com.sparta.stockhub.chat.domain.ChatRoom;
import com.sparta.stockhub.chat.dto.ChatRoomDto;
import com.sparta.stockhub.chat.repository.ChatRoomRepository;
import com.sparta.stockhub.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ChatRoomService { // 재수정
    @Resource(name = "redisTemplate")
    private HashOperations<String, String, String> hashOpsEnterInfo;
    private final ChatRoomRepository chatRoomRepository;
    private final UserService userService;
    public static final String ENTER_INFO = "ENTER_INFO"; // 채팅룸에 입장한 클라이언트의 sessionId 와 채팅룸 id 를 맵핑한 정보 저장

    @Autowired
    public ChatRoomService(ChatRoomRepository chatRoomRepository, UserService userService) {
        this.chatRoomRepository = chatRoomRepository;
        this.userService = userService;
    }

    // 채팅방 생성 ( 생략 가능성)
    public ChatRoom createChatRoom(ChatRoomDto chatRoomDto) {
        ChatRoom chatRoom = new ChatRoom(chatRoomDto, userService);
        chatRoomRepository.save(chatRoom);
        return chatRoom;
    }

    //채팅방 조회(개별)
    public ChatRoom getEachChatRoom(Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow( ()-> new IllegalArgumentException("채팅방 존재하지 않음"));
        return chatRoom;
    }
}
