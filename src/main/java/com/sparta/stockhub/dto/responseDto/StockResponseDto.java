package com.sparta.stockhub.dto.responseDto;

import com.sparta.stockhub.domain.Stock;
import lombok.Getter;

@Getter
public class StockResponseDto {

    private String stockName;

    private int stockPrice;

    private int change;

    private float changeRate;

    private int lastPrice;

    private int startPrice;

    private int highPrice;

    private int lowPrice;

    private int tradeVolume;

    public StockResponseDto(Stock stock) {
        this.stockName = stock.getStockName();
        this.stockPrice = stock.getStockPrice();
        this.change = stock.getChange();
        this.changeRate = stock.getChangeRate();
        this.lastPrice = stock.getLastPrice();
        this.startPrice = stock.getStartPrice();
        this.highPrice = stock.getHighPrice();
        this.lowPrice = stock.getLowPrice();
        this.tradeVolume = stock.getTradeVolume();
    }
}
