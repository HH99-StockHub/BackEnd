package com.sparta.stockhub.service;

import com.sparta.stockhub.domain.Stock;
<<<<<<< HEAD
import com.sparta.stockhub.exceptionHandler.CustomException;
import com.sparta.stockhub.exceptionHandler.ErrorCode;
import com.sparta.stockhub.repository.ArticleRepository;
=======
>>>>>>> origin/feat/mongoConnect
import com.sparta.stockhub.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

    // 전체 종목 목록 조회
    public List<Stock> getStocks() {
        List<Stock> stocks = stockRepository.findAll();
        return stocks;
    }

<<<<<<< HEAD
    // 주식 현재가 조회
    public Stock updateStockInfo(String stockName) {
        Stock stock = stockRepository.findByStockName(stockName).orElse(null);
        // 주식 현재가 조회 추가 필요
        return stock;
    }

    // 주식 수익률 계산
    public double getStockReturn(Long articleId) {
        Article article = articleRepository.findByArticleId(articleId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_ARTICLE)
=======
    // 종목 현재가 조회
    public int getStockPrice(String stockName) {
        Stock stock = stockRepository.findByStockName(stockName).orElseThrow(
                () -> new NullPointerException("종목이 존재하지 않습니다.")
>>>>>>> origin/feat/mongoConnect
        );
        return stock.getPrice();
    }

    // 종목 수익률 계산
    public double getStockReturn(int stockPriceFirst, int stockPriceLast) {
        double stockReturn = (double) (stockPriceLast - stockPriceFirst) / stockPriceFirst;
        return stockReturn;
    }
}
