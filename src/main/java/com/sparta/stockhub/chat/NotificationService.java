package com.sparta.stockhub.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class NotificationService {
//    private final SimpMessagingTemplate messagingTemplate;

    private final SimpMessageSendingOperations messagingTemplate;

    private final NoticeService noticeService;

    private final NoticeRepository noticeRepository;

    public void sendPrivateNotificationComment(String userNickname, Long articleUserId, Long articleId, Long commentUserId) {

       if(!articleUserId.equals(commentUserId)) {
           Long userId = articleUserId;
           String message = userNickname + " 님이 회원님의 게시글에 댓글을 달았습니다.";
           boolean check = false;
           String noticeCode = makeNoticeCode();
           noticeService.createNotice(userId, articleId, message, check, noticeCode);

           Notice notice = noticeRepository.findNoticeByNoticeCode(noticeCode);
           Long noticeId = notice.getNoticeId();
           LocalDateTime noticeCreatedAt = notice.getCreatedAt();
           Long noticeArticleId = articleId;

           ResponseMessage responseMessage = new ResponseMessage(userNickname + " 님이 회원님의 게시글에 댓글을 달았습니다.", noticeArticleId, noticeId, noticeCreatedAt);

           messagingTemplate.convertAndSend("/sub/topic/stockhub/" + articleUserId, responseMessage);
       }




    }




    public void sendPrivateNotificationVote(Long articleUserId, Long articleId) {


        Long userId = articleUserId;
        String message = "회원님의 게시글이 BEST 인기글에 등록되었습니다.";
        boolean check = false;


        String noticeCode = makeNoticeCode();
        noticeService.createNotice(userId, articleId, message, check, noticeCode);

        Notice notice = noticeRepository.findNoticeByNoticeCode(noticeCode);
        Long noticeId = notice.getNoticeId();
        LocalDateTime noticeCreatedAt = notice.getCreatedAt();

        ResponseMessage responseMessage = new ResponseMessage("회원님의 게시글이 BEST 인기글에 등록되었습니다.", articleId, noticeId, noticeCreatedAt);

        messagingTemplate.convertAndSend("/sub/topic/stockhub/" + articleUserId, responseMessage);

    }

    public void sendPrivateNotificationRich(Long articleUserId, Long articleId) {


        Long userId = articleUserId;
        String message = "회원님의 게시글은 BEST 수익왕에 등록되었습니다.";
        boolean check = false;


        String noticeCode = makeNoticeCode();
        noticeService.createNotice(userId, articleId, message, check, noticeCode);

        Notice notice = noticeRepository.findNoticeByNoticeCode(noticeCode);
        Long noticeId = notice.getNoticeId();
        LocalDateTime noticeCreatedAt = notice.getCreatedAt();

        ResponseMessage responseMessage = new ResponseMessage("회원님의 게시글은 BEST 수익왕에 등록되었습니다.", articleId, noticeId, noticeCreatedAt);

        messagingTemplate.convertAndSend("/sub/topic/stockhub/" + articleUserId, responseMessage);

    }

    public void sendPrivateNotificationLike(String userNickname, Long articleUserId, Long articleId) {

        Long userId = articleUserId;
        String message = userNickname + " 님이 회원님의 게시글에 추천을 눌렀습니다";
        boolean check = false;


        String noticeCode = makeNoticeCode();
        noticeService.createNotice(userId, articleId, message, check, noticeCode);

        Notice notice = noticeRepository.findNoticeByNoticeCode(noticeCode);
        Long noticeId = notice.getNoticeId();
        LocalDateTime noticeCreatedAt = notice.getCreatedAt();

        ResponseMessage responseMessage = new ResponseMessage(userNickname + " 님이 회원님의 게시글에 좋아요를 눌렀습니다", articleId, noticeId, noticeCreatedAt);

        messagingTemplate.convertAndSend("/sub/topic/stockhub/" + articleUserId, responseMessage);
    }

    public String makeNoticeCode(){
        Random random = new Random();
        System.out.println(random);

        LocalTime now = LocalTime.now();
        System.out.println(now);

        String noticeCode = random + "/" + now.toString();
        System.out.println(noticeCode);

        return noticeCode;
    }


}


