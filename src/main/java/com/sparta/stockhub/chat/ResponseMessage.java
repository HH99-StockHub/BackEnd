package com.sparta.stockhub.chat;

public class ResponseMessage {
    private String content;

    private Long articleId;

    public ResponseMessage() {
    }

    public ResponseMessage(String content, Long articleId) {
        this.content = content;
        this.articleId = articleId;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }
}
