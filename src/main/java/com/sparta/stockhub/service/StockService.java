package com.sparta.stockhub.service;

import com.sparta.stockhub.domain.Stock;
import com.sparta.stockhub.dto.responseDto.StockResponseDto;
import com.sparta.stockhub.exceptionHandler.CustomException;
import com.sparta.stockhub.exceptionHandler.ErrorCode;
import com.sparta.stockhub.repository.ArticleRepository;
import com.sparta.stockhub.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

    // 주식: 전체 종목 리스트 조회
    public List<Stock> getStocks() {
        List<Stock> stocks = stockRepository.findAll();
        return stocks;
    }

    // 주식: 종목 현재가 조회
    public int getStockPrice(String stockName) {
        Stock stock = stockRepository.findByStockName(stockName).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_STOCKNAME)
        );
        return stock.getStockPrice();
    }

    // 주식: 종목 상세정보 조회
    public StockResponseDto getStockDetails(String stockName) {
        Stock stock = stockRepository.findByStockName(stockName).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_STOCKNAME)
        );
        StockResponseDto responseDto = new StockResponseDto(stock);
        return responseDto;
    }

    // 종목 수익률 계산
    public double getStockReturn(int stockPriceFirst, int stockPriceLast) {
        double stockReturn = (double) (stockPriceLast - stockPriceFirst) / stockPriceFirst;
        stockReturn *= 100;
        return stockReturn;
    }

    // 게시글 작성 시 등록 주식 표시
    @Transactional
    public void registerStock(String stockName) {
        Stock stock = stockRepository.findByStockName(stockName).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_STOCKNAME)
        );
        stock.setRegistered(true);
        stockRepository.save(stock);
    }
}
