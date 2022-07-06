package com.sparta.stockhub.repository;

import com.sparta.stockhub.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    Optional<Article> findByArticleId(Long articleId);

    List<Article> findAllOrderByCreatedAtDesc();

    List<Article> findAllByPopularListOrderByVoteUpCountDesc(boolean popularList);

    List<Article> findAllByRichListOrderByStockReturnDesc(boolean richList);

    List<Article> findAllByPopularListOrderByCreatedAtDesc(boolean popularList);

    List<Article> findAllByRichListOrderByCreatedAtDesc(boolean richList);

    List<Article> findAllByUserIdOrderByCreatedAtDesc(Long userId);
}
