package com.sparta.stockhub.service;

import com.sparta.stockhub.domain.Stock;
import com.sparta.stockhub.domain.User;
import com.sparta.stockhub.dto.requestDto.ArticleRequestDto;
import com.sparta.stockhub.repository.ArticleRepository;
import com.sparta.stockhub.repository.StockRepository;
import com.sparta.stockhub.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final StockRepository stockRepository;

    // 게시글 작성
    public void createArticle(UserDetailsImpl userDetails, ArticleRequestDto requestDto) {
        Long userId = userDetails.getUser().getUserId();
        String articleTitle = requestDto.getArticleTitle();
        String stockName = requestDto.getStockName();
        String point1 = requestDto.getPoint1();
        String content1 = requestDto.getContent1();
        String point2 = requestDto.getPoint2();
        String content2 = requestDto.getContent2();
        String point3 = requestDto.getPoint3();
        String content3 = requestDto.getContent3();

        if (articleTitle.equals("")) throw new IllegalArgumentException("제목 작성이 필요합니다.");
        if (stockName.equals(null)) throw new IllegalArgumentException("종목 선택이 필요합니다.");
        if (point1.equals("")) throw new IllegalArgumentException("투자포인트 작성이 필요합니다.");
        if (content1.equals("")) throw new IllegalArgumentException("세부 내용 작성이 필요합니다.");
        if (articleTitle.length() > 40) throw new IllegalArgumentException("제목은 40자 이내로 작성해주세요.");
        if (point1.length() > 40) throw new IllegalArgumentException("투자포인트는 40자 이내로 작성해주세요.");
        if (content1.length() > 800) throw new IllegalArgumentException("세부 내용은 800자 이내로 작성해주세요.");
        if (point2.length() > 40) throw new IllegalArgumentException("투자포인트는 40자 이내로 작성해주세요.");
        if (content2.length() > 800) throw new IllegalArgumentException("세부 내용은 800자 이내로 작성해주세요.");
        if (point3.length() > 40) throw new IllegalArgumentException("투자포인트는 40자 이내로 작성해주세요.");
        if (content3.length() > 800) throw new IllegalArgumentException("세부 내용은 800자 이내로 작성해주세요.");

        Stock stock = StockRepository.findByStockName(stockName).orElse(null);
        if (stock == null) {
            stock = new Stock("005930", "삼성전자", 58000);
            stockRepository.save(stock);
        }





    }
}
