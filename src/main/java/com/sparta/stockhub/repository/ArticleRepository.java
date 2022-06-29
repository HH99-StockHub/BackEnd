package com.sparta.stockhub.repository;

import com.sparta.stockhub.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    Optional<Article> findByArticleId(Long articleId);

    void deleteByArticleId(Long articleId);
}
