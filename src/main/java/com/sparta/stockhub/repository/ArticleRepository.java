package com.sparta.stockhub.repository;

import com.sparta.stockhub.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    Optional<Article> findByArticleId(Long articleId);

    List<Article> findAllByArticleId(Long articleId);

    List<Article> findAllByArticleTitleContainingOrStockNameContainingOrPoint1ContainingOrPoint2ContainingOrPoint3ContainingOrContent1ContainingOrContent2ContainingOrContent3ContainingOrderByCreatedAtDesc(String title, String stockname, String point1, String point2, String point3, String content1, String content2, String content3);

    List<Article> findAllByOrderByCreatedAtDesc();

    List<Article> findAllByPopularListOrderByVoteUpCountDesc(int popularList);

    List<Article> findAllByRichListOrderByStockReturnDesc(int richList);

    List<Article> findAllByPopularListOrderByPopularRegTimeDesc(int popularList);

    List<Article> findAllByRichListOrderByRichRegTimeDesc(int richList);

    List<Article> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    List<Article> findAllByArticleTitleContainingOrStockNameContainingOrderByCreatedAtDesc(String keywordsTrimmed, String keywordsTrimmed1);

    List<Article> findAllByPoint1ContainingOrPoint2ContainingOrPoint3ContainingOrContent1ContainingOrContent2ContainingOrContent3ContainingOrderByCreatedAtDesc(String keywordsTrimmed, String keywordsTrimmed1, String keywordsTrimmed2, String keywordsTrimmed3, String keywordsTrimmed4, String keywordsTrimmed5);
}
