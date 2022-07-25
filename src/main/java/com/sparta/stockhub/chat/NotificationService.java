package com.sparta.stockhub.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {
//    private final SimpMessagingTemplate messagingTemplate;

    private final SimpMessageSendingOperations messagingTemplate;

    private final NoticeService noticeService;

    public void sendPrivateNotificationComment(String userNickname, Long articleUserId, Long articleId, Long commentUserId) {
       ResponseMessage responseMessage = new ResponseMessage(userNickname + " 님이 회원님의 게시글에 댓글을 달았습니다.", articleId);
       if(articleId != commentUserId) {
           Long userId = articleUserId;
           String message = userNickname + " 님이 회원님의 게시글에 뎃글을 달았습니다.";
           boolean check = false;
           noticeService.createNotice(userId, articleId, message, check);
       }


        messagingTemplate.convertAndSend("/sub/topic/stockhub/" + articleUserId, responseMessage);
//        messagingTemplate.convertAndSend("/sub/topic/stockhub", responseMessage);/*로컬 테스트용*/
    }




    public void sendPrivateNotificationVote(String articleTitle, Long articleUserId, Long articleId) {
        ResponseMessage responseMessage = new ResponseMessage("회원님의 \"" + articleTitle + "\" 게시글이 BEST 인기글에 등록되었습니다.", articleId);

        Long userId = articleUserId;
        String message = "회원님의 \"" + articleTitle + "\" 게시글이 BEST 인기글에 등록되었습니다.";
        boolean check = false;


        noticeService.createNotice(userId, articleId, message, check);

        messagingTemplate.convertAndSend("/sub/topic/stockhub/" + articleUserId, responseMessage);

    }

    public void sendPrivateNotificationRich(String articleTitle, Long articleUserId, Long articleId) {
        ResponseMessage responseMessage = new ResponseMessage("회원님의 \"" + articleTitle + "\" 게시글은 BEST 수익왕에 등록되었습니다.", articleId);

        Long userId = articleUserId;
        String message = "회원님의 \"" + articleTitle + "\" 게시글은 BEST 수익왕에 등록되었습니다.";
        boolean check = false;


        noticeService.createNotice(userId, articleId, message, check);

        messagingTemplate.convertAndSend("/sub/topic/stockhub/" + articleUserId, responseMessage);

    }
}

//    public void createComment(UserDetailsImpl userDetails, Long articleId, CommentRequestDto requestDto) {
//        String comments = requestDto.getComments();
//        Long loginId = userDetails.getUser().getUserId();
//        Article article = articleRepository.findByArticleId(articleId).orElseThrow(
//                () -> new CustomException(ErrorCode.NOT_FOUND_ARTICLE)
//        );
//        if (comments.equals("")) throw new CustomException(ErrorCode.BAD_REQUEST_NOTWRITE);
//        if (comments.length() > 300) throw new CustomException(ErrorCode.BAD_REQUEST_COMMENTLENGTH);
//        Comment newComment = new Comment(loginId, articleId, comments);
//        commentRepository.save(newComment);
//    }
