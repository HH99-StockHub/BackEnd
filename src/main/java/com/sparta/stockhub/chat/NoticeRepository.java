package com.sparta.stockhub.chat;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    List<Notice> findAllByNoticeUserId(Long noticeUserId);

    Notice findNoticeByNoticeId(Long noticeId);

    Notice findNoticeByNoticeCode(String noticeCode);

}
