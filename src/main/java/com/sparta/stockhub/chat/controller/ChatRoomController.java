package com.sparta.stockhub.chat.controller;

import com.sparta.stockhub.chat.domain.ChatMessage;
import com.sparta.stockhub.chat.domain.ChatRoom;
import com.sparta.stockhub.chat.dto.ChatRoomDto;
import com.sparta.stockhub.chat.service.ChatMessageService;
import com.sparta.stockhub.chat.service.ChatRoomService;
import com.sparta.stockhub.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatRoomController { // 재수정

    private final ChatMessageService chatMessageService;
    private final ChatRoomService chatRoomService;

//    @Autowired
//    public ChatRoomController(ChatMessageService chatMessageService, ChatRoomService chatRoomService) {
//        this.chatMessageService = chatMessageService;
//        this.chatRoomService = chatRoomService;
//    }

    // 채팅방 생성 (생략 가능성)
    @PostMapping("/rooms")
    public ChatRoom createChatRoom(@RequestBody ChatRoomDto chatRoomDto, @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        chatRoomDto.setUserId(userDetails.getUser().getUserId());
        ChatRoom chatRoom = chatRoomService.createChatRoom(chatRoomDto);
        return chatRoom;
    }

    // 채팅방 상세 조회
    @GetMapping("/rooms/{roomId}")
    public ChatRoom getEachChatRoom(@PathVariable Long roomId) {
        return chatRoomService.getEachChatRoom(roomId);
    }

    // 채팅방 내 메시지 전체 조회
    @GetMapping("/rooms/{roomId}/messages")
    public Page<ChatMessage> getEachChatRoomMessages(@PathVariable String roomId, @PageableDefault Pageable pageable) {
        return chatMessageService.getChatMessageByRoomId(roomId, pageable);
    }
}
