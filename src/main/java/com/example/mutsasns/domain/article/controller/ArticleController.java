package com.example.mutsasns.domain.article.controller;

import com.example.mutsasns.domain.article.dto.RequestArticleDto;
import com.example.mutsasns.domain.article.dto.ResponseArticleDto;
import com.example.mutsasns.domain.article.dto.ResponseArticleListDto;
import com.example.mutsasns.domain.article.service.ArticleService;
import com.example.mutsasns.domain.images.service.ImageService;
import com.example.mutsasns.global.messages.ResponseDto;
import com.example.mutsasns.global.messages.SystemMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users/{userId}/articles")
public class ArticleController {
    private final ArticleService articleService;
    private final ImageService imageService;

    @PostMapping
    public ResponseEntity<ResponseDto> create(
            @RequestBody RequestArticleDto dto,
            @PathVariable("userId") Long userId,
            Authentication authentication
    ) {
        String username = authentication.getName();
        articleService.createArticle(dto, username, userId);

        log.info("게시글 작성 성공");
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseDto.getInstance(SystemMessage.REGISTER_ARTICLE));
    }

    @GetMapping
    public ResponseEntity<List<ResponseArticleListDto>> readAllByUser(
            Authentication authentication, @PathVariable("userId") Long userId
    ) {
        String username = authentication.getName();

        log.info("게시글 목록 조회 성공");
        return ResponseEntity.ok(articleService.readAllArticles(username, userId));
    }

    @GetMapping("/{articleId}")
    public ResponseEntity<ResponseArticleDto> read(
            Authentication authentication,
            @PathVariable("userId") Long userId,
            @PathVariable("articleId") Long articleId
    ) {
        String username = authentication.getName();

        log.info("게시글 단일 조회 성공");
        return ResponseEntity.ok(articleService.readArticle(username, userId, articleId));
    }

    @PutMapping("/{articleId}")
    public ResponseEntity<ResponseDto> update(
            @RequestParam("image") List<MultipartFile> images, @RequestBody RequestArticleDto dto,
            @PathVariable("userId") Long userId, @PathVariable("articleId") Long articleId,
            Authentication authentication
    ) throws IOException {
        String username = authentication.getName();

        if (images.isEmpty()) {
            articleService.updateArticle(dto, username, userId, articleId);
        } else {
            imageService.updateImage(images, username, userId, articleId);
        }

        log.info("게시글 수정 성공");
        return ResponseEntity.ok(ResponseDto.getInstance(SystemMessage.UPDATE_ARTICLE));
    }

    @DeleteMapping("/{articleId}")
    public ResponseEntity<ResponseDto> delete(
            @PathVariable("userId") Long userId, @PathVariable("articleId") Long articleId,
            Authentication authentication
    ) {
        String username = authentication.getName();
        articleService.deleteArticle(username, userId, articleId);

        log.info("게시글 삭제 성공");
        return ResponseEntity.ok(ResponseDto.getInstance(SystemMessage.DELETE_ARTICLE));
    }
}
