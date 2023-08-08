package com.example.mutsasns.domain.article.controller;

import com.example.mutsasns.domain.article.dto.RequestArticleDto;
import com.example.mutsasns.domain.article.dto.ResponseArticleDto;
import com.example.mutsasns.domain.article.dto.ResponseArticleListDto;
import com.example.mutsasns.domain.article.service.ArticleService;
import com.example.mutsasns.domain.images.service.ArticleImageService;
import com.example.mutsasns.global.messages.ResponseDto;
import com.example.mutsasns.global.messages.SystemMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/articles")
public class ArticleController {
    private final ArticleService articleService;
    private final ArticleImageService imageService;

    @PostMapping
    public ResponseEntity<ResponseDto> create(
            @RequestPart(required = false) List<MultipartFile> requestImages,
            @RequestPart RequestArticleDto dto,
            Authentication authentication
    ) throws IOException {
        String username = authentication.getName();
        imageService.createImage(requestImages, username, articleService.createArticle(dto, username));

        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseDto.getInstance(SystemMessage.REGISTER_ARTICLE));
    }

    @GetMapping
    public ResponseEntity<List<ResponseArticleListDto>> readAll(@RequestParam("username") String username) {
        return ResponseEntity.ok(articleService.readAllArticles(username));
    }

    @GetMapping("/{articleId}")
    public ResponseEntity<ResponseArticleDto> read(
            Authentication authentication,
            @PathVariable("articleId") Long articleId
    ) {
        String username = authentication.getName();

        return ResponseEntity.ok(articleService.readArticle(username, articleId));
    }

    @PutMapping("/{articleId}")
    public ResponseEntity<ResponseDto> update(
            @RequestPart(required = false) List<MultipartFile> requestImages,
            @RequestPart RequestArticleDto dto,
            @PathVariable("articleId") Long articleId,
            Authentication authentication
    ) throws IOException {
        String username = authentication.getName();

        // 조건에 따라 이미지 추가 및 삭제 진행한 것을 반환
        List<MultipartFile> newList = imageService.updateImg(requestImages, articleId);

        // 반환한 newList 로 로직 수행
        articleService.updateArticle(newList, dto, username, articleId);

        return ResponseEntity.ok(ResponseDto.getInstance(SystemMessage.UPDATE_ARTICLE));
    }

    @DeleteMapping("/{articleId}")
    public ResponseEntity<Void> delete(
            @PathVariable("articleId") Long articleId,
            Authentication authentication
    ) {
        String username = authentication.getName();
        articleService.deleteArticle(username, articleId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/followers")
    public ResponseEntity<List<ResponseArticleListDto>> readAllFollowersArticles(
            @RequestParam("username") String username
    ) {
        return ResponseEntity.ok(articleService.readAllArticlesByFollower(username));
    }
}
