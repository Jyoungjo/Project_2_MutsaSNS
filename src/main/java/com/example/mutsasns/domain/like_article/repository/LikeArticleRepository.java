package com.example.mutsasns.domain.like_article.repository;

import com.example.mutsasns.domain.article.domain.Article;
import com.example.mutsasns.domain.like_article.domain.LikeArticle;
import com.example.mutsasns.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeArticleRepository extends JpaRepository<LikeArticle, Long> {
    Optional<LikeArticle> findByUserAndArticle(User user, Article article);
}
