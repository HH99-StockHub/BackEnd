package com.sparta.stockhub.service;

import com.sparta.stockhub.domain.Article;
import com.sparta.stockhub.domain.Stock;
import com.sparta.stockhub.repository.ArticleRepository;
import com.sparta.stockhub.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;
    private final ArticleRepository articleRepository;

    // 주식 종목 정보 조회
    public Stock getStockInfo(String stockName) {
        Stock stock = stockRepository.findByStockName(stockName).orElse(null);
        if (stock == null) {
            stock = new Stock("005930", "삼성전자", 0);
        }
        // 주식 현재가 조회 추가 필요
        stockRepository.save(stock);
        return stock;
    }

    // 주식 현재가 조회
    public Stock updateStockInfo(String stockName) {
        Stock stock = stockRepository.findByStockName(stockName).orElse(null);
        // 주식 현재가 조회 추가 필요
        return stock;
    }

    // 주식 수익률 계산
    public double getStockReturn(Long articleId) {
        Article article = articleRepository.findByArticleId(articleId).orElseThrow(
                () -> new NullPointerException("게시글이 존재하지 않습니다.")
        );
        int stockPriceFirst = article.getStockPriceFirst();
        int stockPriceLast = 0; // 주식 현재가 조회 추가 필요
        double stockReturn = stockPriceLast / stockPriceFirst - 1;
        return stockReturn;
    }
}
