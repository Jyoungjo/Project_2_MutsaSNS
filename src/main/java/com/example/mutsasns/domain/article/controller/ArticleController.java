package com.example.mutsasns.domain.article.controller;

import com.example.mutsasns.domain.article.dto.RequestArticleDto;
import com.example.mutsasns.domain.article.dto.ResponseArticleDto;
import com.example.mutsasns.domain.article.dto.ResponseArticleListDto;
import com.example.mutsasns.domain.article.service.ArticleService;
import com.example.mutsasns.domain.images.domain.ArticleImage;
import com.example.mutsasns.domain.images.dto.ImageDto;
import com.example.mutsasns.domain.images.service.ArticleImageService;
import com.example.mutsasns.global.messages.ResponseDto;
import com.example.mutsasns.global.messages.SystemMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users/{userId}/articles")
public class ArticleController {
    private final ArticleService articleService;
    private final ArticleImageService imageService;

    @PostMapping
    public ResponseEntity<ResponseDto> create(
            @RequestPart(required = false) List<MultipartFile> requestImages,
            @RequestPart RequestArticleDto dto,
            @PathVariable("userId") Long userId,
            Authentication authentication
    ) throws IOException {
        String username = authentication.getName();
        articleService.createArticle(requestImages, dto, username, userId);

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
            @RequestPart(required = false) List<MultipartFile> requestImages,
            @RequestPart RequestArticleDto dto,
            @PathVariable("userId") Long userId,
            @PathVariable("articleId") Long articleId,
            Authentication authentication
    ) throws IOException {
        String username = authentication.getName();

        // DB에 저장되어있던 이미지 리스트
        List<ArticleImage> dbImageList = imageService.getAllImagesByArticle(articleId);
        // 새로 전달된 이미지들을 저장할 리스트
        List<MultipartFile> newImgList = new ArrayList<>();

        // DB에 이미지가 존재하지 않을 때
        if (CollectionUtils.isEmpty(dbImageList)) {
            // 전달된 이미지가 하나라도 존재한다면 (이미지를 추가한다는 뜻)
            if (!CollectionUtils.isEmpty(requestImages)) {
                // 해당 이미지를 newImgList 에 추가
                newImgList.addAll(requestImages);
            }
        }
        // DB에 이미지가 하나라도 존재할 때
        else {
            // 전달된 이미지가 하나도 없다면 (현재 등록된 이미지들 중에 무언가 삭제했다는 뜻)
            if (CollectionUtils.isEmpty(requestImages)) {
                // 사진 삭제
                for (ArticleImage image : dbImageList) {
                    imageService.deleteImage(image.getId());
                }
            }
            // 전달된 이미지가 하나라도 있다면 (추가 및 삭제 여부 체크)
            else {
                // DB에 저장된 파일 원본명 저장할 리스트 생성
                List<String> dbOriginalImgNameList = new ArrayList<>();

                // DB 내 이미지 원본명 추출
                for (ArticleImage image : dbImageList) {
                    ImageDto imageDto = imageService.getImageById(image.getId());
                    String dbImgName = imageDto.getImageName();

                    // 전달된 파일들 중에 DB 내의 이미지 이름이 존재하지 않는다면
                    if (!requestImages.contains(dbImgName)) {
                        // 해당 파일을 삭제한다는 뜻이므로 삭제한다.
                        imageService.deleteImage(image.getId());
                    } else {
                        // 그게 아니라면 추가
                        dbOriginalImgNameList.add(dbImgName);
                    }
                }
                // 전달된 이미지들 체크
                for (MultipartFile reqImg : requestImages) {
                    String multipartOriginalName = reqImg.getOriginalFilename();
                    if (!dbOriginalImgNameList.contains(multipartOriginalName)) {
                        newImgList.add(reqImg);
                    }
                }
            }
        }
        // 새로 추가된 이미지들을 저장한 newImgList 로 로직 수행
        articleService.updateArticle(newImgList, dto, username, userId, articleId);

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
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ResponseDto.getInstance(SystemMessage.DELETE_ARTICLE));
    }
}
