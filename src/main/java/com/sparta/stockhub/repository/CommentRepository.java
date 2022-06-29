package com.sparta.stockhub.repository;

import com.sparta.stockhub.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<Comment> findByCommentId(Long commentId);

    void deleteByCommentId(Long commentId);

    List<Comment> findByCommentIdOrderByCreatedAtDesc(Long articleId);
}
