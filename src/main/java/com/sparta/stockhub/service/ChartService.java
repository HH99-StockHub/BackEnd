package com.sparta.stockhub.service;

import com.sparta.stockhub.domain.Chart;
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
                () -> new NullPointerException("종목이 존재하지 않습니다.")
        );
        return chart;
    }
}
