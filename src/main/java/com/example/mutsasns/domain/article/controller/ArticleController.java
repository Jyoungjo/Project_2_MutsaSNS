package com.example.mutsasns.domain.article.controller;

import com.example.mutsasns.domain.article.dto.RequestArticleDto;
import com.example.mutsasns.domain.article.dto.ResponseArticleDto;
import com.example.mutsasns.domain.article.dto.ResponseArticleListDto;
import com.example.mutsasns.domain.article.service.ArticleService;
import com.example.mutsasns.domain.images.service.ArticleImageService;
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

        log.info("게시글 작성 성공");
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseDto.getInstance(SystemMessage.REGISTER_ARTICLE));
    }

    @GetMapping
    public ResponseEntity<List<ResponseArticleListDto>> readAll() {
        log.info("게시글 목록 조회 성공");
        return ResponseEntity.ok(articleService.readAllArticles());
    }

    @GetMapping("/{articleId}")
    public ResponseEntity<ResponseArticleDto> read(
            Authentication authentication,
            @PathVariable("articleId") Long articleId
    ) {
        String username = authentication.getName();

        log.info("게시글 단일 조회 성공");
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

        log.info("게시글 수정 성공");
        return ResponseEntity.ok(ResponseDto.getInstance(SystemMessage.UPDATE_ARTICLE));
    }

    @DeleteMapping("/{articleId}")
    public ResponseEntity<Void> delete(
            @PathVariable("articleId") Long articleId,
            Authentication authentication
    ) {
        String username = authentication.getName();
        articleService.deleteArticle(username, articleId);

        log.info("게시글 삭제 성공");
        return ResponseEntity.noContent().build();
    }
}
