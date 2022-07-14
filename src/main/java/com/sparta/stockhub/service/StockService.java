package com.sparta.stockhub.service;

import com.sparta.stockhub.domain.Stock;
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

    // 종목 현재가 조회
    public int getStockPrice(String stockName) {
        Stock stock = stockRepository.findByStockName(stockName).orElseThrow(
                () -> new NullPointerException("종목이 존재하지 않습니다.")
        );
        return stock.getStockPrice();
    }

    // 종목 수익률 계산
    public double getStockReturn(int stockPriceFirst, int stockPriceLast) {
        double stockReturn = (double) (stockPriceLast - stockPriceFirst) / stockPriceFirst;
        return stockReturn;
    }
}
