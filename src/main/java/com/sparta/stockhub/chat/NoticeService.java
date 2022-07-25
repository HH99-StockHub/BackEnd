package com.sparta.stockhub.chat;


import com.sparta.stockhub.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;


    public void createNotice(Long userId, Long articleId, String message, boolean check) {

        Long noticeUserId = userId;
        Long noticeArticleId = articleId;
        String noticeMessage = message;
        boolean noticeCheck = check;

        Notice newNotice = new Notice(noticeUserId, noticeArticleId , noticeMessage, noticeCheck);
        noticeRepository.save(newNotice);


    }

    public void deleteNotice(Long noticeId) {

        noticeRepository.deleteById(noticeId);
    }
}
