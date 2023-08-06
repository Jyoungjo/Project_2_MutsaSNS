package com.example.mutsasns.domain.article.repository;

import com.example.mutsasns.domain.article.domain.Article;
import com.example.mutsasns.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article> findAllByUserAndDeletedFalse(User user);

    Article findByIdAndDeletedFalse(Long articleId);
}
