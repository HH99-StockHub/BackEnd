package com.sparta.stockhub.controller;

import com.sparta.stockhub.domain.Index;
import com.sparta.stockhub.repository.IndexRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class IndexController {

    private final IndexRepository indexRepository;

    // 인덱스: 전체 인덱스 조회
    @GetMapping("/indices")
    public List<Index> getIndices() {
        return indexRepository.findAll();
    }
}
