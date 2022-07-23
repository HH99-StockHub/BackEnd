package com.sparta.stockhub.controller;

import com.sparta.stockhub.domain.Chart;
import com.sparta.stockhub.service.ChartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChartController {

    private final ChartService chartService;

    // 차트: 종목 차트 조회
    @GetMapping("/chart/{stockName}")
    public Chart getChart(@PathVariable String stockName) {
        return chartService.getChart(stockName);
    }
}
