package com.sparta.stockhub.repository;

import com.sparta.stockhub.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    Optional<Article> findByArticleId(Long articleId);

    void deleteByArticleId(Long articleId);

    List<Article> findAllByArticleTitleContaining (String search);

    List<Article> findAllByStockNameContaining (String search);

    List<Article> findAllByPoint1ContainingOrPoint2ContainingOrPoint3Containing (String point1, String point2, String point3);

    List<Article> findAllByContent1ContainingOrContent2ContainingOrContent3Containing (String content1, String content2, String content3);

    List<Article> findAllByArticleTitleContainingOrStockNameContainingOrPoint1ContainingOrPoint2ContainingOrPoint3ContainingOrContent1ContainingOrContent2ContainingOrContent3Containing(String title, String stockname, String point1, String point2, String point3, String content1, String content2, String content3);

}
