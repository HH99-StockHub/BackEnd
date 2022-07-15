package com.sparta.stockhub.repository;

import com.sparta.stockhub.domain.VoteUp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VoteUpRepository extends JpaRepository<VoteUp, Long> {
    List<VoteUp> findAllByArticleId(Long articleId);

    Optional<VoteUp> findByUserIdAndArticleId(Long userId, Long articleId);
}
