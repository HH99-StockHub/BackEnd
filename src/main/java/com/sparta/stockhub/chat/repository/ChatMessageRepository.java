package com.sparta.stockhub.chat.repository;

import com.sparta.stockhub.chat.domain.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> { // 재수정
    Page<ChatMessage> findByRoomId(String roomId, Pageable pageable);
}
