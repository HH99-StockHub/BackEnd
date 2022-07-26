package com.sparta.stockhub.dto.responseDto;

import com.sparta.stockhub.domain.Stock;
import lombok.Getter;

@Getter
public class StockResponseDto {

    private String stockName;

    private int stockPrice;

    private int increment;

    private float incrementRate;

    private int lastPrice;

    private int startPrice;

    private int highPrice;

    private int lowPrice;

    private int tradeVolume;

    public StockResponseDto(Stock stock) {
        this.stockName = stock.getStockName();
        this.stockPrice = stock.getStockPrice();
        this.increment = stock.getIncrement();
        this.incrementRate = stock.getIncrementRate();
        this.lastPrice = stock.getLastPrice();
        this.startPrice = stock.getStartPrice();
        this.highPrice = stock.getHighPrice();
        this.lowPrice = stock.getLowPrice();
        this.tradeVolume = stock.getTradeVolume();
    }
}
