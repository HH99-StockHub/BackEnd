package com.sparta.stockhub.service;

import com.sparta.stockhub.domain.Chart;
import com.sparta.stockhub.exceptionHandler.CustomException;
import com.sparta.stockhub.exceptionHandler.ErrorCode;
import com.sparta.stockhub.repository.ChartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChartService {

    private final ChartRepository chartRepository;

    // 차트: 종목 차트 조회
    public Chart getChart(String stockName) {
        Chart chart = chartRepository.findByStockName(stockName).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_STOCKNAME)
        );
        return chart;
    }
}
