package com.example.mutsasns.domain.article.repository;

import com.example.mutsasns.domain.article.domain.Article;
import com.example.mutsasns.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article> findAllByUserAndDeletedFalse(User user);

    Optional<Article> findByIdAndDeletedFalse(Long articleId);

    List<Article> findAllByUserInOrderByIdDesc(List<User> userList);
}
