package com.example.mutsasns.domain.like_article.controller;

import com.example.mutsasns.domain.like_article.dto.ResponseLikeDto;
import com.example.mutsasns.domain.like_article.service.LikeArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/articles/{articleId}")
public class LikeArticleController {
    private final LikeArticleService likeArticleService;

    @PostMapping("/like")
    public ResponseEntity<ResponseLikeDto> createLikeArticle(
            @PathVariable("articleId") Long articleId,
            Authentication authentication
    ) {
        String username = authentication.getName();
        return ResponseEntity.ok(likeArticleService.pushLike(username, articleId));
    }
}
