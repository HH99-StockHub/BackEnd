package com.sparta.stockhub.utils;

import com.sparta.stockhub.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class Scheduler {

    private final ArticleService articleService;

    @Scheduled(cron = "0 0/10 * * * *") // "초 - 분 - 시 - 일 - 월 - 주" 순서이며 현재는 매 0분 0초를 시작으로 매 10분마다 작동
    public void updatePrice() throws InterruptedException {
        articleService.updateArticle();
        articleService.checkRichList();
    }
}