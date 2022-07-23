package com.sparta.stockhub.controller;

import com.sparta.stockhub.domain.Stock;
import com.sparta.stockhub.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    // 주식: 전체 종목 리스트 조회
    @GetMapping("/stocks")
    public List<Stock> getStocks() {
        return stockService.getStocks();
    }

    // 주식: 종목 현재가 조회
    @GetMapping("/stock/price/{stockName}")
    public int getStockPrice(@PathVariable String stockName) {
        return stockService.getStockPrice(stockName);
    }
}
