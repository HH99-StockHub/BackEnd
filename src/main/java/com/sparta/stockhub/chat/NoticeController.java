package com.sparta.stockhub.chat;


import com.sparta.stockhub.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeRepository noticeRepository;

    private final NoticeService noticeService;

    @GetMapping("/notice/{noticeUserId}")
    public List<Notice> readNotice(@PathVariable Long noticeUserId) {
        return noticeRepository.findAllByNoticeUserId(noticeUserId);
    }

    @DeleteMapping("/notice/{noticeId}")
    public void deleteNotice(@PathVariable Long noticeId) {
        noticeService.deleteNotice(noticeId);
    }
}
