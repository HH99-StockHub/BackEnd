package com.sparta.stockhub.repository;

import com.sparta.stockhub.domain.VoteDown;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VoteDownRepository extends JpaRepository<VoteDown, Long> {
    List<VoteDown> findAllByArticleId(Long articleId);

    Optional<VoteDown> findByUserIdAndArticleId(Long loginId, Long articleId);
}
