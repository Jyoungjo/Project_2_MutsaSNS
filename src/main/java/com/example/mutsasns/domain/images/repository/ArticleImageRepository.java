package com.example.mutsasns.domain.images.repository;

import com.example.mutsasns.domain.images.domain.ArticleImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleImageRepository extends JpaRepository<ArticleImage, Long> {
    List<ArticleImage> findArticleImagesByArticleIdOrderById(Long articleId);
}
