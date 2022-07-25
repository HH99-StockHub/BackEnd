package com.sparta.stockhub.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public NotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

//    public void sendGlobalNotification() {
//        ResponseMessage message = new ResponseMessage("Global Notification");
//
//        messagingTemplate.convertAndSend("/topic/global-notifications", message);
//    }

//    public void sendPrivateNotification(final String userId) {
//        ResponseMessage message = new ResponseMessage("Private Notification");
//
//        messagingTemplate.convertAndSendToUser(userId,"/topic/private-notifications", message);
//    }

    public void sendPrivateNotificationComment(String userNickname, String articleTitle, String articleUserId) {
        ResponseMessage message = new ResponseMessage(userNickname + " 님이\"" + articleTitle + "\" 게시글에 댓글을 달았습니다!");

//        messagingTemplate.convertAndSend("/sub/topic/stockhub/" + articleUserId, message);이걸로 할 예정
        messagingTemplate.convertAndSend("/sub/topic/stockhub", message);/*로컬 테스트용*/
    }
}
