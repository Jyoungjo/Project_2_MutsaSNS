package com.example.mutsasns.domain.comment.repository;

import com.example.mutsasns.domain.article.domain.Article;
import com.example.mutsasns.domain.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByArticleAndDeletedFalse(Article article);

    Optional<Comment> findByIdAndDeletedFalse(Long commentId);
}
